package de.joonko.loan.webhooks.postbank.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import javax.xml.bind.annotation.XmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "soapenv:Envelope")
@JacksonXmlRootElement(localName = "soapenv:Envelope")
public class PostbankWebhookResponseEnvelopeWrapper {

    @JacksonXmlProperty(localName = "xmlns:soapenv", isAttribute = true)
    @Builder.Default
    private String namespace1 = "http://schemas.xmlsoap.org/soap/envelope/";

    @JacksonXmlProperty(localName = "xmlns:v2", isAttribute = true)
    @Builder.Default
    private String namespace2 = "http://privatkredit.postbank.de/creditResult/types/v2";

    @JacksonXmlProperty(localName = "soapenv:Header")
    private String header;

    @JacksonXmlProperty(localName = "soapenv:Body")
    private PostbankWebhookResponseEnvelopeBody body;
}
