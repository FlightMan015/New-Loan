package de.joonko.loan.integrations.domain.integrationhandler.fts.mapper;

import de.joonko.loan.integrations.domain.integrationhandler.fts.domain.UserTransactionalDraftDataStore;
import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("integration")
@ExtendWith({MockitoExtension.class, RandomBeansExtension.class})
public class TransactionalDraftDataToFtsRequestMapperIT {


    @Autowired
    private TransactionalDraftDataToFtsRequestMapper mapper;

    @Test
    void mapping_succeeds(@Random UserTransactionalDraftDataStore transactionalDraftDataStore) {
        // when
        final var result = mapper.map(transactionalDraftDataStore);

        // then
        assertAll(
                () -> assertEquals(transactionalDraftDataStore.getAccountInternalId(), result.getAccountInternalId()),
                () -> assertEquals(transactionalDraftDataStore.getUserUUID(), result.getUserUUID()),
                () -> assertEquals(transactionalDraftDataStore.getAccount().getIban(), result.getAccount().getIban()),
                () -> assertEquals(transactionalDraftDataStore.getAccount().getBic(), result.getAccount().getBic()),
                () -> assertEquals(transactionalDraftDataStore.getAccount().getJointAccount(), result.getAccount().getIsJointlyManaged()),
                () -> assertEquals(transactionalDraftDataStore.getAccount().getBankName(), result.getAccount().getBankName()),
                () -> assertEquals(transactionalDraftDataStore.getAccount().getCountryId(), result.getAccount().getCountryId()),
                () -> assertEquals(transactionalDraftDataStore.getAccount().getDescription(), result.getAccount().getDescription()),
                () -> assertEquals(transactionalDraftDataStore.getAccount().getHolder(), result.getAccount().getHolder()),
                () -> assertEquals(transactionalDraftDataStore.getBalance().getBalance(), result.getBalance().getBalance()),
                () -> assertEquals(transactionalDraftDataStore.getBalance().getLimit(), result.getBalance().getLimit()),
                () -> assertEquals(transactionalDraftDataStore.getBalance().getAvailable(), result.getBalance().getAvailable()),
                () -> assertEquals(transactionalDraftDataStore.getBalance().getCurrency(), result.getBalance().getCurrency()),
                () -> assertEquals(transactionalDraftDataStore.getBalance().getDate(), result.getBalance().getDate()),
                () -> assertTrue(result.getTransactions()
                        .stream().allMatch(transaction -> transactionalDraftDataStore.getTransactions().stream()
                                .anyMatch(initialTransaction ->
                                        Objects.equals(initialTransaction.getAmount(), transaction.getAmount()) &&
                                                Objects.equals(initialTransaction.getCurrency(), transaction.getCurrency()) &&
                                                Objects.equals(initialTransaction.getPartnerName(), transaction.getPartnerName()) &&
                                                Objects.equals(initialTransaction.getBookingDate(), transaction.getBookingDate()) &&
                                                Objects.equals(initialTransaction.getPurpose(), transaction.getPurpose())
                                )))
        );
    }
}
