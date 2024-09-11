package de.joonko.loan.webhooks.postbank.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostbankOfferResponseBody {
    @JacksonXmlProperty(localName = "update")
    PostbankOfferUpdate update;
}
