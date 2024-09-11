package de.joonko.loan.offer.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class Precheck {

    private String provider;

    private PreCheckEnum preCheck;

    private Boolean value;

}
