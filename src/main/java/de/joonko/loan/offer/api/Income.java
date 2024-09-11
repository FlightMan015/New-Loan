package de.joonko.loan.offer.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class Income implements Serializable {

    private Double netIncome;
    private Double pensionBenefits;
    private Double childBenefits;
    private Double otherRevenue;
    private Double rentalIncome;
    private Double alimonyPayments;
    private Double acknowledgedNetIncome;
    private Double incomeDeclared;

    void acknowledgeNetIncome() {
        this.acknowledgedNetIncome = netIncome;
    }
}
