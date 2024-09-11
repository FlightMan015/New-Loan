package de.joonko.loan.data.support.model;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RedOfferTopic extends BaseTopic {
    String dacId;
    String applicationId;
    String loanProvider;
    String rejectReason;
    String rejectCode;
    String fullResponse;
    private boolean internalUse;
}
