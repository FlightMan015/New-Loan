package de.joonko.loan.partner.solaris;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.keyvalue.annotation.KeySpace;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@KeySpace("solarisAcceptOfferResponseStore")
@Data
@Builder
@Document("solarisAcceptOffer")
public class SolarisAcceptOfferResponseStore {
    @Id
    String solarisAcceptOfferResponseStoreId;
    String identificationId;
    String personId;
    String applicationId;
    String signingId;
    Boolean consentAccepted;
    @CreatedDate
    private LocalDateTime insertTs;
}