package de.joonko.loan.webhooks.solaris.enums;

public enum WebhookIdentificationStatus {

    created,
    pending,
    pending_successful,
    pending_failed,
    successful,
    aborted,
    canceled,
    failed;

    WebhookIdentificationStatus() {
    }
}
