package de.joonko.loan.offer.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditDetails {
    private Integer bonimaScore;
    private String estimatedSchufaClass;
    private BigDecimal probabilityOfDefault;
    private BigDecimal creditCardLimitDeclared;
    private Boolean isCurrentDelayInInstallmentsDeclared;
}
