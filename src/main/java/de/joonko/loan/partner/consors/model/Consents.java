package de.joonko.loan.partner.consors.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Consents {
    private String customerContactedByPhoneAndEmailForPromotions;

    private boolean schufaCallAllowed;
}
