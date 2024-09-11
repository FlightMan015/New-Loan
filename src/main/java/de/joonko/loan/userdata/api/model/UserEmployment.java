package de.joonko.loan.userdata.api.model;

import de.joonko.loan.offer.api.EmploymentType;
import de.joonko.loan.offer.api.ShortDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Builder
@AllArgsConstructor
@Data
public class UserEmployment {
    private boolean valid;

    private EmploymentType employmentType;
    private String employerName;
    private ShortDate employmentSince;
    private String streetName;
    private String postCode;
    private String city;
    private ShortDate professionEndDate;
    private String houseNumber;
    private String taxId;
}
