package de.joonko.loan.partner.santander;

import de.joonko.loan.acceptoffer.domain.AcceptOfferGateway;
import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.db.service.LoanOfferStoreService;
import de.joonko.loan.db.vo.LoanOfferStore;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RequiredArgsConstructor
public abstract class SantanderBaseAcceptOfferGateway implements AcceptOfferGateway<SantanderAcceptOfferApiMapper, SantanderAcceptOfferRequest, SantanderAcceptOfferResponse> {
    protected final SantanderAcceptOfferApiMapper acceptOfferApiMapper;

    protected final SantanderStoreService santanderStoreService;

    protected final LoanOfferStoreService loanOfferStoreService;

    @Override
    public SantanderAcceptOfferApiMapper getMapper() {
        return acceptOfferApiMapper;
    }

    @Override
    public Mono<String> getLoanProviderReferenceNumber(String loanApplicationId, String loanOfferId) {
        LoanOfferStore acceptedOffer = loanOfferStoreService.findByLoanOfferId(loanOfferId);
        return Mono.fromCallable(() -> santanderStoreService.getScbAntragId(loanApplicationId, acceptedOffer.getOffer().getDurationInMonth()))
                .subscribeOn(Schedulers.elastic());
    }

    @Override
    public Bank getBank() {
        return Bank.SANTANDER;
    }
}
