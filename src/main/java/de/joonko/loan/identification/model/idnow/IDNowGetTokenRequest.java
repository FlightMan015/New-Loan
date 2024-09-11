package de.joonko.loan.identification.model.idnow;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IDNowGetTokenRequest {

    private String apiKey;
}
