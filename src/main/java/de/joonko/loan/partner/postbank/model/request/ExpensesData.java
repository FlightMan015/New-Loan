package de.joonko.loan.partner.postbank.model.request;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonPropertyOrder({"warmmiete", "bewirtschaftung", "kreditraten", "baufinanzierung", "leasingrate", "sparrate", "unterhalt", "privateKrankenversicherung"})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpensesData {

    @JacksonXmlProperty(localName = "warmmiete")
    @Builder.Default
    private BigDecimal warmRent = BigDecimal.ZERO;

    // TODO: Ask Postbank for explanation of "management cost of property"
    @JacksonXmlProperty(localName = "bewirtschaftung")
    @Builder.Default
    private BigDecimal bewirtschaftung = BigDecimal.ZERO;

    @JacksonXmlProperty(localName = "kreditraten")
    @Builder.Default
    private BigDecimal loanInstalments = BigDecimal.ZERO;

    @JacksonXmlProperty(localName = "baufinanzierung")
    @Builder.Default
    private BigDecimal constructionOrMortgageExpenses = BigDecimal.ZERO;

    @JacksonXmlProperty(localName = "leasingrate")
    @Builder.Default
    private BigDecimal leasingExpenses = BigDecimal.ZERO;

    @JacksonXmlProperty(localName = "sparrate")
    @Builder.Default
    private BigDecimal savings = BigDecimal.ZERO;

    @JacksonXmlProperty(localName = "unterhalt")
    @Builder.Default
    private BigDecimal alimonyPayments = BigDecimal.ZERO;

    @JacksonXmlProperty(localName = "privateKrankenversicherung")
    @Builder.Default
    private BigDecimal privateHealthInsurance = BigDecimal.ZERO;
}
