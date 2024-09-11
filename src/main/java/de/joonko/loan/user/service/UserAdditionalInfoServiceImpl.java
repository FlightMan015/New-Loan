package de.joonko.loan.user.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Optional;

@AllArgsConstructor
@Service
public class UserAdditionalInfoServiceImpl implements UserAdditionalInfoService {

    private final UserAdditionalInformationRepository repository;

    @Override
    public Mono<UserAdditionalInformationStore> save(UserAdditionalInformationStore userAdditionalInformationStore) {
        return Mono.fromCallable(() -> repository.save(userAdditionalInformationStore))
                .subscribeOn(Schedulers.elastic());
    }

    @Override
    public Mono<UserAdditionalInformationStore> findById(String userUuid) {
        return Mono.fromCallable(() -> repository.findById(userUuid))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .subscribeOn(Schedulers.elastic());
    }

    @Override
    public Mono<UserAdditionalInformationStore> deleteById(final String userUuid) {
        return Mono.fromCallable(() -> repository.deleteByUserUUID(userUuid))
                .subscribeOn(Schedulers.elastic());
    }
}
