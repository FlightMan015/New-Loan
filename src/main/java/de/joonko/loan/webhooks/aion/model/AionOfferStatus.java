package de.joonko.loan.webhooks.aion.model;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum AionOfferStatus {
    SUCCESS("SUCCESS"),
    MANUAL_AML("MANUAL_AML"),
    FAILED("FAILED");

    public final String status;
}
