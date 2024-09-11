package de.joonko.loan.webhooks.aion;

import de.joonko.loan.acceptoffer.api.OfferRequestMapper;
import de.joonko.loan.acceptoffer.domain.LoanApplicationStatus;
import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.db.service.LoanOfferStoreService;
import de.joonko.loan.db.vo.LoanOfferStore;
import de.joonko.loan.offer.ResourceNotFoundException;
import de.joonko.loan.offer.domain.OfferStatusUpdateService;
import de.joonko.loan.partner.aion.AionStoreService;
import de.joonko.loan.partner.aion.model.CreditApplicationResponseStore;
import de.joonko.loan.webhooks.aion.mapper.AionWebhookStoreMapper;
import de.joonko.loan.webhooks.aion.model.AionWebhookRequest;
import de.joonko.loan.webhooks.aion.model.AionWebhookType;
import de.joonko.loan.webhooks.aion.repositories.AionWebhookStore;

import org.springframework.stereotype.Service;

import java.util.Objects;

import javax.validation.constraints.NotNull;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class AionWebHookService {

    public static final String CASHLOAN_OPEN = "credits.cashloan.loanopen";
    private final AionWebHookStoreService aionWebHookStoreService;
    private final LoanOfferStoreService loanOfferStoreService;
    private final OfferRequestMapper offerRequestMapper;
    private final AionWebhookStoreMapper aionWebhookStoreMapper;
    private final OfferStatusUpdateService offerStatusUpdateService;
    private final AionStoreService aionStoreService;

    public Mono<Void> save(@NotNull final AionWebhookRequest aionWebhookRequest) {
        return saveWebhookRequest(aionWebhookRequest)
                .map(aionWebhookStore -> aionWebhookStore.getPayload().getProcessInstanceId())
                .doOnNext(processId -> log.debug("saved webhook with processId: {}", processId))
                .flatMap(this::findAcceptedOfferInNonFinalState)
                .map(offerRequestMapper::fromRequest)
                .zipWhen(offerRequest -> Mono.just(aionWebhookStoreMapper.mapStatus(AionWebhookType.fromValue(aionWebhookRequest.getType()).get(), aionWebhookRequest.getPayload().getStatus())))
                .flatMap(tuple -> offerStatusUpdateService.updateOfferStatus(tuple.getT1(), tuple.getT2()))
                .doOnNext(loanOfferStore -> log.debug("updated offer status for processId: {}", aionWebhookRequest.getPayload().getProcessInstanceId()))
                .then();
    }

    private Mono<AionWebhookStore> saveWebhookRequest(AionWebhookRequest aionWebhookRequest) {
        return Mono.just(aionWebhookRequest)
                .filter(request -> AionWebhookType.fromValue(request.getType()).isPresent())
                .map(aionWebhookStoreMapper::map)
                .flatMap(aionWebHookStoreService::save);
    }

    private Mono<LoanOfferStore> findAcceptedOfferInNonFinalState(String processId) {
        return aionStoreService.findByProcessId(processId)
                .map(CreditApplicationResponseStore::getApplicationId)
                .flatMap(applicationId -> loanOfferStoreService.findAcceptedByApplicationIdAndBankProvider(applicationId, Bank.AION))
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Unable to find offer for processId: " + processId)))
                .filter(this::offerStatusIsNullOrNotFinal)
                .doOnNext(loanOfferStore -> log.debug("found offer with loanOfferId: {}, processId: {}", loanOfferStore.getLoanOfferId(), processId));
    }

    private boolean offerStatusIsNullOrNotFinal(LoanOfferStore offer) {
        return Objects.isNull(offer.getOfferStatus()) ||
                LoanApplicationStatus.fromValue(offer.getOfferStatus())
                        .map(status -> !LoanApplicationStatus.getFinalOfferStatuses().contains(status))
                        .orElse(true);
    }
}
