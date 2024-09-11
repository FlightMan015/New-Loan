package de.joonko.loan.partner.aion.model.offerchoice;

import de.joonko.loan.partner.aion.model.AionResponseVariable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OfferChoiceRawResponse {

    private String processId;

    private List<AionResponseVariable<Object>> variables;
}
