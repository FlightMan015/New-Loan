package de.joonko.loan.identification.model.webid.useractionresponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CreateUserActionResponse {
    private String url;
    private String actionId;
    private String transactionId;
    private String createdOn;
    private String approximateDateOfExpiry;
    private JointUserActionStatus jointUserActionStatus;
}
