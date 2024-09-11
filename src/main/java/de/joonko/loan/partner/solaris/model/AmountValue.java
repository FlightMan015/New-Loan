package de.joonko.loan.partner.solaris.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AmountValue {
    @NotNull(message = "Currency cannot be null or empty")
    private String currency;

    @NotNull(message = "Currency unit cannot be null or empty")
    private String unit;

    @NotNull(message = "Amount value cannot be null or empty")
    private Integer value;
}
