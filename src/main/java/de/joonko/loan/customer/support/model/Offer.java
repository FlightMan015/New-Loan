package de.joonko.loan.customer.support.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Offer {
    private String bank;
    private double amount;
    private double durationInMonths;
    private double effectiveInterestRate;
    private double nominalInterestRate;
    private double monthlyRate;
    private double totalPayment;
}
