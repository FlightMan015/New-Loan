package de.joonko.loan.partner.postbank.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// TODO: Ask why Postbank requests multiple addresses and how we can differentiate between current and previous address

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"strasse", "hausnummer", "plz", "ort", "land", "seit"})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressData {

    @JacksonXmlProperty(localName = "strasse")
    private String street;

    @JacksonXmlProperty(localName = "hausnummer")
    private String building;

    @JacksonXmlProperty(localName = "plz")
    private String postalCode;

    @JacksonXmlProperty(localName = "ort")
    private String city;

    @JacksonXmlProperty(localName = "land")
    @Builder.Default
    private String country = "DE";

    // TODO: Ask Postbank when this is used and how to differentiate between current and previous addresses
    // TODO: In case this value is mandatory for POSTBANK, we need to introduce this value
    // TODO: Remove the default after correct implementation
    @JacksonXmlProperty(localName = "seit")
    @Builder.Default
    private String since = "2017-02";
}
