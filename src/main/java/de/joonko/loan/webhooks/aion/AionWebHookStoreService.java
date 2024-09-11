package de.joonko.loan.webhooks.aion;

import de.joonko.loan.webhooks.aion.repositories.AionWebHookRepository;
import de.joonko.loan.webhooks.aion.repositories.AionWebhookStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RequiredArgsConstructor
@Service
public class AionWebHookStoreService {

    private final AionWebHookRepository repository;

    public Mono<AionWebhookStore> save(AionWebhookStore aionWebhookStore) {
        return Mono.fromCallable(() -> repository.save(aionWebhookStore))
                .subscribeOn(Schedulers.elastic());
    }
}
