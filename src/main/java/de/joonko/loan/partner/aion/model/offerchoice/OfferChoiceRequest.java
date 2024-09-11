package de.joonko.loan.partner.aion.model.offerchoice;

import de.joonko.loan.partner.aion.model.TransmissionDataType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OfferChoiceRequest {
    private TransmissionDataType name;

    private OfferChoiceTransmissionData value;
}
