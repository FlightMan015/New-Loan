package de.joonko.loan.common.utils;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class DateTimeConverter {

    public static OffsetDateTime fromLong(Long value) {
        return OffsetDateTime.of(LocalDateTime.ofEpochSecond(value, 0, ZoneOffset.UTC), ZoneOffset.UTC);
    }

    public static OffsetDateTime from(LocalDateTime localDateTime) {
        return OffsetDateTime.of(localDateTime, ZoneOffset.UTC);
    }

    public static Long toLong(OffsetDateTime value) {
        return value.toEpochSecond();
    }
}
