package de.joonko.loan.webhooks.postbank.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import javax.xml.bind.annotation.XmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class PostbankWebhookResponse {

    @JacksonXmlProperty(localName = "success")
    private boolean success;

    @JacksonXmlProperty(localName = "error")
    private PostbankRequestError error;
}
