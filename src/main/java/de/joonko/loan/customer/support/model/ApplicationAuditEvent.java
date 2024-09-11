package de.joonko.loan.customer.support.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationAuditEvent extends Event {
    private String auditLink;

    @Builder
    public ApplicationAuditEvent(String email, Long createdAt, String comment, String auditLink) {
        super(email, createdAt, comment);
        this.auditLink = auditLink;
    }
}
