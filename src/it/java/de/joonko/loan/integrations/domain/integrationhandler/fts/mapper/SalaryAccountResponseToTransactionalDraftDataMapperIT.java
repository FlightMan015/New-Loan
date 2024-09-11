package de.joonko.loan.integrations.domain.integrationhandler.fts.mapper;


import de.joonko.loan.avro.dto.salary_account.QuerySalaryAccountResponse;
import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("integration")
@ExtendWith({MockitoExtension.class, RandomBeansExtension.class})
public class SalaryAccountResponseToTransactionalDraftDataMapperIT {

    @Autowired
    private SalaryAccountResponseToTransactionalDraftDataMapper mapper;

    @Test
    void mapping_succeeds(@Random QuerySalaryAccountResponse querySalaryAccountResponse) {
        // when
        final var currentDateInMillis = Instant.now();
        final var currentDate = LocalDate.ofInstant(currentDateInMillis, TimeZone.getDefault().toZoneId());
        querySalaryAccountResponse.getBalance().setDate(currentDate + " " + "00:00:00");
        querySalaryAccountResponse.getTransactions().forEach(transaction -> transaction.setBookingDate(Instant.now().toEpochMilli()));
        final var result = mapper.map(querySalaryAccountResponse);

        // then
        assertAll(
                () -> assertEquals(querySalaryAccountResponse.getAccountInternalId(), result.getAccountInternalId()),
                () -> assertEquals(querySalaryAccountResponse.getUserUUID(), result.getUserUUID()),
                () -> assertEquals(querySalaryAccountResponse.getAccount().getIban(), result.getAccount().getIban()),
                () -> assertEquals(querySalaryAccountResponse.getAccount().getBic(), result.getAccount().getBic()),
                () -> assertEquals(querySalaryAccountResponse.getAccount().getBankName(), result.getAccount().getBankName()),
                () -> assertEquals(querySalaryAccountResponse.getAccount().getCountryId(), result.getAccount().getCountryId()),
                () -> assertEquals(querySalaryAccountResponse.getAccount().getDescription(), result.getAccount().getDescription()),
                () -> assertEquals(querySalaryAccountResponse.getAccount().getHolder(), result.getAccount().getHolder()),
                () -> assertEquals(querySalaryAccountResponse.getBalance().getBalance(), result.getBalance().getBalance()),
                () -> assertEquals(querySalaryAccountResponse.getBalance().getLimit(), result.getBalance().getLimit()),
                () -> assertEquals(querySalaryAccountResponse.getBalance().getAvailable(), result.getBalance().getAvailable()),
                () -> assertEquals(querySalaryAccountResponse.getBalance().getCurrency(), result.getBalance().getCurrency()),
                () -> assertEquals(currentDate, result.getBalance().getDate()),
                () -> assertTrue(result.getTransactions()
                        .stream().allMatch(transaction -> querySalaryAccountResponse.getTransactions().stream()
                                .anyMatch(initialTransaction ->
                                        initialTransaction.getAmount() == transaction.getAmount() &&
                                                Objects.equals(initialTransaction.getCurrency(), transaction.getCurrency()) &&
                                                Objects.equals(initialTransaction.getPartnerName(), transaction.getPartnerName()) &&
                                                Objects.equals(LocalDate.ofInstant(Instant.ofEpochSecond(initialTransaction.getBookingDate()),
                                                        TimeZone.getDefault().toZoneId()), transaction.getBookingDate()) &&
                                                Objects.equals(initialTransaction.getBookingPurpose(), transaction.getPurpose())
                                )))
        );
    }
}
