package de.joonko.loan.userdata.infrastructure.draft.model;

import de.joonko.loan.offer.api.HousingType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserHousingStore {

    private HousingType housingType;
    private Integer numberOfDependants;
    private Integer numberOfChildren;
    private Double acknowledgedMortgages;
    private Double acknowledgedRent;
}
