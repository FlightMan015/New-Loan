package de.joonko.loan.partner.santander;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.common.domain.LoanProvider;
import de.joonko.loan.dac.fts.FTSAccountSnapshotGateway;
import de.joonko.loan.data.support.DataSupportService;
import de.joonko.loan.db.service.LoanApplicationAuditTrailService;
import de.joonko.loan.db.service.LoanDemandStoreService;
import de.joonko.loan.db.vo.LoanDemandStore;
import de.joonko.loan.exception.LoanDemandGatewayException;
import de.joonko.loan.offer.domain.DomainDefault;
import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.offer.domain.LoanDemandGateway;
import de.joonko.loan.offer.domain.LoanDuration;
import de.joonko.loan.partner.santander.model.GetKreditvertragsangebotResponse;
import de.joonko.loan.partner.santander.stub.ScbCapsBcoWSStub;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;

import javax.mail.util.ByteArrayDataSource;
import java.util.List;

@RequiredArgsConstructor
public abstract class SantanderBaseLoanDemandGateway implements LoanDemandGateway<SantanderLoanProviderApiMapper, ScbCapsBcoWSStub.GetKreditvertragsangebot, List<ScbCapsBcoWSStub.GetKreditvertragsangebotResponse>> {

    protected final SantanderLoanProviderApiMapper mapper;
    protected final SantanderStoreService santanderStoreService;
    protected final LoanApplicationAuditTrailService loanApplicationAuditTrailService;
    protected final DataSupportService dataSupportService;
    protected final LoanDemandStoreService loanDemandStoreService;
    protected final FTSAccountSnapshotGateway ftsAccountSnapshotGateway;
    protected final SantanderPrecheckFilter precheckFilter;
    protected final SantanderClientApi santanderClientApi;

    @Override
    public SantanderLoanProviderApiMapper getMapper() {
        return mapper;
    }

    @Override
    public LoanProvider getLoanProvider() {
        return LoanProvider.builder().name(Bank.SANTANDER.getLabel()).build();
    }

    @Override
    public Boolean filterGateway(LoanDemand loanDemand) {
        return !precheckFilter.test(loanDemand);
    }

    @Override
    public List<LoanDuration> getDurations(Integer loanAsked) {
        if (loanAsked >= 1000 && loanAsked < 5000)
            return List.of(LoanDuration.TWELVE, LoanDuration.TWENTY_FOUR, LoanDuration.THIRTY_SIX);
        else if (loanAsked >= 5000 && loanAsked < 10000)
            return List.of(LoanDuration.THIRTY_SIX, LoanDuration.FORTY_EIGHT, LoanDuration.SIXTY);
        else if (loanAsked >= 10000 && loanAsked < 20000)
            return List.of(LoanDuration.SIXTY, LoanDuration.SEVENTY_TWO, LoanDuration.EIGHTY_FOUR);
        else if (loanAsked >= 20000 && loanAsked <= 60000)
            return List.of(LoanDuration.SEVENTY_TWO, LoanDuration.EIGHTY_FOUR, LoanDuration.NINETY_SIX);
        else return List.of();
    }

    protected void pushRedOffersReceivedToDataAnalytics(String applicationId, ScbCapsBcoWSStub.GetKreditvertragsangebotResponse offer) {
        ScbCapsBcoWSStub.AntragstatusType status = offer.getGetKreditvertragsangebotResponse().getAntragsstatus().getStatus();
        ScbCapsBcoWSStub.HinweisXO[] hinweise = offer.getGetKreditvertragsangebotResponse().getAntragsstatus().getHinweise();
        String reasonForRejection = (hinweise != null && hinweise.length > 0) ? String.valueOf(hinweise[0].getText()) : "";

        if (!status.equals(ScbCapsBcoWSStub.AntragstatusType.GENEHMIGT)) {
            log.info("SANTANDER: pushing red offer for applicationId: {}, to data analytics: {}",  applicationId, status);
            ObjectMapper mapper = new ObjectMapper();
            try {
                dataSupportService.pushRedOffersReceivedTopic(applicationId, Bank.SANTANDER.toString(), reasonForRejection, status.getValue(), mapper.writeValueAsString(offer));
                log.info("SANTANDER: done pushing red offer for applicationId: {}, to data analytics: {}", applicationId, status);
            } catch (Exception e) {
                log.error("SANTANDER: failed to send red offers topic to data analytics team for applicationId: {}", applicationId, e);
            }
        }
    }

    @Async
    protected void setDocument(String ftsTransactionId, String applicationId, String scbAntragId) {
        try {
            log.info("SANTANDER: Sending DAC PDF for applicationId: {}, scbAntragId: {}", applicationId, scbAntragId);
            santanderClientApi.setDocument(getEncodedAccountSnapshot(applicationId, ftsTransactionId), scbAntragId);
            log.info("SANTANDER: Done sending DAC PDF for applicationId: {}, scbAntragId: {}", applicationId, scbAntragId);
        } catch (Exception exc) {
            log.info("SANTANDER: Failed to upload the snapshot applicationId: {}, scbAntragId: {}", applicationId, scbAntragId);
            loanApplicationAuditTrailService.saveApplicationError(applicationId, exc.getMessage(), Bank.SANTANDER.label);
            throw new LoanDemandGatewayException("SANTANDER: Snapshot upload failed for duration: " + ", with message: " + exc.getMessage());
        }
    }

    private ByteArrayDataSource getEncodedAccountSnapshot(String applicationId, String transactionId) {
        log.info("SANTANDER: Fetching accountSnapshot for applicationId: {}, transactionId: {}", applicationId, transactionId);
        try {
            return new ByteArrayDataSource(ftsAccountSnapshotGateway.getAccountSnapshot(transactionId, DomainDefault.FTS_QUERY_PARAM_VALUE_PDF).readAllBytes(), "application/pdf");
        } catch (Exception e) {
            log.error("SANTANDER: Failed fetching accountSnapshot for applicationId: {}, transactionId: {}", applicationId, transactionId, e);
            loanApplicationAuditTrailService.saveApplicationError(applicationId, e.getMessage(), Bank.SANTANDER.label);
            return null;
        }
    }

    protected LoanDemandStore getLoanDemandStore(String id) {
        return loanDemandStoreService.findById(id).orElseThrow(() -> new RuntimeException("applicationId not found " + id));
    }

    protected GetKreditvertragsangebotResponse buildOfferResponse(ScbCapsBcoWSStub.GetKreditvertragsangebotResponse offer) {
        return GetKreditvertragsangebotResponse.builder()
                .scbAntragId(offer.getGetKreditvertragsangebotResponse().getAntragsstatus().getScbAntragId())
                .antragstatusType(offer.getGetKreditvertragsangebotResponse().getAntragsstatus().getStatus().getValue())
                .duration(offer.getGetKreditvertragsangebotResponse().getAntragsstatus().getFinanzierung().getLaufzeitInMonaten().intValue())
                .build();
    }
}
