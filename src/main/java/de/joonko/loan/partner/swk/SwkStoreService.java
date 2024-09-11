package de.joonko.loan.partner.swk;

import de.joonko.loan.partner.swk.model.SwkCreditApplicationOffer;
import de.joonko.loan.partner.swk.model.SwkOffer;
import de.joonko.loan.partner.swk.stub.CreditApplicationServiceStub;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SwkStoreService {


    private final SwkOfferRepository swkOfferRepository;
    private final SwkCreditApplicationStoreRepository swkCreditApplicationStoreRepository;

    public Flux<SwkOffer> deleteSwkOffer(final String applicationId) {
        return Mono.fromCallable(() -> swkOfferRepository.deleteByApplicationId(applicationId))
                .flatMapIterable(list -> list)
                .subscribeOn(Schedulers.elastic());
    }

    public Flux<SwkCreditApplicationOffer> deleteSwkCreditApplication(final String applicationId) {
        return Mono.fromCallable(() -> swkCreditApplicationStoreRepository.deleteByApplicationId(applicationId))
                .flatMapIterable(list -> list)
                .subscribeOn(Schedulers.elastic());
    }

    public void saveOffer(SwkOffer creditOffer) {
        log.info("Saving offer in SWK store for loanApplication Id  {} ", creditOffer.getApplicationId());
        swkOfferRepository.save(creditOffer);
    }

    public void saveCreditApplicationOffer(SwkCreditApplicationOffer creditOffer) {
        log.info("Saving credit application offer in SwkCreditApplicationOffer store for loanApplication Id  {} ", creditOffer.getApplicationId());
        swkCreditApplicationStoreRepository.save(creditOffer);
    }

    public String getCustomerNumber(String applicationId, Integer duration) {
        log.info("Finding customer Number for {} and duration {}", applicationId, duration);
        List<SwkOffer> creditOffer = swkOfferRepository.findByApplicationIdIs(applicationId);
        CreditApplicationServiceStub.CreditOffer swkOfferObject = creditOffer.stream()
                .filter(offer -> (offer.getCreditOffer()
                        .getDuration() != 0 && offer.getCreditOffer()
                        .getDuration() == duration))
                .filter(swkOffer -> StringUtils.isNotEmpty(swkOffer.getCreditOffer()
                        .getCustomerAccountNumber()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("SWK : Error in retrieving customerNumber "))
                .getCreditOffer();
        return swkOfferObject.getCustomerAccountNumber();
    }

    public List<SwkOffer> findByApplicationId(String applicationId) {
        return swkOfferRepository.findByApplicationIdIs(applicationId);
    }

    public List<SwkCreditApplicationOffer> findCredApplicationOfferByApplicationId(String applicationId) {
        return swkCreditApplicationStoreRepository.findByApplicationIdIs(applicationId);
    }

}
