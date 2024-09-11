package de.joonko.loan.integrations.domain.integrationhandler.userpersonal.provider;

import de.joonko.loan.integrations.domain.integrationhandler.userpersonal.UserPersonalData;
import de.joonko.loan.integrations.segment.CustomerData;
import de.joonko.loan.integrations.segment.SegmentClient;
import de.joonko.loan.util.HttpUtil;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import static de.joonko.loan.util.HttpUtil.encodeValue;

@Slf4j
@AllArgsConstructor
@Service
public class SegmentService implements UserPersonalDataProvider {

    private final SegmentClient segmentClient;
    private final UserPersonalDataMapper userPersonalDataMapper;

    @Override
    public Mono<UserPersonalData> getUserPersonalData(String email) {
        return segmentClient.getUserTraits("email:" + encodeValue(email))
                .doOnNext(user -> log.debug("Got user data from segment for email: {}", email))
                .doOnError(ex -> log.error("Failed getting user data for email: {}", email, ex))
                .onErrorResume(throwable -> Mono.just(new CustomerData()))
                .map(userPersonalDataMapper::fromCustomerData);
    }
}
