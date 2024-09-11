package de.joonko.loan.partner.auxmoney;

import de.joonko.loan.partner.auxmoney.model.CorrelationDataRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Slf4j
@Component
public class AuxmoneyPushNotificationGateway {

    @Qualifier("auxmoneyWebClient")
    private final WebClient auxmoneyWebClient;

    public Mono<ResponseEntity<Void>> sendCorrelationData(CorrelationDataRequest correlationRequestData) {
        log.info("Sending Correlation Data to Auxmoney {}", correlationRequestData);
        return auxmoneyWebClient
                .post()
                .uri("/partner/esign/joonko/webhook/init")
                .bodyValue(correlationRequestData)
                .retrieve()
                .toBodilessEntity();
    }

}
