package de.joonko.loan.data.support.mapper;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;

public interface BaseMapper {
    default Long map(LocalDate instant) {
        return null == instant ? null : instant.toEpochSecond(LocalTime.NOON, ZoneOffset.UTC);
    }

    default Instant generateTimestamp() {
        return Instant.now();
    }

    default Long map(LocalDateTime instant) {
        return null == instant ? null : instant.toEpochSecond(ZoneOffset.UTC);
    }
}
