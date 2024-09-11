package de.joonko.loan.partner.aion;

import de.joonko.loan.acceptoffer.domain.AcceptOfferGateway;
import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.db.service.LoanOfferStoreService;
import de.joonko.loan.db.vo.LoanOfferStore;
import de.joonko.loan.partner.aion.model.*;
import de.joonko.loan.partner.aion.model.offerchoice.OfferChoiceRequest;
import de.joonko.loan.partner.aion.model.offerchoice.OfferChoiceTransmissionData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(
        value = "aion.enabled",
        havingValue = "true",
        matchIfMissing = true)
public class AionAcceptOfferGateway implements AcceptOfferGateway<AionAcceptOfferApiMapper, AionAcceptOfferRequest, AionAcceptOfferResponse> {

    private final AionAcceptOfferApiMapper aionAcceptOfferApiMapper;
    private final AionStoreService aionStoreService;
    private final AionClient aionClient;
    private final LoanOfferStoreService loanOfferStoreService;

    @Override
    public AionAcceptOfferApiMapper getMapper() {
        return aionAcceptOfferApiMapper;
    }

    @Override
    public Mono<AionAcceptOfferResponse> callApi(AionAcceptOfferRequest aionAcceptOfferRequest, String applicationId, String loanOfferId) {
        return Mono.zip(aionClient.getToken(applicationId), getProcessId(applicationId), getOfferChoiceRequest(applicationId, loanOfferId))
                .flatMap(tuple -> aionClient.sendOfferChoice(tuple.getT1(), tuple.getT2(), tuple.getT3()))
                .flatMap(resp -> aionStoreService.addRepresentativeId(applicationId, resp.getRepresentativeId()))
                .doOnNext(resp -> log.info("Accepted an offer from Aion for applicationId: {}, loanOfferId: {}", applicationId, loanOfferId))
                .doOnError(e -> log.error("Failed accepting an offer for applicationId: {}, loanOfferId: {}", applicationId, loanOfferId, e))
                .map(any -> new AionAcceptOfferResponse());
    }

    private Mono<OfferChoiceRequest> getOfferChoiceRequest(String applicationId, String loanOfferId) {
        return loanOfferStoreService.findById(loanOfferId)
                .flatMap(loanOfferStore -> getLoanProviderOfferId(loanOfferStore, applicationId))
                .map(this::buildOfferChoiceRequest)
                .doOnError(e -> log.error("Failed getting loanProviderOfferId for applicationId: {}, loanOfferId: {}", applicationId, loanOfferId, e));
    }

    private Mono<String> getLoanProviderOfferId(LoanOfferStore loanOfferStore, String applicationId) {
        return getCreditApplicationResponseStore(applicationId)
                .map(CreditApplicationResponseStore::getOffersProvided)
                .filter(offers -> !offers.isEmpty())
                .flatMapIterable(list -> list)
                .filter(offer -> offer.getOfferDetails().getAmount().intValue() == loanOfferStore.getOffer().getAmount() &&
                        offer.getOfferDetails().getMaturity().equals(loanOfferStore.getOffer().getDurationInMonth()) &&
                        offer.getOfferDetails().getMonthlyInstalmentAmount().equals(loanOfferStore.getOffer().getMonthlyRate()) &&
                        offer.getOfferDetails().getTotalRepaymentAmount().compareTo(loanOfferStore.getOffer().getTotalPayment()) == 0)
                .next()
                .map(offer -> offer.getOfferDetails().getId());
    }

    private Mono<String> getProcessId(String applicationId) {
        return getCreditApplicationResponseStore(applicationId)
                .map(CreditApplicationResponseStore::getProcessId)
                .doOnError(e -> log.error("Failed getting processId for applicationId: {}", applicationId, e));
    }

    private Mono<CreditApplicationResponseStore> getCreditApplicationResponseStore(String applicationId) {
        return Mono.just(applicationId)
                .flatMap(aionStoreService::findByApplicationId)
                .map(Optional::get);
    }

    private OfferChoiceRequest buildOfferChoiceRequest(String loanProviderOfferId) {
        return OfferChoiceRequest.builder()
                        .name(TransmissionDataType.SELECTED_OFFER)
                        .value(OfferChoiceTransmissionData.builder()
                                .selectedOfferId(loanProviderOfferId)
                                .timestamp(LocalDateTime.now())
                                .build())
                .build();
    }

    @Override
    public Mono<String> getLoanProviderReferenceNumber(String applicationId, String loanOfferId) {
        return loanOfferStoreService.findById(loanOfferId)
                .flatMap(loanOfferStore -> getLoanProviderOfferId(loanOfferStore, applicationId));
    }

    @Override
    public Bank getBank() {
        return Bank.AION;
    }
}
