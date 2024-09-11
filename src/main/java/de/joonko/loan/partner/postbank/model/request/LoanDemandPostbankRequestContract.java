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
public class LoanDemandPostbankRequestContract {

    @JacksonXmlProperty(localName = "pbkredit")
    private LoanDemandPostbankRequestCredit credit;
}
