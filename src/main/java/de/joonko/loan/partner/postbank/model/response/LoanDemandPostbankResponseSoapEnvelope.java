package de.joonko.loan.partner.postbank.model.response;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import javax.xml.bind.annotation.XmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JacksonXmlRootElement(localName = "Envelope")
@XmlRootElement(name = "Envelope")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanDemandPostbankResponseSoapEnvelope {

    @JacksonXmlProperty(isAttribute = true, localName = "xmlns")
    private String schema;

    @JacksonXmlProperty(localName = "Body")
    private LoanDemandPostbankResponseSoapBody body;

}
