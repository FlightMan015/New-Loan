package de.joonko.loan.identification.service;

import de.joonko.loan.acceptoffer.domain.AcceptOfferService;
import de.joonko.loan.common.utils.CommonUtils;
import de.joonko.loan.contract.ContractStorageService;
import de.joonko.loan.db.repositories.LoanOfferStoreRepository;
import de.joonko.loan.db.service.LoanDemandStoreService;
import de.joonko.loan.db.service.LoanOfferStoreService;
import de.joonko.loan.db.vo.LoanOfferStore;
import de.joonko.loan.identification.model.CreateIdentRequest;
import de.joonko.loan.identification.model.GetIdentStatusResponse;
import de.joonko.loan.identification.model.GetKycUrlResponse;
import de.joonko.loan.identification.model.GetOfferContractsResponse;
import de.joonko.loan.identification.model.IdentificationLink;
import de.joonko.loan.identification.model.KycStatus;
import de.joonko.loan.identification.model.StartIdentResponse;
import de.joonko.loan.integrations.domain.integrationhandler.userpersonal.mapper.UserPersonalInformationMapper;
import de.joonko.loan.user.service.UserAdditionalInformationService;
import de.joonko.loan.user.service.UserPersonalInfoService;
import de.joonko.loan.user.service.UserPersonalInformationRepository;

import org.bouncycastle.util.Strings;
import org.springframework.stereotype.Service;

