package de.joonko.loan.offer.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
public class ContactData {

    private String city;
    private String streetName;
    private String streetNumber;
    private ZipCode zipCode;
    private LocalDate livingSince;
    private PreviousAddress previousAddress;
    private Email email;
    private String mobile;

}
