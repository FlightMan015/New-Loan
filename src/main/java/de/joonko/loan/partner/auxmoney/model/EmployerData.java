package de.joonko.loan.partner.auxmoney.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployerData {
    private String zip;

    private String phone;

    private String city;

    private String street;

    private String company;

    private EmploymentStatus employmentStatus;

    private String since;


}
