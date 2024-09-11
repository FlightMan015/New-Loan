package de.joonko.loan.partner.santander.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class GetKreditvertragsangebotResponse {

    String scbAntragId;
    String antragstatusType;
    int duration;

}
