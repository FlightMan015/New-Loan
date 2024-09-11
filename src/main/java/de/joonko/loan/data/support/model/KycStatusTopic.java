package de.joonko.loan.data.support.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KycStatusTopic extends BaseTopic {
    String dacId;
    String applicationId;
    String loanOfferId;
    String url;
    String status;
    String reason;
    String loanProviderReferenceNumber;
}
