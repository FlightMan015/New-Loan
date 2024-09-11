package de.joonko.loan.util;

import lombok.experimental.UtilityClass;

import javax.validation.constraints.NotNull;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@UtilityClass
public class DateUtil {

    private static final String DEFAULT_ZONE = "Europe/Berlin";
    private static final String UTC_ZONE = "UTC";

    public static boolean isBefore(LocalDate dateToCheck) {
        return dateToCheck.isBefore(LocalDate.now());
    }

    public static boolean isFirstDateIsBeforeTheLastDate(LocalDate firstDate, LocalDate lastDate) {
        return firstDate.isBefore(lastDate);
    }

    public static Date toDate(@NotNull LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static LocalDateTime fromInstant(final Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneId.of(UTC_ZONE));
    }

    public static String formatForAionFromInstant(final Instant instant) {
        return OffsetDateTime.ofInstant(instant, ZoneId.of(UTC_ZONE))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
    }

    // TODO: Remove and replace with the right implementation, when Aion makes the corrections
    public static String formatForAionWrongFromInstant(final Instant instant) {
        return OffsetDateTime.ofInstant(instant, ZoneId.of(UTC_ZONE))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:SSS'Z'"));
    }
}
