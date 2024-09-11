package de.joonko.loan.webhooks.idnow.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "IdnowWebHookNotifications")
public class Identification {
    @JsonProperty("identificationprocess")
    private IdentificationProcess identificationProcess;
    @JsonProperty("customdata")
    private Map<String, String> customData;
    @JsonProperty("contactdata")
    private ContactData contactData;
    @JsonProperty("userdata")
    private UserData userData;
    @JsonProperty("identificationdocument")
    private IdentificationDocument identificationDocument;
    private Map<String, String> attachments;
    private Map<String, Question> questions;
}
