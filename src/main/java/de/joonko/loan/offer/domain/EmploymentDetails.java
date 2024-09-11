package de.joonko.loan.offer.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmploymentDetails {

    private EmploymentType employmentType;
    private String employerName;
    private LocalDate employmentSince;
    private String streetName;
    private ZipCode zipCode;
    private String city;
    private LocalDate professionEndDate;
    private String houseNumber;

}
