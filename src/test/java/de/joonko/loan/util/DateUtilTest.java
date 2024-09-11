package de.joonko.loan.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static de.joonko.loan.util.DateUtil.isBefore;
import static de.joonko.loan.util.DateUtil.isFirstDateIsBeforeTheLastDate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DateUtilTest {

    @Test
    void date_is_before() {
        assertTrue(isBefore(LocalDate.now().minusDays(1)));
    }

    @Test
    void first_date_is_not_bigger_than_second_date() {
        assertTrue(isFirstDateIsBeforeTheLastDate(LocalDate.now().minusDays(1), LocalDate.now()));
    }

    @Test
    void localDate_is_mapped_to_date() {
        // given
        LocalDate localDate = LocalDate.of(2020, 1, 1);

        // when
        Date mappedDate = DateUtil.toDate(localDate);

        // then
        assertEquals(0, localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().compareTo(mappedDate.toInstant()));
    }

    @Test
    void formatForAionFromInstant() {
        // given
        LocalDateTime localDateTime = LocalDateTime.of(2021, 12, 8, 11, 13, 56, 453876000);

        final var instant = localDateTime.atZone(ZoneId.of("UTC")).toInstant();

        final var result = DateUtil.formatForAionFromInstant(instant);

        assertEquals("2021-12-08T11:13:56Z", result);
    }

    @Test
    void formatForAionWrongFromInstant() {
        // given
        LocalDateTime localDateTime = LocalDateTime.of(2021, 12, 8, 11, 13, 56, 453876000);

        final var instant = localDateTime.atZone(ZoneId.of("UTC")).toInstant();

        final var result = DateUtil.formatForAionWrongFromInstant(instant);

        assertEquals("2021-12-08T11:13:453Z", result);
    }
}
