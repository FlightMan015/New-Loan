package de.joonko.loan.partner.consors.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ValidateSubscriptionRequest {

    private BankAccount bankAccount;

    private List<Subscriber> subscribers;

    private Boolean isEsigned;
    private String subscriptionIdentifierExternal;
    private Double externalLoansAmount; // what does it mean?
    private KycPurposeOfLoan kycPurposeOfLoan; // ??
    private SubscriptionBasketInfo subscriptionBasketInfo; // required - what does it mean?

    @JsonIgnore
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Subscriber subscriber;

    /**
     * FOR MVP We are not targeting Multiple Subscriber use case & At preset Mapstruct does not allow to map Single object to List. so this is kind
     * of workaround
     *
     * @return
     */
    public List<Subscriber> getSubscribers() {
        if (null != subscriber) {
            this.subscribers = List.of(subscriber);
        }
        return subscribers;
    }


}
