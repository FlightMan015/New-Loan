package de.joonko.loan.partner.santander;

import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.config.SantanderConfig;
import de.joonko.loan.db.service.LoanApplicationAuditTrailService;
import de.joonko.loan.db.service.LoanOfferStoreService;
import de.joonko.loan.db.vo.LoanOfferStore;
import de.joonko.loan.offer.domain.LoanDuration;
import de.joonko.loan.partner.santander.stub.FaultException;
import de.joonko.loan.partner.santander.stub.ScbCapsBcoWSStub;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.rmi.RemoteException;

@Component
@Slf4j
@ConditionalOnProperty(value = "santander.enabled", havingValue = "update", matchIfMissing = true)
public class SantanderUpdateAcceptOfferGateway extends SantanderBaseAcceptOfferGateway {
    private final LoanApplicationAuditTrailService loanApplicationAuditTrailService;
    private final SantanderConfig santanderConfig;

    public SantanderUpdateAcceptOfferGateway(
            SantanderAcceptOfferApiMapper acceptOfferApiMapper,
            SantanderStoreService santanderStoreService,
            LoanOfferStoreService loanOfferStoreService,
            LoanApplicationAuditTrailService loanApplicationAuditTrailService,
            SantanderConfig santanderConfig) {

        super(acceptOfferApiMapper, santanderStoreService, loanOfferStoreService);
        this.loanApplicationAuditTrailService = loanApplicationAuditTrailService;
        this.santanderConfig = santanderConfig;
    }

    @SneakyThrows
    @Override
    public Mono<SantanderAcceptOfferResponse> callApi(SantanderAcceptOfferRequest santanderAcceptOfferRequest, String applicationId, String offerId) {
        try {
            log.info("SANTANDER: Accepting offer for applicationId: {}, and offerId: {}", applicationId, offerId);

            ScbCapsBcoWSStub scbCapsBcoWSStub = new ScbCapsBcoWSStub(santanderConfig.getBcoEndpoint());
            ScbCapsBcoWSStub.GetKreditvertragsangebot getKreditvertragsangebot = santanderAcceptOfferRequest.getGetKreditvertragsangebot();
            setAuthentication(getKreditvertragsangebot);

            LoanOfferStore acceptedOffer = loanOfferStoreService.findByLoanOfferId(offerId);
            String scbAntragId = santanderStoreService.getScbAntragId(applicationId, acceptedOffer.getOffer().getDurationInMonth());

            ScbCapsBcoWSStub.GetKreditvertragsangebotResponse response1 = updateOffer(getKreditvertragsangebot, applicationId, scbCapsBcoWSStub, santanderAcceptOfferRequest.getDuration(), scbAntragId);
            ScbCapsBcoWSStub.AntragstatusType status = response1.getGetKreditvertragsangebotResponse().getAntragsstatus().getStatus();
            if (!status.equals(ScbCapsBcoWSStub.AntragstatusType.GENEHMIGT)) {
                throw new RuntimeException("Santander update offer call failed with a status " + status);
            }
        } catch (Exception e) {
            log.error("SANTANDER: Failed to get offer for applicationId: {}", applicationId, e);
            loanApplicationAuditTrailService.saveApplicationError(applicationId, e.getMessage(), Bank.SANTANDER.label);
            throw e;
        }

        return Mono.just(new SantanderAcceptOfferResponse());
    }

    private void setAuthentication(ScbCapsBcoWSStub.GetKreditvertragsangebot getKreditvertragsangebot) {
        ScbCapsBcoWSStub.AuthentisierungXO authentisierung = new ScbCapsBcoWSStub.AuthentisierungXO();
        authentisierung.setBenutzer(santanderConfig.getUsername());
        authentisierung.setPasswort(santanderConfig.getPassword());
        getKreditvertragsangebot.getGetKreditvertragsangebot().setAuthentisierung(authentisierung);
    }

    private ScbCapsBcoWSStub.GetKreditvertragsangebotResponse updateOffer(ScbCapsBcoWSStub.GetKreditvertragsangebot getKreditvertragsangebot, String applicationId, ScbCapsBcoWSStub scbCapsBcoWSStub, LoanDuration duration, String scbAntragId) throws FaultException, RemoteException {
        log.info("SANTANDER: accept offer update call: Fetching loan offer for applicationId: {}, duration: {}", applicationId, duration);
        getKreditvertragsangebot.getGetKreditvertragsangebot().getKreditantrag().setScbAntragId(scbAntragId);
        getKreditvertragsangebot.getGetKreditvertragsangebot().getKreditantrag().getFinanzierung().setLaufzeitInMonaten(BigInteger.valueOf(duration.getValue()));
        try {
            return scbCapsBcoWSStub.getKreditvertragsangebot(getKreditvertragsangebot);
        } catch (Exception e) {
            loanApplicationAuditTrailService.saveApplicationError(applicationId, e.getMessage(), Bank.SANTANDER.label);
            log.error("SANTANDER: accept offer update call: Failed to get offer for applicationId: {}", applicationId, e);
            throw e;
        }
    }
}
