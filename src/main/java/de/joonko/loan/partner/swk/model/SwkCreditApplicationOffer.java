package de.joonko.loan.partner.swk.model;


import de.joonko.loan.partner.swk.stub.CreditApplicationServiceStub;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document
public class SwkCreditApplicationOffer {
    @Id
    private String swkOfferId;
    private String applicationId;
    private CreditApplicationServiceStub.CreditOffer creditOffer;
}
