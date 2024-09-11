package de.joonko.loan.partner.postbank.model.response;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanDemandPostbankResponseStatus {

    // 0 - error, 1 - no error
    @JacksonXmlProperty(localName = "state")
    private Integer state;

    // TODO: Ask Postbank if this is integer and 1 value or can be a list, also what do these numbers stand for
    @JacksonXmlProperty(localName = "error")
    private Integer error;

    @JacksonXmlProperty(localName = "messages")
    private List<LoanDemandPostbankResponseMessage> messages;
}
