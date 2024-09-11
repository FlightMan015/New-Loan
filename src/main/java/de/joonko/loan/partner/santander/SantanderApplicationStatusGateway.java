package de.joonko.loan.partner.santander;

import de.joonko.loan.acceptoffer.domain.LoanApplicationStatusGateway;
import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.db.service.LoanApplicationAuditTrailService;
import de.joonko.loan.db.service.LoanOfferStoreService;
import de.joonko.loan.db.vo.LoanOfferStore;
import de.joonko.loan.partner.santander.stub.ScbCapsBcoWSStub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

@Component
@Slf4j
@ConditionalOnProperty(
        value = "santander.enabled",
        havingValue = "true",
        matchIfMissing = true)
@RequiredArgsConstructor
public class SantanderApplicationStatusGateway implements LoanApplicationStatusGateway<SantanderLoanApplicationStatusApiMapper, ScbCapsBcoWSStub.GetKreditantragsstatusParams, ScbCapsBcoWSStub.GetKreditantragsstatusResult> {

    @Autowired
    private final SantanderLoanApplicationStatusApiMapper mapper;

    @Autowired
    private final LoanOfferStoreService loanOfferStoreService;

    @Autowired
    private final SantanderStoreService santanderStoreService;

    @Autowired
    private final SantanderClientApi santanderClientApi;

    @Autowired
    private final LoanApplicationAuditTrailService loanApplicationAuditTrailService;

    @Override
    public SantanderLoanApplicationStatusApiMapper getMapper() {
        return mapper;
    }

    @Override
    public Mono<ScbCapsBcoWSStub.GetKreditantragsstatusResult> callApi(final ScbCapsBcoWSStub.GetKreditantragsstatusParams getKreditantragsstatusParams, final String loanApplicationId, final String loanOfferId) {
        return Mono.just(getKreditantragsstatusParams)
                .map(request -> {
                    request.setScbAntragId(getLoanProviderReferenceNumber(loanApplicationId, loanOfferId));
                    return request;
                })
                .flatMap(request -> requestApplicationStatus(request, loanApplicationId));
    }

    @Override
    public String getLoanProviderReferenceNumber(String loanApplicationId, String loanOfferId) {
        LoanOfferStore acceptedOffer = loanOfferStoreService.findByLoanOfferId(loanOfferId);
        return santanderStoreService.getScbAntragId(loanApplicationId, acceptedOffer.getOffer().getDurationInMonth());
    }

    @Override
    public Bank getBank() {
        return Bank.SANTANDER;
    }

    private Mono<ScbCapsBcoWSStub.GetKreditantragsstatusResult> requestApplicationStatus(ScbCapsBcoWSStub.GetKreditantragsstatusParams request, String applicationId) {
        return Mono.fromCallable(() ->
        {
            try {
                return santanderClientApi.getApplicationStatus(request);
            } catch (Exception ex) {
                log.error("SANTANDER: Failed to get offer status for applicationId: {}, exception - {}", applicationId, ex.getMessage());
                loanApplicationAuditTrailService.saveApplicationError(applicationId, ex.getMessage(), Bank.SANTANDER.label);
                return null;
            }
        });
    }
}
