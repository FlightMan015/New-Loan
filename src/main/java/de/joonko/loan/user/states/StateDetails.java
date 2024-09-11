package de.joonko.loan.user.states;

import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class StateDetails {

    protected OffsetDateTime requestDateTime;
    protected OffsetDateTime responseDateTime;
    protected Status state;
    
    @Builder.Default
    private Boolean additionalFieldsForHighAmountAdded = false;

    public boolean isSuccess() {
        return Status.SUCCESS == this.state;
    }


}
