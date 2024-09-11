package de.joonko.loan.webhooks.idnow.service;

import de.joonko.loan.webhooks.idnow.repositores.IdentificationWebHookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
public class IdentificationWebHookStoreService {

    private final IdentificationWebHookRepository identificationWebHookRepository;

    public Mono<Long> deleteByTransactionNumber(final String transactionNumber) {
        return Mono.fromCallable(() -> identificationWebHookRepository.deleteAllByTransactionNumber(transactionNumber))
                .subscribeOn(Schedulers.elastic());
    }
}
