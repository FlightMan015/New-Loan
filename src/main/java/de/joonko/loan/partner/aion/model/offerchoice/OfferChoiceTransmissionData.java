package de.joonko.loan.partner.aion.model.offerchoice;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class OfferChoiceTransmissionData {

    private String selectedOfferId;

    private LocalDateTime timestamp;
}
