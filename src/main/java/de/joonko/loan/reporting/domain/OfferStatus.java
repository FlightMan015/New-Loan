package de.joonko.loan.reporting.domain;

import de.joonko.loan.offer.api.LoanProvider;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class OfferStatus {

    private String userUUID;
    private String distributionChannelUUID;

    private OffsetDateTime bankAccountAddedAt;
    private OffsetDateTime personalDataAddedAt;

    private String purpose;
    private Integer loanAmountRequested;
    private OffsetDateTime loanAmountRequestedAt;

    private LoanProvider offerProvider;

    private OffsetDateTime offersReceivedAt;
    private OffsetDateTime offerAcceptedAt;

    private String kycStatus;
    private OffsetDateTime kycStatusLastUpdatedAt;

    private String offerStatus;
    private OffsetDateTime offerStatusLastUpdatedAt;
}
