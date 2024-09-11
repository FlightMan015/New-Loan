package de.joonko.loan.integrations.domain.integrationhandler.fts.domain;


import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
@ActiveProfiles("integration")
@ExtendWith({MockitoExtension.class, RandomBeansExtension.class})
public class UserTransactionalDraftDataStoreServiceIT {

    @Autowired
    private UserTransactionalDraftDataStoreService userTransactionalDraftDataStoreService;

    @Test
    void save_succeeds(@Random UserTransactionalDraftDataStore userTransactionalDraftDataStore) {
        // when
        final var saved = userTransactionalDraftDataStoreService.save(userTransactionalDraftDataStore).block();

        // then
        final var fetched = userTransactionalDraftDataStoreService.findById(saved.getUserUUID()).block();

        assertEquals(saved, fetched);
    }
}
