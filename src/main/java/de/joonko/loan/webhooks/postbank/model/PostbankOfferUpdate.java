package de.joonko.loan.webhooks.postbank.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import javax.xml.bind.annotation.XmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@JacksonXmlRootElement(localName = "update", namespace = "http://privatkredit.postbank.de/creditResult/types/v2")
@XmlRootElement(name = "update", namespace = "http://privatkredit.postbank.de/creditResult/types/v2")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostbankOfferUpdate {
    PostbankOfferResponse arg0;
}
