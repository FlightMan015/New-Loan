package de.joonko.loan.webhooks.webid.model.request;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class JointUserActionStatus {
    private List<String> failedUserActions = null;
    private List<String> pendingUserActions = null;
    private List<String> successfulUserActions = null;
}