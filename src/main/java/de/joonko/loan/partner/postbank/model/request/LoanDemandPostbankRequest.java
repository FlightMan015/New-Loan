package de.joonko.loan.partner.postbank.model.request;


import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanDemandPostbankRequest {

    @JacksonXmlProperty(localName = "type")
    @Builder.Default
    private String type = "antrag";


    @JacksonXmlProperty(localName = "kreditid")
    private String applicationId;


    @JacksonXmlProperty(localName = "kreditbetrag")
    private Integer loanAmount;

    @JacksonXmlProperty(localName = "abloesebetrag")
    @Builder.Default
    private Integer refinancingLoanAmount = 0;

    @JacksonXmlProperty(localName = "laufzeit")
    private Integer duration;

    @JacksonXmlProperty(localName = "wunschrate")
    @Builder.Default
    private Integer interestRate = 0;

    @JacksonXmlProperty(localName = "ratenzahlung")
    @Builder.Default
    private Integer paymentDayOfMonth = 15;

    @JacksonXmlProperty(localName = "kreditnehmer")
    @NotNull
    private PersonalData personalData;

    @JacksonXmlProperty(localName = "konto")
    @JacksonXmlElementWrapper(useWrapping = false)
    @NotNull
    @NotEmpty
    private List<AccountData> accounts;

    @JacksonXmlProperty(localName = "umsatzdaten")
    @NotNull
    private FtsData ftsData;
}
