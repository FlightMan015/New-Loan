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
public class LoanDemandPostbankResponseMessage {

    @JacksonXmlProperty(localName = "message")
    private String message;
}
