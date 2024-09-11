package de.joonko.loan.data.support.model;

import de.joonko.loan.offer.domain.ContactData;
import de.joonko.loan.offer.domain.EmploymentDetails;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PersonalDetailTopic extends BaseTopic {
    private String applicationId;
    private Integer loanAsked;
    private int duration;
    private de.joonko.loan.offer.domain.PersonalDetails personalDetails;
    private EmploymentDetails employmentDetails;
    private ContactData contactData;
    private String dacId;
    private boolean internalUse;
}
