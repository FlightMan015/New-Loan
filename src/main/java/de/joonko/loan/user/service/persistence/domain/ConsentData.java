package de.joonko.loan.user.service.persistence.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.time.Instant;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ConsentData implements Serializable {

    private ConsentType consentType;

    private ConsentState consentState;

    private Instant lastUpdatedTimestamp;

    private String clientIP;
}
