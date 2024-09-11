package de.joonko.loan.offer.api.status;

import de.joonko.loan.common.domain.Bank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class LoanStatusRequest {

    @NotNull(message = "Banks must not be null")
    @NotEmpty(message = "Banks must not be empty")
    @Valid
    private Set<Bank> banks;
}
