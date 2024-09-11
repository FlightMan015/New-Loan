package de.joonko.loan.partner.santander.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@Builder
@Document
public class SantanderOffer {
    @Id
    private String santanderOfferId;
    private String applicationId;
    private GetKreditvertragsangebotResponse kreditOffer;
}
