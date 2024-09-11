package de.joonko.loan.customer.support;

import de.joonko.loan.config.CustomerSupportConfig;
import de.joonko.loan.customer.support.exception.AcceptOfferEventException;
import de.joonko.loan.customer.support.exception.CustomerSupportEventException;
import de.joonko.loan.customer.support.exception.OfferReceivedEventException;
import de.joonko.loan.customer.support.exception.UserCreationException;
import de.joonko.loan.customer.support.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
        value = "customer.support.enabled",
        havingValue = "true",
        matchIfMissing = true)
public class CustomerSupportGatewayImpl implements CustomerSupportGateway {

    @Qualifier("intercomWebClient")
    private final WebClient intercomWebClient;

    private final CustomerSupportConfig customerSupportConfig;

    @Override
    public void createUser(User user) {
        intercomWebClient.post()
                .uri(customerSupportConfig.getUserEndpoint())
                .bodyValue(user)
                .retrieve()
                .onStatus(httpStatus -> {
                    if (!HttpStatus.OK.equals(httpStatus)) {
                        throw new UserCreationException("Error creating user in customer support service");
                    }
                    return true;
                }, clientResponse -> Mono.empty())
                .bodyToMono(IntercomEventResponse.class)
                .block();
    }

    @Override
    public void pushOfferAcceptedEvent(OfferAcceptedEvent offerAcceptedEvent) {
        intercomWebClient.post()
                .uri(customerSupportConfig.getEventEndpoint() + customerSupportConfig.getEventOfferAcceptedEndpoint())
                .bodyValue(offerAcceptedEvent)
                .retrieve()
                .onStatus(httpStatus -> {
                    if (!HttpStatus.NO_CONTENT.equals(httpStatus)) {
                        throw new AcceptOfferEventException("Failed to push accept offer event in customer support service");
                    }
                    return true;
                }, clientResponse -> Mono.empty())
                .bodyToMono(IntercomEventResponse.class)
                .block();
    }

    @Override
    public void pushOffersReceivedEvent(OfferReceivedEvent offerReceivedEvent) {
        intercomWebClient.post()
                .uri(customerSupportConfig.getEventEndpoint() + customerSupportConfig.getEventOfferEndpoint())
                .bodyValue(offerReceivedEvent)
                .retrieve()
                .onStatus(httpStatus -> {
                    if (!HttpStatus.OK.equals(httpStatus) && !HttpStatus.NO_CONTENT.equals(httpStatus)) {
                        throw new OfferReceivedEventException("Failed to push offers received event in customer support service");
                    }
                    return true;
                }, clientResponse -> Mono.empty())
                .bodyToMono(IntercomEventResponse.class)
                .block();
    }

    @Override
    public void pushKycStatus(KycStatusEvent kycStatusEvent) {
        intercomWebClient.post()
                .uri(customerSupportConfig.getEventEndpoint() + customerSupportConfig.getEventKycStatus())
                .bodyValue(kycStatusEvent)
                .retrieve()
                .onStatus(httpStatus -> {
                    if (!HttpStatus.NO_CONTENT.equals(httpStatus)) {
                        throw new AcceptOfferEventException("Failed to push kyc status event in customer support service");
                    }
                    return true;
                }, clientResponse -> Mono.empty())
                .bodyToMono(IntercomEventResponse.class)
                .block();
    }

    @Override
    public void pushApplicationAuditEvent(ApplicationAuditEvent applicationAuditEvent) {

        intercomWebClient.post()
                .uri(customerSupportConfig.getEventEndpoint() + customerSupportConfig.getEventApplicationAuditEndpoint())
                .bodyValue(applicationAuditEvent)
                .retrieve()
                .onStatus(httpStatus -> {
                    if (!HttpStatus.NO_CONTENT.equals(httpStatus)) {
                        throw new AcceptOfferEventException("Failed to push application audit event in customer support service");
                    }
                    return true;
                }, clientResponse -> Mono.empty())
                .bodyToMono(IntercomEventResponse.class)
                .block();
    }

    @Override
    public void pushEvent(Event event, String uri) {
        intercomWebClient.post()
                .uri(uri)
                .bodyValue(event)
                .retrieve()
                .onStatus(httpStatus -> {
                    if (!HttpStatus.OK.equals(httpStatus) && !HttpStatus.NO_CONTENT.equals(httpStatus)) {
                        throw new CustomerSupportEventException("Failed to push event in customer support service: " + uri + " : " + event);
                    }
                    return true;
                }, clientResponse -> Mono.empty())
                .bodyToMono(IntercomEventResponse.class)
                .block();
    }
}
