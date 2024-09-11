package de.joonko.loan.partner.consors.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
class LegitimationInfo {

    private KycStatus kycStatus;
    private Boolean legitimationFlag;
}
			
		