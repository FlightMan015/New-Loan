package de.joonko.loan.user.api.model;

import de.joonko.loan.user.api.model.Consent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsentResponse {

    @NotNull(message = "Consents must not be null")
    @NotEmpty(message = "Consents must not be empty")
    private List<Consent> consents;
}
