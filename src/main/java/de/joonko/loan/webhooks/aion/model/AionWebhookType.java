package de.joonko.loan.webhooks.aion.model;

import java.util.Arrays;
import java.util.Optional;

public enum AionWebhookType {
    CASHLOAN_OPEN("credits.cashloan.loanopen"),
    ONBOARDING("credits.cashloan.onboarding");

    private String value;

    public String getValue() {
        return value;
    }

    AionWebhookType(final String value) {
        this.value = value;
    }

    public static Optional<AionWebhookType> fromValue(final String value) {
        return Arrays.stream(AionWebhookType.values())
                .filter(type -> type.value.equals(value))
                .findFirst();
    }
}
