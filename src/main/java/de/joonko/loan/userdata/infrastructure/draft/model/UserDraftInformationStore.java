package de.joonko.loan.userdata.infrastructure.draft.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class UserDraftInformationStore {

    @Id
    private String userUUID;

    private UserPersonalStore userPersonal;
    private UserContactStore userContact;
    private UserEmploymentStore userEmployment;
    private UserHousingStore userHousing;
    private UserCreditStore userCredit;
}
