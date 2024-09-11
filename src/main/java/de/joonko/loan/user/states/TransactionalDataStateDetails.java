package de.joonko.loan.user.states;

import java.time.OffsetDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransactionalDataStateDetails {

    private String accountInternalId;
    private OffsetDateTime requestFromDataSolution;
    private OffsetDateTime responseFromDataSolution;
    private Integer sentForClassificationCounter;
    private OffsetDateTime sentForClassification;
    private OffsetDateTime responseDateTime;
    private Status state;
    private Boolean salaryAccountAdded;
    private Boolean userVerifiedByBankAccount;


    public boolean isSuccess() {
        return Status.SUCCESS == this.state;
    }

    public boolean isStatusIs(Status status) {
        return this.state != null && this.state == status;
    }


    public void increaseSentForClassificationCounter() {
        if (this.sentForClassificationCounter == null) {
            this.sentForClassificationCounter = 1;
        } else {
            this.sentForClassificationCounter++;
        }
    }

    public void clearSentForClassificationCounter() {
        this.sentForClassificationCounter = null;
    }
}
