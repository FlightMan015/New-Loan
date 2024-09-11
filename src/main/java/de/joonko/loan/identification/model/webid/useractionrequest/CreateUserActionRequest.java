package de.joonko.loan.identification.model.webid.useractionrequest;

import lombok.Data;

@Data
public class CreateUserActionRequest {
    private String transactionId;
    private String joinWithActionId;
    private String actionType;
    private String identMode;
    private String identifiedOn;
    private String preferredLanguage;
    private User user;
    private IdDocument idDocument;
    private Device device;
    private ProcessParameters processParameters;
    private CustomParameters customParameters;
    private Boolean termsAndConditionsConfirmed;
}
