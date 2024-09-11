package de.joonko.loan.reporting.api.validator;

import org.springframework.stereotype.Component;

import javax.validation.ValidationException;

import java.time.OffsetDateTime;
import java.util.function.BiPredicate;

@Component
public class DateValidator implements BiPredicate<OffsetDateTime, OffsetDateTime> {

    private static final int MAX_MONTHS_DURATION = 3;

    @Override
    public boolean test(OffsetDateTime from, OffsetDateTime to) {
        return isBefore(from, to) && isBetweenMonthsLimit(from, to);
    }

    private boolean isBefore(OffsetDateTime from, OffsetDateTime to) {
        if (from.isBefore(to)) {
            return true;
        } else {
            throw new ValidationException("Required request parameter 'start-date' has to be before 'end-date'");
        }
    }

    private boolean isBetweenMonthsLimit(OffsetDateTime from, OffsetDateTime to) {
        if (to.minusMonths(MAX_MONTHS_DURATION).isBefore(from)) {
            return true;
        } else {
            throw new ValidationException(String.format("Max duration between request parameters 'start-date' and 'end-date' has to be less than %s months", MAX_MONTHS_DURATION));
        }
    }
}
