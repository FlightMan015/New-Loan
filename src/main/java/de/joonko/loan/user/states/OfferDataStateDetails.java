package de.joonko.loan.user.states;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.OffsetDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class OfferDataStateDetails {

    private String applicationId;
    private String parentApplicationId;
    private Integer amount;
    private String purpose;
    private OffsetDateTime requestDateTime;
    private OffsetDateTime responseDateTime;
    private Status state;
    private Boolean expired;


    public boolean isSuccess() {
        return Status.SUCCESS == this.state;
    }

    public boolean isError() {
        return Status.ERROR == this.state;
    }

    public boolean isRecommended() {
        return parentApplicationId != null;
    }

    public void markAsExpired() {
        this.expired = true;
    }

    @JsonIgnore
    public boolean isExpiredAlready() {
        return Boolean.TRUE == expired;
    }

    @JsonIgnore
    public boolean isNotExpired() {
        return Boolean.TRUE != expired;
    }
}
