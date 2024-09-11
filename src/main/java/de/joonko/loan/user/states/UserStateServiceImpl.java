package de.joonko.loan.user.states;

import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@AllArgsConstructor
@Service
public class UserStateServiceImpl implements UserStateService {

    private final UserStatesStoreService userStatesStoreService;

    @Override
    public Mono<UserStatesStore> saveUserStates(UserStatesStore userState) {
        log.info("saving userStates for userId: {}", userState.getUserUUID());

        return Mono.just(userState)
                .flatMap(userStatesStoreService::save)
                .doOnError(e -> log.error("Error on saveUserStates for userId: {}", userState.getUserUUID(), e));
    }

    @Override
    public Mono<UserStatesStore> saveUserPersonalDataState(final String userId, StateDetails userPersonalDataState) {
        log.info("saving userPersonalDataState for userId: {}", userId);

        return Mono.just(userId)
                .flatMap(userStatesStoreService::findById)
                .map(userStatesStore -> {
                    userStatesStore.setUserPersonalInformationStateDetails(userPersonalDataState);
                    return userStatesStore;
                }).flatMap(userStatesStoreService::save)
                .doOnError(e -> log.error("Error on saveUserPersonalDataStates for userId: {}", userId, e));
    }

    @Override
    public Mono<UserStatesStore> saveUpdatedOffersStates(final String userId, @NotNull Set<OfferDataStateDetails> updatedOffersStates) {
        log.info("saving updated offersStates for userId: {}", userId);

        return Mono.just(updatedOffersStates)
                .filter(offersStates -> !offersStates.isEmpty())
                .flatMap(offersStates -> userStatesStoreService.findById(userId))
                .map(userStatesStore -> {
                    updatedOffersStates.forEach(offersState -> updateResponseDateTimeAndState(userStatesStore, offersState));
                    return userStatesStore;
                }).flatMap(userStatesStoreService::save)
                .doOnError(e -> log.error("Error on saveUpdatedOffersStates for userId: {}", userId, e));
    }

    @Override
    public Mono<UserStatesStore> addOffersStates(final String userId, @NotNull Set<OfferDataStateDetails> newOffersStates) {
        log.info("adding offersStates for userId: {}", userId);

        return Mono.just(newOffersStates)
                .filter(offersStates -> !offersStates.isEmpty())
                .flatMap(offersStates -> userStatesStoreService.findById(userId))
                .map(userStatesStore -> addOffers(userStatesStore, newOffersStates))
                .flatMap(userStatesStoreService::save)
                .doOnError(e -> log.error("Error on addOffersStates for userId: {}", userId, e));
    }

    private UserStatesStore addOffers(final UserStatesStore userStatesStore, final Set<OfferDataStateDetails> newOffersStates) {
        newOffersStates.forEach(userStatesStore::add);
        return userStatesStore;
    }

    private Optional<UserStatesStore> updateResponseDateTimeAndState(UserStatesStore userStatesStore, OfferDataStateDetails updatedOfferState) {
        return userStatesStore.getOfferDateStateDetailsSet().stream()
                .filter(offersState -> Objects.equals(updatedOfferState.getApplicationId(), offersState.getApplicationId()))
                .findFirst()
                .map(offersState -> {
                    offersState.setResponseDateTime(updatedOfferState.getResponseDateTime());
                    offersState.setState(updatedOfferState.getState());
                    return userStatesStore;
                });
    }
}
