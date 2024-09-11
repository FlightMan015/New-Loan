package de.joonko.loan.webhooks.aion;

import de.joonko.loan.webhooks.aion.repositories.AionWebHookRepository;
import de.joonko.loan.webhooks.aion.repositories.AionWebhookStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AionWebHookStoreServiceTest {

    private AionWebHookStoreService aionWebHookStoreService;

    private AionWebHookRepository aionWebHookRepository;

    @BeforeEach
    void setUp() {
        aionWebHookRepository = mock(AionWebHookRepository.class);

        aionWebHookStoreService = new AionWebHookStoreService(aionWebHookRepository);
    }

    @Test
    void saveAionWebhook() {
        // given
        final var aionWebhookStore = AionWebhookStore.builder().build();
        when(aionWebHookRepository.save(aionWebhookStore)).thenReturn(aionWebhookStore);

        // when
        var aionWebhookMono = aionWebHookStoreService.save(aionWebhookStore);

        // then
        StepVerifier.create(aionWebhookMono).expectNextCount(1).verifyComplete();
    }
}
