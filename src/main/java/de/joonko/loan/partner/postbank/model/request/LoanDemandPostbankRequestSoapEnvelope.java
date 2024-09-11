package de.joonko.loan.partner.postbank.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import javax.xml.bind.annotation.XmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JacksonXmlRootElement(localName = "soapenv:Envelope")
@XmlRootElement(name = "soapenv:Envelope")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanDemandPostbankRequestSoapEnvelope {

    @JacksonXmlProperty(isAttribute = true, localName = "xmlns:soapenv")
    @Builder.Default
    private String schema = "http://schemas.xmlsoap.org/soap/envelope/";

    @JacksonXmlProperty(isAttribute = true, localName = "xmlns")
    @Builder.Default
    private String types = "http://privatkredit.postbank.de/types";

    @JacksonXmlProperty(localName = "soapenv:Body")
    private LoanDemandPostbankRequestSoapBody body;
}
