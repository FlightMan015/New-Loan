package de.joonko.loan.partner.consors;

import de.joonko.loan.partner.consors.model.ConsorsAcceptOfferResponse;
import de.joonko.loan.partner.consors.model.Link;

import org.springframework.data.annotation.Id;
import org.springframework.data.keyvalue.annotation.KeySpace;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Builder;
import lombok.Data;

@KeySpace("consorsAcceptedOffer")
@Data
@Builder
@Document
public class ConsorsAcceptedOfferStore {
    @Id
    private String loanApplicationId;

    ConsorsAcceptOfferResponse consorsAcceptOfferResponse;

    private static final String DOWNLOAD_SUBSCRIPTION_LINK = "_downloadSubscriptionDocument";
    private static final String ONLINE_IDENT_LINK = "_onlineIdent";

    public String getDownloadSubscriptionDocumentLink() {
        return consorsAcceptOfferResponse.getLinks()
                .stream()
                .filter(link -> link.getRel()
                        .equalsIgnoreCase(DOWNLOAD_SUBSCRIPTION_LINK))
                .map(Link::getHref)
                .findFirst()
                .orElseThrow(() -> new RuntimeException(String.format("%s link not found", DOWNLOAD_SUBSCRIPTION_LINK)));
    }

    public String getKYCLink() {
        return consorsAcceptOfferResponse.getLinks()
                .stream()
                .filter(link -> link.getRel()
                        .equalsIgnoreCase(ONLINE_IDENT_LINK))
                .map(Link::getHref)
                .findFirst()
                .orElseThrow(() -> new RuntimeException(String.format("%s link not found", ONLINE_IDENT_LINK)));
    }

}
