package de.joonko.loan.integrations.domain.integrationhandler.userpersonal.provider;

import de.joonko.loan.integrations.domain.integrationhandler.userpersonal.UserPersonalData;
import reactor.core.publisher.Mono;

public interface UserPersonalDataAggregator {
    Mono<UserPersonalData> getUserPersonalData(String userUuid);
}
