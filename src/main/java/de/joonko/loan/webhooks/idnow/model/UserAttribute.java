package de.joonko.loan.webhooks.idnow.model;

import de.joonko.loan.webhooks.idnow.model.enums.MatchStatus;
import de.joonko.loan.webhooks.idnow.model.enums.ReviewStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAttribute<T> {
    private MatchStatus status;
    private ReviewStatus reviewstatus;
    private T value;
    private T original;
    private T identification;
}
