package de.joonko.loan.data.support.model;

import de.joonko.loan.identification.model.CreateIdentRequest;
import de.joonko.loan.identification.model.CreateIdentResponse;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KycInitiationTopic extends BaseTopic {
    String dacId;
    CreateIdentRequest request;
    CreateIdentResponse response;
    private boolean internalUse;
}
