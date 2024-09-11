package de.joonko.loan.userdata.api.model;

import lombok.Data;

@Data
public class UserDataRequest {
    private UserPersonal userPersonal;
    private UserContact userContact;
    private UserEmployment userEmployment;
    private UserHousing userHousing;
    private UserCredit userCredit;
}
