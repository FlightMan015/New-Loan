package de.joonko.loan.partner.consors;

import de.joonko.loan.partner.consors.model.LinkRelation;
import de.joonko.loan.partner.consors.model.PersonalizedCalculationsResponse;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.keyvalue.annotation.KeySpace;
import org.springframework.data.mongodb.core.mapping.Document;

@KeySpace("personalizedCalculationsStore")
@Data
@Builder
@Document
public class PersonalizedCalculationsStore {

    @Id
    private String personalizedCalculationsId;


    private String applicationId;

    private PersonalizedCalculationsResponse personalizedCalculationsResponse;

    public LinkRelation getFinalizeSubscriptionLink() {
        return personalizedCalculationsResponse.getFinancialCalculations()
                .getLinks()
                .stream()
                .filter(link -> link.getRel()
                        .equalsIgnoreCase("_finalizesubscription"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("_finalizesubscription link not found"));
    }

}
