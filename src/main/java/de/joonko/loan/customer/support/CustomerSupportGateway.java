package de.joonko.loan.customer.support;

import de.joonko.loan.customer.support.model.*;

public interface CustomerSupportGateway {
    public void createUser(User user);

    public void pushOfferAcceptedEvent(OfferAcceptedEvent offerAcceptedEvent);

    public void pushOffersReceivedEvent(OfferReceivedEvent offerReceivedEvent);

    public void pushKycStatus(KycStatusEvent kycStatusEvent);

    public void pushApplicationAuditEvent(ApplicationAuditEvent applicationAuditEvent);

    public void pushEvent(Event event, String uri);
}
