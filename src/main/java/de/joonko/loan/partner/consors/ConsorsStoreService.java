package de.joonko.loan.partner.consors;

import de.joonko.loan.partner.PersonalizedCalculationStoreRepository;
import de.joonko.loan.partner.consors.model.ConsorsAcceptOfferResponse;
import de.joonko.loan.partner.consors.model.LinkRelation;
import de.joonko.loan.partner.consors.model.PersonalizedCalculationsResponse;

import org.springframework.stereotype.Service;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConsorsStoreService {

    private final ConsorsAcceptedOfferRepository consorsAcceptedOfferRepository;
    private final PersonalizedCalculationStoreRepository personalizedCalculationStoreRepository;

    public Flux<PersonalizedCalculationsStore> deletePersonalizedCalculationByApplicationId(final String applicationId) {
        return Mono.fromCallable(() -> personalizedCalculationStoreRepository.deleteByApplicationId(applicationId))
                .flatMapIterable(list -> list)
                .subscribeOn(Schedulers.elastic());
    }

    public void saveAcceptedOffer(String applicationId, ConsorsAcceptOfferResponse consorsAcceptOfferResponse) {
        log.info("Saving accepted offer in Consors data store for applicationId {} ", applicationId);
        ConsorsAcceptedOfferStore acceptedOfferStore = ConsorsAcceptedOfferStore.builder()
                .loanApplicationId(applicationId)
                .consorsAcceptOfferResponse(consorsAcceptOfferResponse)
                .build();
        consorsAcceptedOfferRepository.save(acceptedOfferStore);
    }

    public String getContractIdForApplicationid(String applicationId) {
        log.info("Getting contract id for applicationId {} ", applicationId);
        ConsorsAcceptedOfferStore consorsAcceptedOfferStore = consorsAcceptedOfferRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("applicationId not found in database"));
        return consorsAcceptedOfferStore.getConsorsAcceptOfferResponse().getContractIdentifier();
    }

    public String getLoanApplicationIdByContractIdentifier(String contractIdentifier) {
        log.info("Getting applicationId for contract id {} ", contractIdentifier);
        return consorsAcceptedOfferRepository.findByConsorsAcceptOfferResponse_ContractIdentifier(contractIdentifier)
                .orElseThrow(() -> new RuntimeException("applicationId not found in database")).getLoanApplicationId();
    }

    public String getDownloadSubscriptionDocumentLinkForApplicationId(String applicationId) {
        log.info("Getting applicationId {} ", applicationId);
        ConsorsAcceptedOfferStore consorsAcceptedOfferStore = consorsAcceptedOfferRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("applicationId not found in database"));
        return consorsAcceptedOfferStore.getDownloadSubscriptionDocumentLink();
    }

    public Mono<ConsorsAcceptedOfferStore> findById(final String applicationId) {
        return Mono.fromCallable(() -> consorsAcceptedOfferRepository.findById(applicationId))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .subscribeOn(Schedulers.elastic());
    }

    public Mono<String> getKYCLinkForApplicationId(final String applicationId) {
        return findById(applicationId)
                .map(ConsorsAcceptedOfferStore::getKYCLink);
    }

    public void savePersonalizedCalculations(String applicationId, PersonalizedCalculationsResponse personalizedCalculationsResponse) {
        log.info("Saving personalizedCalculationsResponse Consors data store for applicationId  {} ", applicationId);
        PersonalizedCalculationsStore personalizedCalculationsStore = PersonalizedCalculationsStore.builder()
                .applicationId(applicationId)
                .personalizedCalculationsResponse(personalizedCalculationsResponse)
                .build();
        personalizedCalculationStoreRepository.save(personalizedCalculationsStore);

    }

    public LinkRelation getFinalizeSubscriptionLinkForApplicationId(String applicationId) {
        log.info("Getting  FinalizeSubscriptionLink applicationId  {} ", applicationId);
        PersonalizedCalculationsStore personalizedCalculationsStore = personalizedCalculationStoreRepository.findByApplicationId(applicationId)
                .orElseThrow(() -> new RuntimeException("applicationId not found in database"));
        return personalizedCalculationsStore.getFinalizeSubscriptionLink();
    }

    public Mono<String> getConsorsContractIdentifier(String applicationId) {
        log.info("Getting LoanProviderContractReferenceNumber for applicationId {} ", applicationId);

        return Mono.fromCallable(() -> consorsAcceptedOfferRepository.findById(applicationId)
                        .orElseThrow(() -> new RuntimeException("Not able to find ConsorsAcceptedOfferStore for applicationId: " + applicationId)))
                .map(acceptedOfferStore -> acceptedOfferStore.getConsorsAcceptOfferResponse().getContractIdentifier())
                .subscribeOn(Schedulers.elastic());
    }
}
