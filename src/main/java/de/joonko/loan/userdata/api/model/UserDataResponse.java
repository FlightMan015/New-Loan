package de.joonko.loan.userdata.api.model;

import lombok.Data;

@Data
public class UserDataResponse {
    private UserPersonal userPersonal;
    private UserContact userContact;
    private UserEmployment userEmployment;
    private UserHousing userHousing;
    private UserCredit userCredit;
    private UserAccount userAccount;
}
