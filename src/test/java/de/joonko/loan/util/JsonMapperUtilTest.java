package de.joonko.loan.util;

import de.joonko.loan.dac.fts.model.Account;
import de.joonko.loan.dac.fts.model.Balance;
import de.joonko.loan.dac.fts.model.FtsRawData;
import de.joonko.loan.dac.fts.model.Turnover;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JsonMapperUtilTest {

    @SneakyThrows
    @Test
    void getJsonAsString() {
        // given
        final var ftsData = FtsRawData.builder()
                .account(Account.builder()
                        .holder("HolderTest")
                        .bankName("BankNameTest")
                        .description("descriptionTest")
                        .build())
                .balance(Balance.builder()
                        .balance(1)
                        .limit(2)
                        .build())
                .turnovers(List.of(
                        Turnover.builder()
                                .amount(BigDecimal.ONE)
                                .build(),
                        Turnover.builder()
                                .amount(BigDecimal.TEN)
                                .build()
                ))
                .date("01-01-2022")
                .days(90)
                .build();

        // when
        final var actualJson = JsonMapperUtil.getJsonString(ftsData);

        // then
        assertEquals("{\"account\":{\"jointAccount\":false,\"holder\":\"HolderTest\",\"description\":\"descriptionTest\",\"iban\":null,\"bic\":null,\"bank_name\":\"BankNameTest\",\"country_id\":null,\"joint_account\":false,\"type\":null},\"balance\":{\"balance\":1,\"limit\":2,\"available\":0,\"currency\":null,\"date\":null},\"turnovers\":[{\"booking_date\":null,\"amount\":1,\"currency\":null,\"purpose\":null,\"counter_iban\":null,\"counter_bic\":null,\"counter_holder\":null,\"prebooked\":false,\"creditor_id\":null,\"tags\":null},{\"booking_date\":null,\"amount\":10,\"currency\":null,\"purpose\":null,\"counter_iban\":null,\"counter_bic\":null,\"counter_holder\":null,\"prebooked\":false,\"creditor_id\":null,\"tags\":null}],\"date\":\"01-01-2022\",\"days\":90,\"filters\":null}", actualJson);
    }

    @SneakyThrows
    @Test
    void getEncodedJsonObject() {
        // given
        final var ftsData = FtsRawData.builder()
                .account(Account.builder()
                        .holder("HolderTest")
                        .bankName("BankNameTest")
                        .description("descriptionTest")
                        .build())
                .balance(Balance.builder()
                        .balance(1)
                        .limit(2)
                        .build())
                .turnovers(List.of(
                        Turnover.builder()
                                .amount(BigDecimal.ONE)
                                .build(),
                        Turnover.builder()
                                .amount(BigDecimal.TEN)
                                .build()
                ))
                .date("01-01-2022")
                .days(90)
                .build();

        // when
        final var actualJson = JsonMapperUtil.getBase64Encoded(ftsData);

        // then
        assertEquals("eyJhY2NvdW50Ijp7ImpvaW50QWNjb3VudCI6ZmFsc2UsImhvbGRlciI6IkhvbGRlclRlc3QiLCJkZXNjcmlwdGlvbiI6ImRlc2NyaXB0aW9uVGVzdCIsImliYW4iOm51bGwsImJpYyI6bnVsbCwiYmFua19uYW1lIjoiQmFua05hbWVUZXN0IiwiY291bnRyeV9pZCI6bnVsbCwiam9pbnRfYWNjb3VudCI6ZmFsc2UsInR5cGUiOm51bGx9LCJiYWxhbmNlIjp7ImJhbGFuY2UiOjEsImxpbWl0IjoyLCJhdmFpbGFibGUiOjAsImN1cnJlbmN5IjpudWxsLCJkYXRlIjpudWxsfSwidHVybm92ZXJzIjpbeyJib29raW5nX2RhdGUiOm51bGwsImFtb3VudCI6MSwiY3VycmVuY3kiOm51bGwsInB1cnBvc2UiOm51bGwsImNvdW50ZXJfaWJhbiI6bnVsbCwiY291bnRlcl9iaWMiOm51bGwsImNvdW50ZXJfaG9sZGVyIjpudWxsLCJwcmVib29rZWQiOmZhbHNlLCJjcmVkaXRvcl9pZCI6bnVsbCwidGFncyI6bnVsbH0seyJib29raW5nX2RhdGUiOm51bGwsImFtb3VudCI6MTAsImN1cnJlbmN5IjpudWxsLCJwdXJwb3NlIjpudWxsLCJjb3VudGVyX2liYW4iOm51bGwsImNvdW50ZXJfYmljIjpudWxsLCJjb3VudGVyX2hvbGRlciI6bnVsbCwicHJlYm9va2VkIjpmYWxzZSwiY3JlZGl0b3JfaWQiOm51bGwsInRhZ3MiOm51bGx9XSwiZGF0ZSI6IjAxLTAxLTIwMjIiLCJkYXlzIjo5MCwiZmlsdGVycyI6bnVsbH0=", actualJson);
    }
}
