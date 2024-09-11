package de.joonko.loan.integrations.model;

import de.joonko.loan.offer.OfferDemandRequest;
import de.joonko.loan.util.ClientIPService;
import lombok.*;

import java.net.InetAddress;

@Getter
@Data
@Builder
@AllArgsConstructor
public class OfferRequest {
    private final String userUUID;
    private final Long bonifyUserId;
    private final Integer requestedAmount;
    private final String requestedPurpose;
    private final boolean isRequestedBonifyLoans;
    private final String clientIp;
    private final String countryCode;

    @Setter
    private UserState userState;

    public OfferRequest(final OfferDemandRequest offerDemandRequest, final Long bonifyUserId) {
        this.userUUID = offerDemandRequest.getUserUUID();
        this.bonifyUserId = bonifyUserId;
        this.requestedAmount = offerDemandRequest.getRequestedLoanAmount();
        this.requestedPurpose = offerDemandRequest.getRequestedLoanPurpose();
        this.isRequestedBonifyLoans = offerDemandRequest.isOnlyBonify();
        this.clientIp = offerDemandRequest.getInetAddress().map(InetAddress::getHostAddress).orElse("");
        this.countryCode = offerDemandRequest.getInetAddress().map(ClientIPService::getCountry).orElse("");
    }
}
