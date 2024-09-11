package de.joonko.loan.db.vo;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.keyvalue.annotation.KeySpace;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@KeySpace("loanDemandStore")
@Data
@Builder(toBuilder = true)
@Document
public class LoanDemandStore {

    @Id
    private String applicationId;
    @Indexed
    private String userUUID;
    @Indexed
    private String dacId;
    @Indexed
    private String ftsTransactionId; //TODO: find better place
    private String firstName;
    private String lastName;
    private String emailId;
    private Boolean termsAccepted;
    private ExternalIdentifiers externalIdentifiers;
    private Boolean internalUse;

    @LastModifiedDate
    private LocalDateTime updateTs;
}
