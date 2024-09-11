package de.joonko.loan.userdata.api.model;

import de.joonko.loan.offer.api.PreviousAddress;
import de.joonko.loan.offer.api.ShortDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Builder
@AllArgsConstructor
@Data
public class UserContact {
    private boolean valid;

    private String streetName;
    private String houseNumber;
    private String postCode;
    private String city;
    private ShortDate livingSince;
    private PreviousAddress previousAddress;
    private String email;
    private String mobile;
}
