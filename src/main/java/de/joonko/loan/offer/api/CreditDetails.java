package de.joonko.loan.offer.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CreditDetails implements Serializable {

    private Integer bonimaScore;
    private String estimatedSchufaClass;
    private Double probabilityOfDefault;
    private Double creditCardLimitDeclared;
    private Boolean isCurrentDelayInInstallmentsDeclared;
}
