package de.joonko.loan.integrations.segment;

import de.joonko.loan.metric.ApiMetric;
import de.joonko.loan.metric.model.ApiComponent;
import de.joonko.loan.metric.model.ApiName;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Component
public class SegmentClient {

    @Qualifier("segmentPersonasClient")
    private final WebClient segmentPersonasClient;
    private final SegmentPropertiesConfig propertiesConfig;
    private final ApiMetric apiMetric;

    public Mono<CustomerData> getUserTraits(String id) {
        return segmentPersonasClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/spaces/")
                        .path(propertiesConfig.getPersonasSpaceId())
                        .path("/collections/users/profiles/")
                        .path(id)
                        .path("/traits")
                        .queryParam("limit", 200)
                        .build())
                .retrieve()
                .onStatus(httpStatus -> {
                    apiMetric.incrementStatusCounter(httpStatus, ApiComponent.SEGMENT, ApiName.GET_USER_TRAITS);
                    return false;
                }, clientResponse -> Mono.empty())
                .bodyToMono(CustomerData.class)
                .doOnError(throwable -> log.error("Failed getting user traits for id: {}", id, throwable))
                .doOnSuccess(customerData -> log.info("Received user traits for id: {}", id));
    }
}
