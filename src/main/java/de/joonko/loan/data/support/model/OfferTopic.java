package de.joonko.loan.data.support.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OfferTopic extends BaseTopic {
    String dacId;
    String applicationId;
    String loanProvider;
    DataLoanOffer offer;
    private boolean internalUse;
    private String remark;
}