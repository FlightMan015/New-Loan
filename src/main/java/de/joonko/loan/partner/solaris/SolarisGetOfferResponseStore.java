package de.joonko.loan.partner.solaris;

import de.joonko.loan.partner.solaris.model.SolarisGetOffersResponse;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.keyvalue.annotation.KeySpace;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@KeySpace("solarisGetOfferResponseStore")
@Data
@Builder
@Document("solarisOffer")
public class SolarisGetOfferResponseStore {

    @Id
    String solarisGetOfferResponseStoreId;
    String applicationId;
    SolarisGetOffersResponse solarisGetOffersResponse;
    @CreatedDate
    private LocalDateTime insertTs;

}
