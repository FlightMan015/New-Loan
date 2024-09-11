package de.joonko.loan.webhooks.aion;

import de.joonko.loan.exception.GenericExceptionHandler;
import de.joonko.loan.webhooks.aion.model.AionWebhookRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@Slf4j
@RequiredArgsConstructor
@GenericExceptionHandler
public class AionWebHookController {

    private final AionWebHookService aionWebHookService;

    @PostMapping("/loan/aion/webhook")
    public Mono<ResponseEntity> handleAionWebHookNotification(@RequestBody @Valid AionWebhookRequest aionWebhookRequest) {
        return Mono.just(aionWebhookRequest)
                .doOnNext(request -> log.info("Received webhook with processId: {}, type: {}, status: {}, info: {}", request.getPayload().getProcessInstanceId(), request.getType(), request.getPayload().getStatus(), request.getPayload().getCustomerInfo()))
                .flatMap(aionWebHookService::save)
                .map(ignore -> ResponseEntity.ok().build());
    }
}
