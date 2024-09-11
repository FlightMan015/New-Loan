package de.joonko.loan.integrations.domain.integrationhandler.userpersonal.provider;

import de.joonko.loan.integrations.domain.integrationhandler.userpersonal.UserPersonalData;
import de.joonko.loan.integrations.domain.integrationhandler.userpersonal.UserPersonalDataMerger;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class UserPersonalDataAggregatorImpl implements UserPersonalDataAggregator {

    private final UserPersonalDataProvider userManagementService;
    private final UserPersonalDataProvider segmentService;
    private final UserPersonalDataMerger userPersonalDataMerger;

    @Override
    public Mono<UserPersonalData> getUserPersonalData(String userUuid) {
        return Mono.just(userUuid)
                .flatMap(userManagementService::getUserPersonalData)
                .doOnError(ex -> log.error("Failed getting user personal data from user management service for userId: {}", userUuid, ex))
                .zipWhen(userPersonal -> segmentService.getUserPersonalData(userPersonal.getContactData().getEmail())
                        .doOnError(ex -> log.error("Failed getting user personal data from segment service for userId: {}", userUuid, ex))
                        .onErrorReturn(new UserPersonalData()), List::of)
                .map(userPersonalDataMerger::merge);
    }
}
