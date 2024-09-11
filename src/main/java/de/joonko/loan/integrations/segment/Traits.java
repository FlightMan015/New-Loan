package de.joonko.loan.integrations.segment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Traits {
    private Boolean hasAddedBank;
    private String tenantId;

    private String gender;
    private String firstName;
    private String lastName;
    private String maritalStatus;
    private LocalDate dateOfBirth;
    private String nationality;
    private String placeOfBirth;
    private Integer childrenCount;
    private String housingSituation;
    private Integer numberOfCreditCard;

    private Integer bonimaScore;
    private String estimatedSchufaClass;
    private Double probabilityOfDefault; //TODO: missing in segment

    private String addressStreet;
    private String addressHouseNumber;
    private String addressZipCode;
    private String addressCity;
    private LocalDate livingSince;
    // TODO: [previousAddress] missing
    private String email;
    private String phone_number;

    private String employmentType;
    private String nameOfEmployer;
    private LocalDate workContractStartDate;
    private String addressStreetOfEmployer;
    private String addressZipCodeOfEmployer;
    private String addressCityOfEmployer;
    // TODO: [professionEndDate] missing
    private String addressHouseNumberOfEmployer;

    private Double employeeSalaryAmountLast1M;
    private Double pensionAmountLast1M;
    private Double childBenefitAmountLast1M;
    private Double otherIncomeAmountLast1M;
    private Double rentalIncomeLast1M;
    private Double alimonyIncomeAmountLast1M;

    private Double monthlyMortgage;
    private Double monthlyInsurance;
    private Double monthlyLoanInstallments;
    private Double monthlyRent;
    private Double alimonyAmountLast1M;
    private Double monthlyPrivateHealthInsurance;
    // TODO: [loanInstallmentsSwk] missing
    private Double carInsuranceAmountLast1M;
}
