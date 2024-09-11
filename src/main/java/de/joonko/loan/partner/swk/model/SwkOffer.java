package de.joonko.loan.partner.swk.model;

import de.joonko.loan.partner.swk.stub.CreditApplicationServiceStub;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@Builder
@Document
public class SwkOffer {
    @Id
    private String swkOfferId;
    private String applicationId;
    private boolean isDefaultOfferAdded;
    private CreditApplicationServiceStub.CreditOffer creditOffer;
}
