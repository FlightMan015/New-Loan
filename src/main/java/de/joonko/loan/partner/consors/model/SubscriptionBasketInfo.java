package de.joonko.loan.partner.consors.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionBasketInfo {
    private Double price;
    private Double firstPayment;
    private Integer term;
    private String shopName;
    private String shopStreet;
    private String shopLocation;
    private Integer kiosk;
    private Integer campaignDuration;
    private String successURL;
    private String failureURL;
    private String cancelURL;
    private String notifyURL;
    private String returnToCheckoutURL;
    private Integer campaignCode;
}
