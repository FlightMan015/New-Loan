package de.joonko.loan.partner.postbank.model.response;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanDemandPostbankResponse {

    @JacksonXmlProperty(isAttribute = true, localName = "xmlns")
    private String type;

    @JacksonXmlProperty(localName = "return")
    private LoanDemandPostbankResponseBody responseBody;
}
