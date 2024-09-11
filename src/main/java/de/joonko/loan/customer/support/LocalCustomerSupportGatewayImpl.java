package de.joonko.loan.customer.support;

import de.joonko.loan.customer.support.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(
        value = "customer.support.enabled",
        havingValue = "false")
public class LocalCustomerSupportGatewayImpl implements CustomerSupportGateway {
    @Override
    public void createUser(User user) {
    }

    @Override
    public void pushOfferAcceptedEvent(OfferAcceptedEvent offerAcceptedEvent) {
    }

    @Override
    public void pushOffersReceivedEvent(OfferReceivedEvent offerReceivedEvent) {

    }

    @Override
    public void pushKycStatus(KycStatusEvent kycStatusEvent) {
    }

    @Override
    public void pushEvent(Event event, String uri) {
    }

    @Override
    public void pushApplicationAuditEvent(ApplicationAuditEvent applicationAuditEvent) {
    }
}
