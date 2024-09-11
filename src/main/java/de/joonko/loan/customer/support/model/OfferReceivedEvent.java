package de.joonko.loan.customer.support.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OfferReceivedEvent extends Event {
    private String offersLink;

    @Builder
    public OfferReceivedEvent(String email, Long createdAt, String comment, String offersLink) {
        super(email, createdAt, comment);
        this.offersLink = offersLink;
    }
}
