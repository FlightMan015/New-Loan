package de.joonko.loan.partner.postbank.model.store;

import de.joonko.loan.exception.PostBankException;
import de.joonko.loan.offer.ResourceNotFoundException;
import de.joonko.loan.webhooks.postbank.model.CreditResult;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostbankLoanDemandStoreService {

    private final PostbankLoanDemandStoreRepository repository;

    public Mono<PostbankLoanDemandStore> findByApplicationId(final String applicationId) {
        return Mono.fromCallable(() -> repository.findByApplicationId(applicationId))
                .filter(list -> !list.isEmpty())
                .map(list -> list.get(0))
                .subscribeOn(Schedulers.elastic());
    }

    public Mono<PostbankLoanDemandStore> save(final PostbankLoanDemandStore responseStore) {
        return Mono.fromCallable(() -> repository.save(responseStore))
                .subscribeOn(Schedulers.elastic());
    }

    public Mono<PostbankLoanDemandStore> addOffersResponse(final String applicationId, final CreditResult creditResult) {
        return findByApplicationId(applicationId)
                .switchIfEmpty(Mono.error(new PostBankException(String.format("POSTBANK: Could not find PostbankOfferStore for applicationId - %s", applicationId))))
                .map(response -> {
                    final var creditResults = response.getCreditResults();
                    creditResults.add(creditResult);
                    return response.toBuilder()
                            .creditResults(creditResults)
                            .build();
                })
                .flatMap(this::save)
                .doOnError(err -> log.error("POSTBANK: Error occurred when trying to save Postbank offer response for applicationId - {}", applicationId))
                .doOnSuccess(any -> log.info(String.format("POSTBANK: Successfully saved loan offer response from Postbank for applicationId - %s", applicationId)));
    }

}
