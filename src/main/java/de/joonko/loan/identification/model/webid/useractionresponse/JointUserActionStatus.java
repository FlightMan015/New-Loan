package de.joonko.loan.identification.model.webid.useractionresponse;

import lombok.Data;

import java.util.List;

@Data
public class JointUserActionStatus {
    private List<String> failedUserActions;
    private List<String> pendingUserActions;
    private List<String> successfulUserActions;
}
