package de.joonko.loan.partner.postbank.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"beschaeftigungsart", "seit", "bis"})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmploymentData {

    @JacksonXmlProperty(localName = "beschaeftigungsart")
    @Builder.Default
    private Integer employmentType = 1; // employee

    @JacksonXmlProperty(localName = "seit")
    private String employmentSince;

    // TODO: Ask Postbank what else we shoudl send in case we have the bis field as if nothing else is added we get an error back
    // Bei befristetem Beschäftigungsverhältnis ist die Angabe ob dieses schon einmal verlängert wurde Pflicht!
    @JacksonXmlProperty(localName = "bis")
    private String employmentUntil;
}
