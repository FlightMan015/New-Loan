package de.joonko.loan.user.api.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Consent {

    @NotNull(message = "Consent type must not be null")
    private ConsentApiType type;

    @NotNull(message = "Accepted must not be null")
    private ConsentApiState consent;
}
