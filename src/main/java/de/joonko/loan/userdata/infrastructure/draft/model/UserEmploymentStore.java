package de.joonko.loan.userdata.infrastructure.draft.model;

import de.joonko.loan.offer.api.EmploymentType;
import de.joonko.loan.offer.api.ShortDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserEmploymentStore {

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
