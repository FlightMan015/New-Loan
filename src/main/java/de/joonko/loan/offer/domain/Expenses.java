package de.joonko.loan.offer.domain;

import de.joonko.loan.util.Util;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Expenses {
    private BigDecimal mortgages;
    private BigDecimal insuranceAndSavings;
    private BigDecimal loanInstalments;
    private BigDecimal rent;
    private BigDecimal alimony;
    private BigDecimal loanInstallmentsSwk;
    private BigDecimal vehicleInsurance;
    private BigDecimal privateHealthInsurance;
    private BigDecimal acknowledgedMortgages;
    private BigDecimal acknowledgedRent;
    private BigDecimal monthlyLifeCost;
    private BigDecimal monthlyLoanInstallmentsDeclared;

    public int getInsuranceAndSavingsInEuroCent() {
        return Util.toEuroCent(insuranceAndSavings);
    }

    public int getMortgagesInEuroCent() {
        return Util.toEuroCent(mortgages);
    }

    public int getLoanInstalmentsInEuroCent() {
        return Util.toEuroCent(loanInstalments);
    }

    public int getRentInEuroCent() {
        return Util.toEuroCent(rent);
    }

    public int getAlimonyInEuroCent() {
        return Util.toEuroCent(alimony);
    }

    public int getVehicleInsuranceInEuroCent() { return Util.toEuroCent(loanInstallmentsSwk); }

    public int getPrivateHealthInsuranceInEuroCent() {
        return Util.toEuroCent(privateHealthInsurance);
    }

    public int getAcknowledgedMortgagesInEuroCent() {
        return Util.toEuroCent(acknowledgedMortgages);
    }

    public int getAcknowledgedRentInEuroCent() {
        return Util.toEuroCent(acknowledgedRent);
    }

    public BigDecimal getSumOfAllExpenses() {
        return mortgages.add(insuranceAndSavings)
                .add(loanInstalments)
                .add(rent)
                .add(alimony)
                .add(privateHealthInsurance);
    }


}
