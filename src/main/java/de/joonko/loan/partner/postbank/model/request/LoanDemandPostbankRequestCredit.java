package de.joonko.loan.partner.postbank.model.request;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanDemandPostbankRequestCredit {

    @JacksonXmlProperty(isAttribute = true, localName = "id")
    private String companyId;

    @JacksonXmlProperty(isAttribute = true, localName = "passwort")
    private String password;

    @JacksonXmlProperty(isAttribute = true, localName = "version")
    @Builder.Default
    private String version = "1.0";

    @JacksonXmlProperty(localName = "request")
    private LoanDemandPostbankRequest request;
}
