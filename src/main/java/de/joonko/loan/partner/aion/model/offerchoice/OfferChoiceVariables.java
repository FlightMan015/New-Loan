package de.joonko.loan.partner.aion.model.offerchoice;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OfferChoiceVariables {

    private String representativeId;

    private String agreement;
    private String schedule;
    private String secci;
}