import java.util.Objects;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class IdentificationService {
    private final IdentServiceFactory identServiceFactory;
    private final IdentificationAuditService identificationAuditService;
    private final LoanOfferStoreService loanOfferStoreService;
    private final LoanDemandStoreService loanDemandStoreService;
    private final IdentificationLinkService identificationLinkService;
    private final LoanOfferStoreRepository loanOfferStoreRepository;
    private final UserPersonalInformationRepository userPersonalInformationRepository;
    private final LoanOfferStoreRepository offerStoreRepository;
    private final UserPersonalInformationMapper userPersonalInformationMapper;
    private final AcceptOfferService acceptOfferService;
    private final UserPersonalInfoService userPersonalInfoService;
    private final UserAdditionalInformationService userAdditionalInformationService;
    private final ContractStorageService contractStorageService;


    public Mono<StartIdentResponse> createIdentification(String userUUID, String loanOfferId, String applicationId) {
        return loanOfferStoreService.findById(loanOfferId)
                .flatMap(loanOfferStore -> {
                    if (loanOfferStore.identificationAlreadyPassedAndContractsAvailable()) {
                        return Mono.just(loanOfferStore)
                                .map(lo -> StartIdentResponse.builder()
                                        .kycUrl(lo.getKycUrl())
                                        .kycProvider(lo.getKycProvider())
                                        .build());
                    }
                    return constructCreateIdentRequestForUser(loanOfferStore, userUUID, applicationId)
                            .flatMap(this::createIdentification)
                            .flatMap(this::initiateKYCProcess
                            );
                });
    }

    public Mono<StartIdentResponse> createIdentificationV2(final String userUUID, final String loanOfferId, final String applicationId) {
        return loanOfferStoreService.findById(loanOfferId)
                .flatMap(loanOfferStore -> {
                    if (offerNotBelongToUserOrApplication(userUUID, applicationId, loanOfferStore)) {
                        return Mono.error(new IllegalStateException(String.format("user %s is not allowed to start identification for offer %s", userUUID, loanOfferId)));
                    }
                    return Mono.just(loanOfferStore);
                })
                .flatMap(loanOfferStore -> {
                    if (kycAlreadyInitiated(loanOfferStore)) {
                        return Mono.just(buildKycResponseFromOOffer(loanOfferStore));
                    }
                    return initiateKYCProcess(loanOfferStore);
                });
    }

    private boolean kycAlreadyInitiated(LoanOfferStore loanOfferStore) {
        return Objects.nonNull(loanOfferStore.getKycStatus()) && Objects.nonNull(loanOfferStore.getKycProvider());
    }

    public Mono<GetOfferContractsResponse> acceptAndGetContracts(final String loanOfferId, final String userUUID) {
        return acceptOfferService.acceptOfferByUser(loanOfferId, userUUID)
                .flatMap(loanOfferStore -> {
                    if (loanOfferStore.identificationAlreadyPassedAndContractsAvailable()) {
                        return Mono.just(loanOfferStore);
                    }
                    return constructCreateIdentRequestForUser(loanOfferStore, userUUID, loanOfferStore.getApplicationId())
                            .flatMap(this::createIdentification);
                })
                .zipWhen(response -> contractStorageService.preSignContracts(response.getContracts(), response.getUserUUID()))
                .map(tuple -> GetOfferContractsResponse.builder()
                        .kycURL(tuple.getT1().getKycUrl())
                        .contracts(tuple.getT2())
                        .build());
    }

    public Mono<GetOfferContractsResponse> acceptAndCreateIdentificationByInternalUser(String loanOfferId) {
        return acceptOfferService.acceptOfferByInternalUser(loanOfferId)
                .flatMap(loanOfferStore -> {
                    if (loanOfferStore.identificationAlreadyPassedAndContractsAvailable()) {
                        return Mono.just(loanOfferStore);
                    }
                    return constructCreateIdentRequestForInternal(loanOfferStore)
                            .flatMap(this::createIdentification)
                            .flatMap(loanStore -> initiateKYCProcess(loanStore)
                                    .map(any -> loanStore)
                            );
                })
                .zipWhen(response -> contractStorageService.preSignContracts(response.getContracts(), response.getUserUUID()))
                .map(tuple -> GetOfferContractsResponse.builder()
                        .kycURL(tuple.getT1().getKycUrl())
                        .contracts(tuple.getT2())
                        .build());
    }

    private Mono<CreateIdentRequest> constructCreateIdentRequestForUser(final LoanOfferStore loanOffer, String userUUID, String applicationId) {
        return Mono.just(loanOffer)
                .flatMap(loanOfferStore -> {
                    if (offerNotBelongToUserOrApplication(userUUID, applicationId, loanOfferStore)) {
                        return Mono.error(new IllegalStateException(String.format("user %s is not allowed to accept offer %s application %s", userUUID, loanOffer.getLoanOfferId(), applicationId)));
                    }
                    return Mono.just(loanOfferStore);
                })
                .zipWhen(loanOfferStore -> userPersonalInfoService.findById(userUUID))
                .zipWhen(tuple -> userAdditionalInformationService.findById(userUUID))
                .map(tuple2 -> userPersonalInformationMapper.from(tuple2.getT1().getT2(), tuple2.getT1().getT1(), tuple2.getT2()));
    }

    private Mono<CreateIdentRequest> constructCreateIdentRequestForInternal(final LoanOfferStore loanOfferStore) {
        return Mono.zip(userPersonalInfoService.findById(loanOfferStore.getUserUUID()),
                        userAdditionalInformationService.findById(loanOfferStore.getUserUUID()))
                .map(tuple2 -> userPersonalInformationMapper.from(tuple2.getT1(), loanOfferStore, tuple2.getT2()));
    }

    private Mono<LoanOfferStore> createIdentification(CreateIdentRequest createIdentRequest) {
        IdentService identService = identServiceFactory.getIdentService(createIdentRequest.getLoanProvider());

        return identService.createIdent(createIdentRequest)
                .doOnSuccess(createIdentResponse -> identificationAuditService.kycLinkCreated(createIdentResponse, createIdentRequest))
                .flatMap(response ->
                        loanOfferStoreService.findById(createIdentRequest.getLoanOfferId())
                                .map(loanOfferStore -> {
                                    loanOfferStore.setKycUrl(response.getKycUrl());
                                    return loanOfferStore;
                                })
                                .flatMap(loanOfferStoreService::saveOffer)
                                .flatMap(loanOfferStore ->
                                        contractStorageService.storeContracts(response.getDocuments(), loanOfferStore.getUserUUID(), loanOfferStore.getApplicationId(), loanOfferStore.getLoanOfferId())
                                                .map(documents -> {
                                                    loanOfferStore.setContracts(documents);
                                                    return loanOfferStore;
                                                })
                                )
                                .flatMap(loanOfferStoreService::saveOffer)
                );
    }

    private Mono<StartIdentResponse> initiateKYCProcess(final LoanOfferStore loanOfferStore) {
        IdentService identService = identServiceFactory.getIdentService(loanOfferStore.getOffer().getLoanProvider().getName());

        identificationAuditService.kycInitiated(identService.getProvider().name(), loanOfferStore.getApplicationId());

        return Mono.just(loanOfferStore)
                .flatMap(loanOffer -> {
                    loanOffer.setKycStatus(Strings.toUpperCase(KycStatus.INITIATED.name()));
                    loanOfferStore.setOfferStatus(Strings.toUpperCase(KycStatus.INITIATED.name()));
                    loanOfferStore.setKycProvider(identService.getProvider());
                    return loanOfferStoreService.saveOffer(loanOffer);
                })
                .map(this::buildKycResponseFromOOffer);
    }

    private StartIdentResponse buildKycResponseFromOOffer(final LoanOfferStore offer) {
        return StartIdentResponse.builder()
                .kycProvider(offer.getKycProvider())
                .kycUrl(offer.getKycUrl())
                .build();
    }

    public Mono<GetIdentStatusResponse> getIdentificationStatus(String externalIdentId) {
        IdentificationLink identificationLink = identificationLinkService.getByExternalIdentId(externalIdentId);
        String applicationId = identificationLink.getApplicationId();
        String loanProvider = identificationLink.getLoanProvider();
        String dacId = loanDemandStoreService.getDacId(applicationId);
        CommonUtils.loadLogContext(applicationId, dacId);

        log.info("Getting identification status for provider {}", loanProvider);
        IdentService identService = identServiceFactory.getIdentService(identificationLink.getLoanProvider());

        return identService.getIdentStatus(externalIdentId)
                .map(status -> {
                    LoanOfferStore loanOfferStore = offerStoreRepository.findById(identificationLink.getOfferId()).orElseThrow();
                    String firstName = userPersonalInformationRepository.findById(loanOfferStore.getUserUUID()).orElseThrow().getFirstName();
                    return GetIdentStatusResponse.builder().status(status).firstName(firstName).loanProvider(loanProvider).kycProvider(identService.getProvider()).build();
                }).doOnSuccess(identificationStatus -> log.info("Received identification status for applicationId {} and status is {} ", applicationId, identificationStatus.getStatus()))
                .doOnSuccess(getIdentStatusResponse -> {
                    LoanOfferStore loanOfferStore = loanOfferStoreService.findByLoanOfferId(identificationLink.getOfferId());
                    loanOfferStore.setOfferStatus(getIdentStatusResponse.getStatus());
                    loanOfferStore.setKycProvider(getIdentStatusResponse.getKycProvider());
                    loanOfferStoreRepository.save(loanOfferStore);
                });
    }

    public Mono<GetKycUrlResponse> getKycUrl(String externalIdentId) {
        IdentificationLink identificationLink = identificationLinkService.getByExternalIdentId(externalIdentId);
        String applicationId = identificationLink.getApplicationId();
        String dacId = loanDemandStoreService.getDacId(applicationId);
        CommonUtils.loadLogContext(applicationId, dacId);

        return Mono.just(GetKycUrlResponse.builder()
                .kycUrl(identificationLink.getKycUrl())
                .kycProvider(identificationLink.getIdentProvider())
                .loanProvider(identificationLink.getLoanProvider()).build());
    }

    private boolean offerNotBelongToUserOrApplication(final String userUUID, final String applicationId, final LoanOfferStore loanOfferStore) {
        return !Objects.equals(loanOfferStore.getApplicationId(), applicationId)
                || !Objects.equals(loanOfferStore.getUserUUID(), userUUID)
                || !Boolean.TRUE.equals(loanOfferStore.getIsAccepted());
    }

}
