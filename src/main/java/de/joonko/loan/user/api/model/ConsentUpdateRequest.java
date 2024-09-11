package de.joonko.loan.user.api.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsentUpdateRequest {

    @NotNull(message = "Consents must not be null")
    @NotEmpty(message = "Consents must not be empty")
    @Valid
    private List<Consent> consents;
}
