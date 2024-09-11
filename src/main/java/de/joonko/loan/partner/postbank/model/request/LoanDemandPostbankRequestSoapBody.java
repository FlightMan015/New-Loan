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
public class LoanDemandPostbankRequestSoapBody {

    @JacksonXmlProperty(isAttribute = true, localName = "serviceKreditantrag")
    private LoanDemandPostbankRequestContract contract;
}
