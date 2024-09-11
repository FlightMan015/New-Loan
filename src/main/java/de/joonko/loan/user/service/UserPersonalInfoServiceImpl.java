package de.joonko.loan.user.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Optional;

@AllArgsConstructor
@Service
public class UserPersonalInfoServiceImpl implements UserPersonalInfoService {

    private final UserPersonalInformationRepository repository;

    @Override
    public Mono<UserPersonalInformationStore> save(UserPersonalInformationStore userPersonalInformationStore) {
        return Mono.fromCallable(() -> repository.save(userPersonalInformationStore))
                .subscribeOn(Schedulers.elastic());
    }

    @Override
    public Mono<UserPersonalInformationStore> findById(final String userId) {
        return Mono.fromCallable(() -> repository.findById(userId))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .subscribeOn(Schedulers.elastic());
    }

    @Override
    public Mono<UserPersonalInformationStore> deleteById(final String userId) {
        return Mono.fromCallable(() -> repository.deleteByUserUUID(userId))
                .subscribeOn(Schedulers.elastic());
    }
}
