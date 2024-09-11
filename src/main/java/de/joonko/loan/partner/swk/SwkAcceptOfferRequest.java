package de.joonko.loan.partner.swk;

import de.joonko.loan.partner.swk.stub.CreditApplicationServiceStub;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
public class SwkAcceptOfferRequest {
    private String applicationId;
    private String offerId;
    private CreditApplicationServiceStub.ApplyForCredit applyForCredit;
}

