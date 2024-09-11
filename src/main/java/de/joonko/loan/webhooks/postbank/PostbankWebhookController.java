package de.joonko.loan.webhooks.postbank;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import de.joonko.loan.exception.GenericExceptionHandler;
import de.joonko.loan.webhooks.postbank.model.PostbankOfferResponseEnvelope;
import de.joonko.loan.webhooks.postbank.model.PostbankRequestError;
import de.joonko.loan.webhooks.postbank.model.PostbankWebhookResponse;
import de.joonko.loan.webhooks.postbank.model.PostbankWebhookResponseEnvelope;
import de.joonko.loan.webhooks.postbank.model.PostbankWebhookResponseEnvelopeBody;
import de.joonko.loan.webhooks.postbank.model.PostbankWebhookResponseEnvelopeWrapper;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.ws.rs.core.MediaType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
@RequiredArgsConstructor
@GenericExceptionHandler
public class PostbankWebhookController {

    private final PostbankWebhookService postbankWebhookService;

    private final XmlMapper xmlMapper = new XmlMapper();

    @PostMapping(value = "/loan/postbank/offers-notification", consumes = MediaType.TEXT_XML, produces = MediaType.TEXT_XML)
    public Mono<ResponseEntity<String>> handlePostBankOfferWebHookNotification(@RequestBody @Valid String requestBody) {
        logPostbankWebhookRequestWithoutSensitiveInfo(requestBody);
        return deserializeXml(requestBody)
                .map(postbankOfferResponseEnvelope -> postbankOfferResponseEnvelope.getBody().getUpdate().getArg0().getCreditResultWithContracts())
                .doOnNext(creditResult -> log.info("POSTBANK: Received offer webhook for contractNumber - {}, applicationId - {}", creditResult.getContractNumber(), creditResult.getPartnerContractNumber()))
                .flatMap(postbankWebhookService::savePostbankOfferResponse)
                .map(ignore -> ResponseEntity.ok().body(constructSuccessResponse()))
                .onErrorResume(err -> Mono.just(ResponseEntity.ok().body(constructErrorResponse())));
    }

    private void logPostbankWebhookRequestWithoutSensitiveInfo(String requestBody) {
        final var replacedStringForLog = requestBody
                .replaceAll("<contract>[\\s\\S]*?</contract>", "")
                .replaceAll("<contractShort>[\\s\\S]*?</contractShort>", "")
                .replaceAll("<creditResultAuthentication>[\\s\\S]*?</creditResultAuthentication>", "");
        log.info("POSTBANK: received xml for webhook: {}", replacedStringForLog);
    }

    private Mono<PostbankOfferResponseEnvelope> deserializeXml(final String requestBody) {
        try {
            return Mono.just(xmlMapper.readValue(requestBody, PostbankOfferResponseEnvelope.class));
        } catch (final JsonProcessingException e) {
            log.error("POSTBANK: Unable to process request", e);
            return Mono.error(e);
        }
    }

    public String constructErrorResponse() {
        final var response = PostbankWebhookResponseEnvelopeWrapper.builder()
                .body(PostbankWebhookResponseEnvelopeBody.builder()
                        .responseEnvelope(
                                PostbankWebhookResponseEnvelope.builder()
                                        .response(PostbankWebhookResponse.builder()
                                                .success(false)
                                                .error(PostbankRequestError.builder()
                                                        .code(-1)
                                                        .description("general error")
                                                        .build())
                                                .build())
                                        .build())
                        .build())
                .build();
        final var xmlResponse = serializeResponse(response);
        log.info("POSTBANK: Webhook response is {}", xmlResponse);

        return xmlResponse;
    }

    public String constructSuccessResponse() {
        final var response = PostbankWebhookResponseEnvelopeWrapper.builder()
                .body(PostbankWebhookResponseEnvelopeBody.builder()
                        .responseEnvelope(PostbankWebhookResponseEnvelope.builder()
                                .response(PostbankWebhookResponse.builder()
                                        .success(true)
                                        .build())
                                .build())
                        .build())
                .build();

        final var xmlResponse = serializeResponse(response);
        log.info("POSTBANK: Webhook response is {}", xmlResponse);

        return xmlResponse;
    }

    private String serializeResponse(final PostbankWebhookResponseEnvelopeWrapper response) {
        try {
            return xmlMapper.writeValueAsString(response);
        } catch (final JsonProcessingException e) {
            log.error("POSTBANK: Unable to serialize response", e);
            return "";
        }
    }
}
