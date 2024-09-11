package de.joonko.loan.partner.santander;

import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.dac.fts.FTSAccountSnapshotGateway;
import de.joonko.loan.data.support.DataSupportService;
import de.joonko.loan.db.service.LoanApplicationAuditTrailService;
import de.joonko.loan.db.service.LoanDemandStoreService;
import de.joonko.loan.offer.domain.LoanDuration;
import de.joonko.loan.partner.santander.model.SantanderOffer;
import de.joonko.loan.partner.santander.stub.FaultException;
import de.joonko.loan.partner.santander.stub.ScbCapsBcoWSStub;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@ConditionalOnProperty(
        value = "santander.enabled",
        havingValue = "true"
)
public class SantanderLoanDemandGateway extends SantanderBaseLoanDemandGateway {

    public SantanderLoanDemandGateway(
            SantanderLoanProviderApiMapper mapper,
            SantanderStoreService santanderStoreService,
            LoanApplicationAuditTrailService loanApplicationAuditTrailService,
            DataSupportService dataSupportService,
            LoanDemandStoreService loanDemandStoreService,
            FTSAccountSnapshotGateway ftsAccountSnapshotGateway,
            SantanderPrecheckFilter precheckFilter,
            SantanderClientApi santanderClientApi) {
        super(mapper, santanderStoreService, loanApplicationAuditTrailService, dataSupportService, loanDemandStoreService, ftsAccountSnapshotGateway, precheckFilter,santanderClientApi);
    }

    @Override
    public Mono<List<ScbCapsBcoWSStub.GetKreditvertragsangebotResponse>> callApi(ScbCapsBcoWSStub.GetKreditvertragsangebot getKreditvertragsangebot, String applicationId) {
        loanApplicationAuditTrailService.sendingLoanDemandRequest(applicationId, Bank.SANTANDER);
        String ftsTransactionId = getLoanDemandStore(applicationId).getFtsTransactionId();
        log.info("SANTANDER: Sending loan Demand request for applicationId: {}", applicationId);
        try {
            ScbCapsBcoWSStub.KreditantragXO creditOffer = getKreditvertragsangebot.getGetKreditvertragsangebot().getKreditantrag();
            Date professionEndDate = creditOffer.getDarlehnsnehmer().getAktuellesBV().getBeschaeftigtBis();
            BigInteger duration = creditOffer.getFinanzierung().getLaufzeitInMonaten();

            if (precheckFilter.doesContractEndBeforeRepayment(professionEndDate, LoanDuration.fromNumber(duration.intValue()), applicationId)) {
                return Mono.empty();
            }
            log.info("SANTANDER: Fetching loan offer for applicationId: {}, duration: {}", applicationId, duration);
            return getSingleOffer(getKreditvertragsangebot, ftsTransactionId, applicationId)
                    .doOnSuccess(offer -> santanderStoreService.saveOffer(
                            SantanderOffer.builder()
                                    .applicationId(applicationId)
                                    .kreditOffer(buildOfferResponse(offer))
                                    .build()
                    ))
                    .doOnSuccess(offer -> pushRedOffersReceivedToDataAnalytics(applicationId, offer))
                    .doOnSuccess(offer -> loanApplicationAuditTrailService.receivedLoanDemanResponseSantander(applicationId, offer))
                    .doOnSuccess(offer -> {
                        ScbCapsBcoWSStub.AntragstatusType status = offer.getGetKreditvertragsangebotResponse().getAntragsstatus().getStatus();
                        String scbAntragId = offer.getGetKreditvertragsangebotResponse().getAntragsstatus().getScbAntragId();
                        if (status.equals(ScbCapsBcoWSStub.AntragstatusType.GENEHMIGT)) {
                            loanApplicationAuditTrailService.receivedAsGreenProfileSantander(applicationId, status, scbAntragId, duration);
                        } else {
                            loanApplicationAuditTrailService.receivedAsRedProfileSantander(applicationId, status, scbAntragId, duration);
                            throw new RuntimeException("SANTANDER: Red profile. Dont trigger getOffers for other durations");
                        }
                    })
                    .doOnError(throwable -> loanApplicationAuditTrailService.receivedLoanDemandResponseError(applicationId, throwable.getMessage(), Bank.SANTANDER))
                    .filter(offer -> offer.getGetKreditvertragsangebotResponse().getAntragsstatus().getStatus().equals(ScbCapsBcoWSStub.AntragstatusType.GENEHMIGT))
                    .flatMap(offer -> Mono.just(List.of(offer)));
        } catch (Exception e) {
            log.error("SANTANDER: Failed to get offer for applicationId: {}", applicationId, e);
            loanApplicationAuditTrailService.saveApplicationError(applicationId, e.getMessage(), Bank.SANTANDER.label);
            return Mono.empty();
        }
    }

    private Mono<ScbCapsBcoWSStub.GetKreditvertragsangebotResponse> getSingleOffer(ScbCapsBcoWSStub.GetKreditvertragsangebot getKreditvertragsangebot, String ftsTransactionId, String applicationId) throws FaultException, RemoteException {
        ScbCapsBcoWSStub.GetKreditvertragsangebotResponse kreditvertragsangebot = santanderClientApi.getOffer(getKreditvertragsangebot);

        String scbAntragId = kreditvertragsangebot.getGetKreditvertragsangebotResponse().getAntragsstatus().getScbAntragId();
        ScbCapsBcoWSStub.AntragstatusType status = kreditvertragsangebot.getGetKreditvertragsangebotResponse().getAntragsstatus().getStatus();
        if (status.equals(ScbCapsBcoWSStub.AntragstatusType.GENEHMIGT)) {
            setDocument(ftsTransactionId, applicationId, scbAntragId);
        }
        return Mono.just(kreditvertragsangebot);
    }
}
