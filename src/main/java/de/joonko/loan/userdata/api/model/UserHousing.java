package de.joonko.loan.userdata.api.model;

import de.joonko.loan.offer.api.HousingType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Builder
@AllArgsConstructor
@Data
public class UserHousing {
    private boolean valid;

    private HousingType housingType;
    private Integer numberOfDependants;
    private Integer numberOfChildren;

    private Double mortgages;
    private Double acknowledgedMortgages;

    private Double rent;
    private Double acknowledgedRent;
}
