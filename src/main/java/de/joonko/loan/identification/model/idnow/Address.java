package de.joonko.loan.identification.model.idnow;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class Address {

    private StatusValue zipcode;
    private StatusValue country;
    private StatusValue city;
    private StatusValue street;
    private StatusValue streetnumber;
}
