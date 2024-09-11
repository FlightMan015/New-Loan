package de.joonko.loan.partner.postbank.model.request;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import de.joonko.loan.partner.postbank.model.request.fts.DocumentType;
import de.joonko.loan.partner.postbank.model.request.fts.ProviderType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FtsData {

    @JacksonXmlProperty(localName = "iban")
    @NotNull
    private String iban;

    @JacksonXmlProperty(localName = "document")
    private String document;

    @JacksonXmlProperty(localName = "documentType")
    @NotNull
    @Builder.Default
    private DocumentType documentType = DocumentType.JSON;

    @JacksonXmlProperty(localName = "documentName")
    @NotNull
    @Builder.Default
    private String documentName = "dac";

    @JacksonXmlProperty(localName = "source")
    @NotNull
    @Builder.Default
    private ProviderType source = ProviderType.FINTEC;

    @JacksonXmlProperty(localName = "target")
    @NotNull
    @Builder.Default
    private ProviderType target = ProviderType.FINTEC;
}
