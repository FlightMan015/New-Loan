package de.joonko.loan.user.states;


import de.joonko.loan.integrations.model.DistributionChannel;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

//TODO check keyspace topic
@Data
@Document
public class UserStatesStore {

    @Id
    private String userUUID;

    private Long bonifyUserId;

    private String tenantId;

    private DistributionChannel distributionChannel;

    private StateDetails userPersonalInformationStateDetails;

    private TransactionalDataStateDetails transactionalDataStateDetails;

    private Integer lastRequestedLoanAmount;

    private String lastRequestedPurpose;

    private Boolean isLastRequestedBonifyLoans;

    private Set<OfferDataStateDetails> offerDateStateDetailsSet;


    public boolean isBonifyUser() {
        return DistributionChannel.BONIFY == distributionChannel;
    }

    public void add(OfferDataStateDetails offerDataStateDetails) {
        if (offerDateStateDetailsSet == null) {
            offerDateStateDetailsSet = new HashSet<>();
        }
        offerDateStateDetailsSet.add(offerDataStateDetails);
    }
}
