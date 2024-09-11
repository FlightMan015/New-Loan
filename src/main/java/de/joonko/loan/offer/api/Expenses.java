package de.joonko.loan.offer.api;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class Expenses implements Serializable {

    private Double mortgages;
    private Double insuranceAndSavings;
    private Double loanInstalments;
    private Double rent;
    private Double alimony;
    private Double privateHealthInsurance;
    private Double loanInstallmentsSwk;
    private Double vehicleInsurance;
    private Double monthlyLifeCost;
    private Double monthlyLoanInstallmentsDeclared;
    @NotNull(message = "acknowledgedMortgages must not be null")
    private Double acknowledgedMortgages;
    private Double acknowledgedRent;


    void acknowledgeRentAndMortgages() {
        if (this.acknowledgedRent == null && rent > 0) {
            this.acknowledgedRent = rent;
        }
        this.acknowledgedMortgages = mortgages;
    }
}
