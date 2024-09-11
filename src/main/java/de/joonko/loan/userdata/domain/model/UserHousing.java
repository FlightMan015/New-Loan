package de.joonko.loan.userdata.domain.model;

import de.joonko.loan.offer.api.HousingType;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class UserHousing {
    private boolean valid;

    @NotNull(message = "HousingType must not be null")
    private HousingType housingType;

    @NotNull(message = "Number of Dependants must not be null")
    @Min(0)
    @Builder.Default
    private Integer numberOfDependants;

    @NotNull(message = "Number of Children must not be null")
    @Min(0)
    @Max(9)
    @Builder.Default
    private Integer numberOfChildren;

    private Double mortgages;
    @NotNull(message = "acknowledgedMortgages must not be null")
    private Double acknowledgedMortgages;

    private Double rent;
    private Double acknowledgedRent;
}
