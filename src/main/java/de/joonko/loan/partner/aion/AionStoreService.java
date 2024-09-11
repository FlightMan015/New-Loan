package de.joonko.loan.partner.aion;

import de.joonko.loan.offer.domain.BestLoanOffer;
import de.joonko.loan.partner.aion.model.BestOfferValue;
import de.joonko.loan.partner.aion.model.CreditApplicationResponseStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.InvalidObjectException;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@Service
@Slf4j
@RequiredArgsConstructor
public class AionStoreService {

    private final AionCreditApplicationResponseStoreRepository repository;

    public Mono<CreditApplicationResponseStore> saveCreditApplicationResponse(final CreditApplicationResponseStore responseStore) {
        return findByApplicationId(responseStore.getApplicationId())
                .map(optionalAionResponse -> optionalAionResponse.
                        map(existingAionResponse -> {
                            existingAionResponse.getVariables().addAll(ofNullable(responseStore.getVariables()).orElse(List.of()));
                            existingAionResponse.getOffersProvided().addAll(ofNullable(responseStore.getOffersProvided()).orElse(List.of()));
                            return existingAionResponse;
                        })
                        .orElse(responseStore)
                )
                .flatMap(this::save);
    }

    public Mono<Optional<CreditApplicationResponseStore>> findByApplicationId(final String applicationId) {
        return Mono.fromCallable(() -> repository.findByApplicationId(applicationId))
                .map(list -> list.stream().findFirst())
                .subscribeOn(Schedulers.elastic());
    }

    public Mono<CreditApplicationResponseStore> findByProcessId(final String processId) {
        return Mono.fromCallable(() -> repository.findByProcessId(processId))
                .filter(list -> !list.isEmpty())
                .map(list -> list.get(0))
                .subscribeOn(Schedulers.elastic());
    }

    public Mono<CreditApplicationResponseStore> addRepresentativeId(final String applicationId, final String representativeId) {
        return findByApplicationId(applicationId)
                .map(Optional::get)
                .map(creditApplicationResponse -> {
                    creditApplicationResponse.setRepresentativeId(representativeId);
                    return creditApplicationResponse;
                }).flatMap(this::save);
    }

    public Mono<CreditApplicationResponseStore> addBestOffers(final String applicationId, final List<BestLoanOffer> offersToBeat) {
        return findByApplicationId(applicationId)
                .flatMap(optionalAionResponse -> optionalAionResponse.map(existingAionResponse ->
                                Mono.just(existingAionResponse.toBuilder()
                                        .offersToBeat(offersToBeat)
                                        .build())
                        ).orElse(Mono.error(() -> new InvalidObjectException(String.format("Aion: No process found for applicationId - %s", applicationId))))
                )
                .flatMap(this::save);
    }

    public Mono<CreditApplicationResponseStore> addOffersProvided(final String applicationId, final List<BestOfferValue> offersToBeat) {
        return findByApplicationId(applicationId)
                .flatMap(optionalAionResponse -> optionalAionResponse.map(existingAionResponse ->
                                Mono.just(existingAionResponse.toBuilder()
                                        .offersProvided(offersToBeat)
                                        .build())
                        ).orElse(Mono.error(() -> new InvalidObjectException(String.format("Aion: No process found for applicationId - %s", applicationId))))
                )
                .flatMap(this::save);
    }

    private Mono<CreditApplicationResponseStore> save(final CreditApplicationResponseStore responseStore) {
        return Mono.fromCallable(() -> repository.save(responseStore))
                .subscribeOn(Schedulers.elastic());
    }
}
