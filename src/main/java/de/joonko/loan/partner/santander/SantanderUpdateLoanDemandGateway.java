package de.joonko.loan.partner.santander;

import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.dac.fts.FTSAccountSnapshotGateway;
import de.joonko.loan.data.support.DataSupportService;
import de.joonko.loan.db.service.LoanApplicationAuditTrailService;
import de.joonko.loan.db.service.LoanDemandStoreService;
import de.joonko.loan.offer.domain.LoanDuration;
import de.joonko.loan.partner.santander.model.SantanderOffer;
import de.joonko.loan.partner.santander.stub.ScbCapsBcoWSStub;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@ConditionalOnProperty(
        value = "santander.enabled",
        havingValue = "update"
)
public class SantanderUpdateLoanDemandGateway extends SantanderBaseLoanDemandGateway {

    public SantanderUpdateLoanDemandGateway(
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

            getKreditvertragsangebot.getGetKreditvertragsangebot().getKreditantrag().setScbAntragId("");

            List<LoanDuration> durations = getOptimumDurations(creditOffer.getFinanzierung().getKreditbetragNetto());

            Stream<ScbCapsBcoWSStub.GetKreditvertragsangebotResponse> responses = durations.stream()
                    .filter(duration -> !precheckFilter.doesContractEndBeforeRepayment(professionEndDate, duration, applicationId))
                    .map(duration -> {
                        ScbCapsBcoWSStub.GetKreditvertragsangebotResponse response = fetchSingleOffer(getKreditvertragsangebot, applicationId, duration);
                        String scbAntragId = response.getGetKreditvertragsangebotResponse().getAntragsstatus().getScbAntragId();
                        getKreditvertragsangebot.getGetKreditvertragsangebot().getKreditantrag().setScbAntragId(scbAntragId);
                        return response;
                    });

            return Flux.fromStream(responses)
                    .flatMap(offer -> Mono.just(offer)
                            .doOnSuccess(o -> processDownStreamOrders(o, applicationId)))
                    .filter(offer -> offer.getGetKreditvertragsangebotResponse().getAntragsstatus().getStatus().equals(ScbCapsBcoWSStub.AntragstatusType.GENEHMIGT))
                    .doOnError(throwable -> loanApplicationAuditTrailService.receivedLoanDemandResponseError(applicationId, throwable.getMessage(), Bank.SANTANDER))
                    .collectList()
                    .doOnNext(offers -> {
                        if (!offers.isEmpty()) {
                            String scbAntragId = getKreditvertragsangebot.getGetKreditvertragsangebot().getKreditantrag().getScbAntragId();
                            setDocument(ftsTransactionId, applicationId, scbAntragId);
                        }
                    });

        } catch (Exception e) {
            log.error("SANTANDER: Failed to get offer for applicationId: {}", applicationId, e);
            loanApplicationAuditTrailService.saveApplicationError(applicationId, e.getMessage(), Bank.SANTANDER.label);
            return Mono.empty();
        }
    }

    private void processDownStreamOrders(ScbCapsBcoWSStub.GetKreditvertragsangebotResponse offer, String applicationId) {
        santanderStoreService.saveOffer(SantanderOffer
                .builder()
                .applicationId(applicationId)
                .kreditOffer(buildOfferResponse(offer))
                .build());
        pushRedOffersReceivedToDataAnalytics(applicationId, offer);
        loanApplicationAuditTrailService.receivedLoanDemanResponseSantander(applicationId, offer);

        ScbCapsBcoWSStub.AntragstatusType status = offer.getGetKreditvertragsangebotResponse().getAntragsstatus().getStatus();
        String scbAntragId = offer.getGetKreditvertragsangebotResponse().getAntragsstatus().getScbAntragId();
        BigInteger duration = offer.getGetKreditvertragsangebotResponse().getAntragsstatus().getFinanzierung().getLaufzeitInMonaten();
        if (status.equals(ScbCapsBcoWSStub.AntragstatusType.GENEHMIGT)) {
            loanApplicationAuditTrailService.receivedAsGreenProfileSantander(applicationId, status, scbAntragId, duration);
        } else {
            loanApplicationAuditTrailService.receivedAsRedProfileSantander(applicationId, status, scbAntragId, duration);
        }

    }

    private ScbCapsBcoWSStub.GetKreditvertragsangebotResponse fetchSingleOffer(ScbCapsBcoWSStub.GetKreditvertragsangebot getKreditvertragsangebot, String applicationId, LoanDuration duration) {
        log.info("SANTANDER: Fetching loan offer for applicationId: {}, for duration: {}", applicationId, duration);
        getKreditvertragsangebot.getGetKreditvertragsangebot().getKreditantrag().getFinanzierung().setLaufzeitInMonaten(BigInteger.valueOf(duration.getValue()));
        try {
            return santanderClientApi.getOffer(getKreditvertragsangebot);
        } catch (Exception e) {
            loanApplicationAuditTrailService.saveApplicationError(applicationId, e.getMessage(), Bank.SANTANDER.label);
            log.error("SANTANDER: Failed to get offer for applicationId: {}, duration {}", applicationId, duration, e);
        }
        return null;
    }

    @Override
    public List<LoanDuration> getDurations(Integer loanAsked) {
        return List.of(LoanDuration.FORTY_EIGHT);
    }

    private List<LoanDuration> getOptimumDurations(BigDecimal kreditbetragNetto) {
        int loanAsked = kreditbetragNetto.intValue();
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
}
