package de.joonko.loan.webhooks.postbank.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class PostbankWebhookResponseEnvelopeBody {

    @JacksonXmlProperty(localName = "v2:updateResponse")
    private PostbankWebhookResponseEnvelope responseEnvelope;
}
