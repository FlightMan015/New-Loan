package de.joonko.loan.integrations.domain.integrationhandler.testData;

import de.joonko.loan.offer.api.LoanDemandRequest;
import de.joonko.loan.offer.domain.LoanDemand;

public class LoanDemandProviderServiceTestData {

    public LoanDemand getLoanDemand(String applicationId) {
        return new LoanDemand(applicationId, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
    }

    public LoanDemandRequest getRequestedLoanDemandRequest(String userId, Integer loanAmount) {
        LoanDemandRequest loanDemandRequest = new LoanDemandRequest();
        loanDemandRequest.setUserUUID(userId);
        loanDemandRequest.setLoanAsked(loanAmount);
        return loanDemandRequest;
    }

    public LoanDemandRequest getRecommended(LoanDemandRequest loanDemandRequest, Integer loanAsked) {
        return LoanDemandRequest.builder()
                .userUUID(loanDemandRequest.getUserUUID())
                .loanAsked(loanAsked)
                .parentApplicationId(loanDemandRequest.getApplicationId())
                .build();
    }
}
