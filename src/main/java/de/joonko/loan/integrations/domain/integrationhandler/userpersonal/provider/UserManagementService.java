package de.joonko.loan.integrations.domain.integrationhandler.userpersonal.provider;

import de.joonko.loan.integrations.domain.integrationhandler.userpersonal.UserPersonalData;
import de.joonko.loan.user.service.UserPersonalInformationStore;
import io.fusionauth.domain.User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@AllArgsConstructor
@Service
public class UserManagementService implements UserPersonalDataProvider {

    private final AuthConnector authConnector;
    private final UserPersonalDataMapper userPersonalDataMapper;

    @Override
    public Mono<UserPersonalData> getUserPersonalData(String userUuid) {
        return Mono.just(userUuid)
                .flatMap(this::getUserData)
                .map(userPersonalDataMapper::fromUserData);
    }

    public Mono<Void> updateUserPersonalData(UserPersonalInformationStore userPersonalInfo) {
        return Mono.fromRunnable(() -> authConnector.updateUserData(userPersonalInfo))
                .subscribeOn(Schedulers.elastic())
                .then();
    }

    private Mono<User> getUserData(String userUuid) {
        return Mono.fromCallable(() -> authConnector.getUserData(userUuid))
                .doOnNext(user -> log.debug("Got user data from management service for userId: {}", userUuid))
                .doOnError(ex -> log.error("Failed getting data for userId: {}", userUuid, ex))
                .subscribeOn(Schedulers.elastic());
    }
}
