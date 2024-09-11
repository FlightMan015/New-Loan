package de.joonko.loan.partner.postbank.model.request;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"kontotyp", "kontoinhaber", "iban", "institut"})
public class AccountData {

    @JacksonXmlProperty(localName = "kontotyp")
    private AccountType accountType;

    @JacksonXmlProperty(localName = "kontoinhaber")
    private String accountHolder;

    @JacksonXmlProperty()
    private String iban;

    @JacksonXmlProperty(localName = "institut")
    private String bank;
}
