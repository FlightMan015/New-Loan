package de.joonko.loan.partner.postbank.model.request;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@JsonPropertyOrder({"nettoeinkommen", "freiberuf", "mieteinnahmen", "minijob", "unterhalt", "wohngeld"})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncomeData {

    @JacksonXmlProperty(localName = "nettoeinkommen")
    private BigDecimal netIncome;

    @JacksonXmlProperty(localName = "freiberuf")
    @Builder.Default
    private BigDecimal freelanceIncome = BigDecimal.ZERO;

    @JacksonXmlProperty(localName = "mieteinnahmen")
    @Builder.Default
    private BigDecimal rentalIncome = BigDecimal.ZERO;

    @JacksonXmlProperty(localName = "minijob")
    @Builder.Default
    private BigDecimal minijob = BigDecimal.ZERO;

    @JacksonXmlProperty(localName = "unterhalt")
    @Builder.Default
    private BigDecimal alimonyPayments = BigDecimal.ZERO;

    // TODO: Ask Postbank what this revenue stream stands for, we already have rentalIncome
    @JacksonXmlProperty(localName = "wohngeld")
    @Builder.Default
    private BigDecimal housingBenefits = BigDecimal.ZERO;
}
