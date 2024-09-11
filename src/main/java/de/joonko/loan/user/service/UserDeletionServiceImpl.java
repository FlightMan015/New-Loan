package de.joonko.loan.user.service;

import de.joonko.loan.contract.ContractStorageService;
import de.joonko.loan.contract.model.DocumentDetails;
import de.joonko.loan.db.service.LoanApplicationAuditTrailService;
import de.joonko.loan.db.service.LoanDemandRequestService;
import de.joonko.loan.db.service.LoanDemandStoreService;
import de.joonko.loan.db.service.LoanOfferStoreService;
import de.joonko.loan.db.vo.LoanDemandStore;
import de.joonko.loan.identification.model.IdentificationLink;
import de.joonko.loan.identification.service.IdentificationLinkService;
import de.joonko.loan.integrations.domain.integrationhandler.fts.domain.UserTransactionalDataStoreService;
import de.joonko.loan.integrations.domain.integrationhandler.fts.domain.UserTransactionalDraftDataStoreService;
import de.joonko.loan.partner.consors.ConsorsStoreService;
import de.joonko.loan.partner.santander.SantanderStoreService;
import de.joonko.loan.partner.swk.SwkStoreService;

import de.joonko.loan.user.states.UserStatesStoreService;
import de.joonko.loan.webhooks.idnow.service.IdentificationWebHookStoreService;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static java.util.stream.Collectors.toList;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserDeletionServiceImpl implements UserDeletionService {

    private final LoanDemandStoreService loanDemandStoreService;
    private final LoanApplicationAuditTrailService loanApplicationAuditTrailService;
    private final ConsorsStoreService consorsStoreService;
    private final IdentificationLinkService identificationLinkService;
    private final IdentificationWebHookStoreService identificationWebHookStoreService;
    private final SantanderStoreService santanderStoreService;
    private final SwkStoreService swkStoreService;
    private final UserStatesStoreService userStatesStoreService;
    private final UserAdditionalInfoService userAdditionalInfoService;
    private final UserPersonalInfoService userPersonalInfoService;
    private final UserTransactionalDataStoreService userTransactionalDataStoreService;
    private final UserTransactionalDraftDataStoreService userTransactionalDraftDataStoreService;
    private final LoanOfferStoreService loanOfferStoreService;
    private final LoanDemandRequestService loanDemandRequestService;
    private final ContractStorageService contractStorageService;

    @Override
    public Mono<Void> deleteUser(final String userUuid) {
        return Mono.just(userUuid)
                .doOnNext(userId -> log.info("delete user data for userId: {}", userId))
                .flatMapMany(loanDemandStoreService::findByUserId)
                .map(LoanDemandStore::getApplicationId)
                .flatMap(this::deleteByApplicationId)
                .then(userStatesStoreService.deleteByUserId(userUuid))
                .doOnNext(any -> log.debug("deleted user data for userId: {} from userStatesStore", userUuid))
                .then(userAdditionalInfoService.deleteById(userUuid))
                .doOnNext(any -> log.debug("deleted user data for userId: {} from userAdditionalInformationStore", userUuid))
                .then(userPersonalInfoService.deleteById(userUuid))
                .doOnNext(any -> log.debug("deleted user data for userId: {} from userPersonalInformationStore", userUuid))
                .then(userTransactionalDataStoreService.deleteById(userUuid))
                .doOnNext(any -> log.debug("deleted user data for userId: {} from userTransactionalDataStore", userUuid))
                .then(userTransactionalDraftDataStoreService.deleteById(userUuid))
                .doOnNext(any -> log.debug("deleted user data for userId: {} from userTransactionalDraftDataStore", userUuid))
                .then(deleteContracts(userUuid))
                .thenMany(loanOfferStoreService.deleteAllByUserId(userUuid))
                .doOnNext(any -> log.debug("deleted user data for userId: {} from loanOfferStore", userUuid))
                .thenMany(loanDemandRequestService.deleteAllByUserId(userUuid))
                .doOnNext(any -> log.debug("deleted user data for userId: {} from loanDemandRequest", userUuid))
                .then()
                .doOnError(ex -> log.error("unable to delete user data for userId: {}", userUuid, ex))
                .doOnNext(any -> log.info("deleted user data for userId: {}", userUuid));
    }

    private Mono<Void> deleteByApplicationId(String applicationId) {
        return Mono.just(applicationId)
                .then(loanDemandStoreService.deleteById(applicationId))
                .doOnNext(any -> log.debug("deleted user data for applicationId: {} from loanDemandStore", applicationId))
                .thenMany(consorsStoreService.deletePersonalizedCalculationByApplicationId(applicationId))
                .doOnNext(any -> log.debug("deleted user data for applicationId: {} from personalizedCalculationsStore", applicationId))
                .thenMany(santanderStoreService.deleteByApplicationId(applicationId))
                .doOnNext(any -> log.debug("deleted user data for applicationId: {} from santanderOffer", applicationId))
                .thenMany(swkStoreService.deleteSwkOffer(applicationId))
                .doOnNext(any -> log.debug("deleted user data for applicationId: {} from swkOffer", applicationId))
                .thenMany(swkStoreService.deleteSwkCreditApplication(applicationId))
                .doOnNext(any -> log.debug("deleted user data for applicationId: {} from SwkCreditApplicationOffer", applicationId))
                .thenMany(loanApplicationAuditTrailService.deleteByApplicationId(applicationId))
                .doOnNext(any -> log.debug("deleted user data for applicationId: {} from loanApplicationAuditTrail", applicationId))
                .thenMany(deleteIdentification(applicationId))
                .doOnNext(any -> log.debug("deleted user data for applicationId: {} from identificationLink and identification", applicationId))
                .then();
    }

    private Flux<Long> deleteIdentification(String applicationId) {
        return Mono.just(applicationId)
                .flatMapMany(application -> identificationLinkService.deleteByApplicationId(applicationId))
                .map(IdentificationLink::getExternalIdentId)
                .flatMap(identificationWebHookStoreService::deleteByTransactionNumber);
    }

    private Mono<Void> deleteContracts(String userUuid) {
        return loanOfferStoreService.findLoanOfferStoreByUserUUIDAndContractsExists(userUuid)
                .map(offers -> offers.stream()
                        .flatMap(offer -> offer.getContracts().stream().map(DocumentDetails::getKey))
                        .collect(toList())
                ).flatMap(contractsKeys -> contractStorageService.deleteContracts(contractsKeys, userUuid));
    }
}
