package de.joonko.loan.partner.consors.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Income {
    private int netIncome;
    private int rentIncome;
    private int otherIncome;
    private Boolean isChildBenefitInSalery;
    private Boolean childBenefitInSalery;
}
