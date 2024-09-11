package de.joonko.loan.integrations.domain.enhancers;

import de.joonko.loan.db.service.LoanOfferStoreService;
import de.joonko.loan.db.vo.LoanOfferStore;
import de.joonko.loan.integrations.domain.integrationhandler.fts.domain.UserTransactionalDataStoreService;
import de.joonko.loan.integrations.model.OfferRequest;
import de.joonko.loan.offer.api.AccountDetails;
import de.joonko.loan.offer.api.LoanOffer;
import de.joonko.loan.offer.api.LoanProvider;
import de.joonko.loan.offer.api.mapper.LoanOfferStoreMapperImpl;
import de.joonko.loan.offer.api.model.OfferResponseState;
import de.joonko.loan.integrations.domain.OffersStateValidator;
import de.joonko.loan.data.support.DataSolutionCommunicationManager;
import de.joonko.loan.user.service.UserPersonalInfoService;
import de.joonko.loan.user.service.UserPersonalInformationStore;
import de.joonko.loan.user.states.OfferDataStateDetails;
import de.joonko.loan.user.states.UserStatesStore;
import de.joonko.loan.user.states.UserStatesStoreService;
import de.joonko.loan.integrations.domain.integrationhandler.fts.domain.UserTransactionalDataStore;
import de.joonko.loan.integrations.domain.integrationhandler.fts.domain.UserTransactionalDataStoreMapper;

import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import javax.annotation.Resource;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(RandomBeansExtension.class)
class OffersReadyResponseEnhancerTest {

    @InjectMocks
    @Resource
    private OffersReadyResponseEnhancer offersReadyResponseEnhancer;
    @Mock
    private UserPersonalInfoService userPersonalInfoService;
    @Mock
    private DataSolutionCommunicationManager dataSolutionCommunicationManager;
    @Mock
    private UserTransactionalDataStoreService userTransactionalDataStoreService;
    @Mock
    private UserTransactionalDataStoreMapper userTransactionalDataStoreMapper;
    @Spy
    private LoanOfferStoreMapperImpl loanOfferStoreMapper;
    @Mock
    private UserStatesStoreService userStatesStoreService;
    @Mock
    private LoanOfferStoreService loanOfferStoreService;
    @Mock
    private OffersStateValidator offersStateValidator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    void buildResponseData_noUserStateStore_returnsEmpty(@Random OfferRequest offerRequest) {
        // when
        when(userStatesStoreService.findById(offerRequest.getUserUUID())).thenReturn(Mono.empty());

        final var result = offersReadyResponseEnhancer.buildResponseData(offerRequest);

        // then
        assertAll(
                () -> StepVerifier.create(result).expectNextCount(0).verifyComplete(),
                () -> verifyNoInteractions(loanOfferStoreService),
                () -> verifyNoInteractions(dataSolutionCommunicationManager),
                () -> verifyNoInteractions(userTransactionalDataStoreMapper),
                () -> verifyNoInteractions(loanOfferStoreMapper)
        );
    }

    @Test
    void buildResponseData_userStateStore_returnsEmptyOfferDataState(@Random OfferRequest offerRequest) {
        // given
        final var userStatesStore = new UserStatesStore();
        userStatesStore.setOfferDateStateDetailsSet(Set.of());

        // when
        when(userStatesStoreService.findById(offerRequest.getUserUUID())).thenReturn(Mono.just(userStatesStore));
        when(userTransactionalDataStoreService.getKycRelatedPersonalDetails(offerRequest.getUserUUID())).thenReturn(Mono.empty());
        when(userPersonalInfoService.findById(offerRequest.getUserUUID())).thenReturn(Mono.empty());

        final var result = offersReadyResponseEnhancer.buildResponseData(offerRequest);

        // then
        assertAll(
                () -> StepVerifier.create(result).expectNextCount(0).verifyComplete(),
                () -> verifyNoInteractions(loanOfferStoreService),
                () -> verifyNoInteractions(dataSolutionCommunicationManager),
                () -> verifyNoInteractions(userTransactionalDataStoreMapper),
                () -> verifyNoInteractions(loanOfferStoreMapper)
        );
    }

    @Test
    void buildResponseData_noSameAmountOffer(@Random OfferRequest offerRequest) {
        // given
        final var userStatesStore = new UserStatesStore();
        final var offerDataStateDetails = OfferDataStateDetails.builder().amount(offerRequest.getRequestedAmount() * 2).build();
        userStatesStore.setOfferDateStateDetailsSet(Set.of(offerDataStateDetails));

        // when
        when(userStatesStoreService.findById(offerRequest.getUserUUID())).thenReturn(Mono.just(userStatesStore));
        when(userTransactionalDataStoreService.getKycRelatedPersonalDetails(offerRequest.getUserUUID())).thenReturn(Mono.empty());
        when(userPersonalInfoService.findById(offerRequest.getUserUUID())).thenReturn(Mono.empty());

        final var result = offersReadyResponseEnhancer.buildResponseData(offerRequest);

        // then
        assertAll(
                () -> StepVerifier.create(result).expectNextCount(0).verifyComplete(),
                () -> verifyNoInteractions(loanOfferStoreService),
                () -> verifyNoInteractions(dataSolutionCommunicationManager),
                () -> verifyNoInteractions(userTransactionalDataStoreMapper),
                () -> verifyNoInteractions(loanOfferStoreMapper)
        );
    }

    @Test
    void buildResponseData_sameAmountOffer_notRecommended_samePurpose_butExpired(@Random OfferRequest offerRequest) {
        // given
        final var userStatesStore = new UserStatesStore();
        final var offerDataStateDetails = OfferDataStateDetails.builder()
                .amount(offerRequest.getRequestedAmount())
                .purpose(offerRequest.getRequestedPurpose())
                .parentApplicationId(null)
                .expired(true)
                .build();
        userStatesStore.setOfferDateStateDetailsSet(Set.of(offerDataStateDetails));

        // when
        when(userStatesStoreService.findById(offerRequest.getUserUUID())).thenReturn(Mono.just(userStatesStore));
        when(userTransactionalDataStoreService.getKycRelatedPersonalDetails(offerRequest.getUserUUID())).thenReturn(Mono.empty());
        when(userPersonalInfoService.findById(offerRequest.getUserUUID())).thenReturn(Mono.empty());

        final var result = offersReadyResponseEnhancer.buildResponseData(offerRequest);

        // then
        assertAll(
                () -> StepVerifier.create(result).expectNextCount(0).verifyComplete(),
                () -> verifyNoInteractions(loanOfferStoreService),
                () -> verifyNoInteractions(dataSolutionCommunicationManager),
                () -> verifyNoInteractions(userTransactionalDataStoreMapper),
                () -> verifyNoInteractions(loanOfferStoreMapper)
        );
    }

    @Test
    void buildResponseData_sameAmountOffer_notRecommended_no_purpose(@Random OfferRequest offerRequest) {
        // given
        final var userStatesStore = new UserStatesStore();
        final var offerDataStateDetails = OfferDataStateDetails.builder()
                .amount(offerRequest.getRequestedAmount())
                .parentApplicationId(null)
                .expired(false)
                .build();
        userStatesStore.setOfferDateStateDetailsSet(Set.of(offerDataStateDetails));

        // when
        when(userStatesStoreService.findById(offerRequest.getUserUUID())).thenReturn(Mono.just(userStatesStore));
        when(userTransactionalDataStoreService.getKycRelatedPersonalDetails(offerRequest.getUserUUID())).thenReturn(Mono.empty());
        when(userPersonalInfoService.findById(offerRequest.getUserUUID())).thenReturn(Mono.empty());

        final var result = offersReadyResponseEnhancer.buildResponseData(offerRequest);

        // then
        assertAll(
                () -> StepVerifier.create(result).expectNextCount(0).verifyComplete(),
                () -> verifyNoInteractions(loanOfferStoreService),
                () -> verifyNoInteractions(dataSolutionCommunicationManager),
                () -> verifyNoInteractions(userTransactionalDataStoreMapper),
                () -> verifyNoInteractions(loanOfferStoreMapper)
        );
    }

    @Test
    void buildResponseData_sameAmountOffer_notRecommended_different_purpose(@Random OfferRequest offerRequest) {
        // given
        final var userStatesStore = new UserStatesStore();
        final var offerDataStateDetails = OfferDataStateDetails.builder()
                .amount(offerRequest.getRequestedAmount())
                .purpose("phone2515")
                .parentApplicationId(null)
                .expired(false)
                .build();
        userStatesStore.setOfferDateStateDetailsSet(Set.of(offerDataStateDetails));

        // when
        when(userStatesStoreService.findById(offerRequest.getUserUUID())).thenReturn(Mono.just(userStatesStore));
        when(userTransactionalDataStoreService.getKycRelatedPersonalDetails(offerRequest.getUserUUID())).thenReturn(Mono.empty());
        when(userPersonalInfoService.findById(offerRequest.getUserUUID())).thenReturn(Mono.empty());

        final var result = offersReadyResponseEnhancer.buildResponseData(offerRequest);

        // then
        assertAll(
                () -> StepVerifier.create(result).expectNextCount(0).verifyComplete(),
                () -> verifyNoInteractions(loanOfferStoreService),
                () -> verifyNoInteractions(dataSolutionCommunicationManager),
                () -> verifyNoInteractions(userTransactionalDataStoreMapper),
                () -> verifyNoInteractions(loanOfferStoreMapper)
        );
    }

    @Test
    void buildResponseData_empty_offers_list(@Random OfferRequest offerRequest) {
        // given
        final var applicationId = "a";
        final var userStatesStore = new UserStatesStore();
        final var offerDataStateDetails = OfferDataStateDetails.builder()
                .amount(offerRequest.getRequestedAmount())
                .purpose(offerRequest.getRequestedPurpose())
                .applicationId(applicationId)
                .parentApplicationId(null)
                .expired(false)
                .responseDateTime(OffsetDateTime.now())
                .build();
        userStatesStore.setOfferDateStateDetailsSet(Set.of(offerDataStateDetails));
        final List<LoanOfferStore> offers = List.of();

        final var userTransactionalDataStore = new UserTransactionalDataStore();
        userTransactionalDataStore.setAccountDetails(AccountDetails.builder().build());

        final var userPersonalDataStore = new UserPersonalInformationStore();
        final var kycRelatedPersonalDetails = new KycRelatedPersonalDetails();

        // when
        when(userStatesStoreService.findById(offerRequest.getUserUUID())).thenReturn(Mono.just(userStatesStore));
        when(userTransactionalDataStoreService.getKycRelatedPersonalDetails(offerRequest.getUserUUID())).thenReturn(Mono.just(userTransactionalDataStore));
        when(userPersonalInfoService.findById(offerRequest.getUserUUID())).thenReturn(Mono.just(userPersonalDataStore));
        when(userTransactionalDataStoreMapper.mapToKycRelatedPersonalDetails(userTransactionalDataStore.getAccountDetails(), userPersonalDataStore)).thenReturn(kycRelatedPersonalDetails);
        when(loanOfferStoreService.getLoanOffers(offerRequest.getUserUUID(), applicationId)).thenReturn(Mono.just(offers));

        final var result = offersReadyResponseEnhancer.buildResponseData(offerRequest);

        // then
        StepVerifier.create(result).consumeNextWith(response ->
                assertAll(
                        () -> assertEquals(OfferResponseState.OFFERS_READY, response.getState()),
                        () -> assertTrue(response.getData().getOffers().isEmpty()),
                        () -> assertEquals(kycRelatedPersonalDetails, response.getData().getKycRelatedPersonalDetails())
                )
        ).verifyComplete();
    }

    @Test
    void buildResponseData_sameAmountOffer_notRecommended_notExpired(@Random LoanOfferStore loanOfferStore) {
        // given
        final var offerRequest = OfferRequest.builder()
                .userUUID("92ff692c-5be0-4d26-b9a1-512c6647252b")
                .requestedAmount(5000)
                .requestedPurpose("car")
                .isRequestedBonifyLoans(true)
                .build();
        final var applicationId = "a";
        final var userStatesStore = new UserStatesStore();
        final var offerDataStateDetails = OfferDataStateDetails.builder()
                .amount(offerRequest.getRequestedAmount())
                .purpose(offerRequest.getRequestedPurpose())
                .applicationId(applicationId)
                .parentApplicationId(null)
                .expired(false)
                .responseDateTime(OffsetDateTime.now())
                .build();
        userStatesStore.setOfferDateStateDetailsSet(Set.of(offerDataStateDetails));
        final var offers = List.of(loanOfferStore.toBuilder()
                .offer(LoanOffer.builder().loanProvider(new LoanProvider("AION")).build())
                .applicationId(applicationId).build());

        final var userTransactionalDataStore = new UserTransactionalDataStore();
        userTransactionalDataStore.setAccountDetails(AccountDetails.builder().build());

        final var kycRelatedPersonalDetails = new KycRelatedPersonalDetails();
        final var userPersonalDataStore = new UserPersonalInformationStore();

        // when
        when(userStatesStoreService.findById(offerRequest.getUserUUID())).thenReturn(Mono.just(userStatesStore));
        when(userTransactionalDataStoreService.getKycRelatedPersonalDetails(offerRequest.getUserUUID())).thenReturn(Mono.just(userTransactionalDataStore));
        when(userPersonalInfoService.findById(offerRequest.getUserUUID())).thenReturn(Mono.just(userPersonalDataStore));
        when(userTransactionalDataStoreMapper.mapToKycRelatedPersonalDetails(userTransactionalDataStore.getAccountDetails(), userPersonalDataStore)).thenReturn(kycRelatedPersonalDetails);

        when(loanOfferStoreService.getLoanOffers(offerRequest.getUserUUID(), applicationId)).thenReturn(Mono.just(offers));
        when(offersStateValidator.areStillValidOffers(any())).thenReturn(true);

        final var result = offersReadyResponseEnhancer.buildResponseData(offerRequest);

        // then
        StepVerifier.create(result).consumeNextWith(response ->
                assertAll(
                        () -> assertEquals(OfferResponseState.OFFERS_READY, response.getState()),
                        () -> assertEquals(1, response.getData().getOffers().size()),
                        () -> assertEquals(applicationId, response.getData().getOffers().stream().findFirst().get().getApplicationId()),
                        () -> assertEquals(kycRelatedPersonalDetails, response.getData().getKycRelatedPersonalDetails()),
                        () -> assertEquals(1, response.getData().getRecentQueriedAmounts().size()),
                        () -> assertEquals(loanOfferStore.getAcceptedDate(), response.getData().getOffers().stream().findFirst().get().getAcceptedDate()),
                        () -> assertEquals(loanOfferStore.getKycStatusLastUpdateDate(), response.getData().getOffers().stream().findFirst().get().getKycStatusLastUpdateDate()),
                        () -> assertEquals(loanOfferStore.getStatusLastUpdateDate(), response.getData().getOffers().stream().findFirst().get().getStatusLastUpdateDate())
                )
        ).verifyComplete();
    }

    @Test
    void buildResponseData_sameAmountOffer_Recommended_notExpired() {
        // given
        final var offerRequest = OfferRequest.builder()
                .userUUID("92ff692c-5be0-4d26-b9a1-512c6647252b")
                .requestedAmount(5000)
                .requestedPurpose("car")
                .isRequestedBonifyLoans(true)
                .build();
        final var applicationId = "a";
        final var userStatesStore = new UserStatesStore();
        final var offerDataStateDetails = OfferDataStateDetails.builder()
                .amount(offerRequest.getRequestedAmount())
                .purpose(offerRequest.getRequestedPurpose())
                .applicationId(applicationId)
                .parentApplicationId("b")
                .expired(false)
                .responseDateTime(OffsetDateTime.now())
                .build();
        userStatesStore.setOfferDateStateDetailsSet(Set.of(offerDataStateDetails));
        final var offers = List.of(LoanOfferStore.builder()
                .offer(LoanOffer.builder().loanProvider(new LoanProvider("AION")).build())
                .applicationId(applicationId).build());

        final var userTransactionalDataStore = new UserTransactionalDataStore();
        userTransactionalDataStore.setAccountDetails(AccountDetails.builder().build());

        final var kycRelatedPersonalDetails = new KycRelatedPersonalDetails();
        final var userPersonalDataStore = new UserPersonalInformationStore();

        // when
        when(userStatesStoreService.findById(offerRequest.getUserUUID())).thenReturn(Mono.just(userStatesStore));
        when(userTransactionalDataStoreService.getKycRelatedPersonalDetails(offerRequest.getUserUUID())).thenReturn(Mono.just(userTransactionalDataStore));
        when(userPersonalInfoService.findById(offerRequest.getUserUUID())).thenReturn(Mono.just(userPersonalDataStore));
        when(userTransactionalDataStoreMapper.mapToKycRelatedPersonalDetails(userTransactionalDataStore.getAccountDetails(), userPersonalDataStore)).thenReturn(kycRelatedPersonalDetails);


        when(loanOfferStoreService.getLoanOffers(offerRequest.getUserUUID(), applicationId)).thenReturn(Mono.just(offers));
        when(offersStateValidator.areStillValidOffers(any())).thenReturn(true);

        final var result = offersReadyResponseEnhancer.buildResponseData(offerRequest);

        // then
        StepVerifier.create(result).consumeNextWith(response ->
                assertAll(
                        () -> assertEquals(OfferResponseState.OFFERS_READY, response.getState()),
                        () -> assertEquals(1, response.getData().getOffers().size()),
                        () -> assertEquals(applicationId, response.getData().getOffers().stream().findFirst().get().getApplicationId()),
                        () -> assertEquals(kycRelatedPersonalDetails, response.getData().getKycRelatedPersonalDetails()),
                        () -> assertEquals(1, response.getData().getRecentQueriedAmounts().size()),
                        () -> verify(dataSolutionCommunicationManager).sendLoanOffers(offerRequest, applicationId, offers, 1, 0)
                )
        ).verifyComplete();
    }


    @Test
    void buildResponseData_multipleSameAmountOffer_Recommended_notExpired() {
        // given
        final var offerRequest = OfferRequest.builder()
                .userUUID("92ff692c-5be0-4d26-b9a1-512c6647252b")
                .requestedAmount(5000)
                .requestedPurpose("car")
                .isRequestedBonifyLoans(true)
                .build();
        final var applicationId = "a";
        final var userStatesStore = new UserStatesStore();

        final var offerDataStateDetails = OfferDataStateDetails.builder()
                .amount(offerRequest.getRequestedAmount())
                .purpose(offerRequest.getRequestedPurpose())
                .applicationId(applicationId)
                .parentApplicationId(null)
                .expired(false)
                .responseDateTime(OffsetDateTime.now())
                .build();
        final var recommendedOfferDataStateDetails = OfferDataStateDetails.builder()
                .amount(offerRequest.getRequestedAmount())
                .purpose(offerRequest.getRequestedPurpose())
                .applicationId("c")
                .parentApplicationId("b")
                .responseDateTime(OffsetDateTime.now().minusDays(1))
                .expired(false)
                .build();
        userStatesStore.setOfferDateStateDetailsSet(Set.of(offerDataStateDetails, recommendedOfferDataStateDetails));
        final var offers = List.of(
                LoanOfferStore.builder()
                        .applicationId(applicationId)
                        .offer(LoanOffer.builder().loanProvider(new LoanProvider("AION")).build())
                        .build(),
                LoanOfferStore.builder()
                        .applicationId(applicationId)
                        .offer(LoanOffer.builder().loanProvider(new LoanProvider("SANTANDER")).build())
                        .build()
        );

        final var userTransactionalDataStore = new UserTransactionalDataStore();
        userTransactionalDataStore.setAccountDetails(AccountDetails.builder().build());

        final var kycRelatedPersonalDetails = new KycRelatedPersonalDetails();
        final var userPersonalDataStore = new UserPersonalInformationStore();

        // when
        when(userStatesStoreService.findById(offerRequest.getUserUUID())).thenReturn(Mono.just(userStatesStore));
        when(userTransactionalDataStoreService.getKycRelatedPersonalDetails(offerRequest.getUserUUID())).thenReturn(Mono.just(userTransactionalDataStore));
        when(userPersonalInfoService.findById(offerRequest.getUserUUID())).thenReturn(Mono.just(userPersonalDataStore));
        when(userTransactionalDataStoreMapper.mapToKycRelatedPersonalDetails(userTransactionalDataStore.getAccountDetails(), userPersonalDataStore)).thenReturn(kycRelatedPersonalDetails);


        when(loanOfferStoreService.getLoanOffers(offerRequest.getUserUUID(), applicationId)).thenReturn(Mono.just(offers));
        when(offersStateValidator.areStillValidOffers(any())).thenReturn(true);

        final var result = offersReadyResponseEnhancer.buildResponseData(offerRequest);

        // then
        StepVerifier.create(result).consumeNextWith(response ->
                assertAll(
                        () -> assertEquals(OfferResponseState.OFFERS_READY, response.getState()),
                        () -> assertEquals(1, response.getData().getOffers().size()),
                        () -> assertEquals(applicationId, response.getData().getOffers().stream().findFirst().get().getApplicationId()),
                        () -> assertEquals(kycRelatedPersonalDetails, response.getData().getKycRelatedPersonalDetails()),
                        () -> assertEquals(1, response.getData().getRecentQueriedAmounts().size()),
                        () -> assertEquals(2, response.getData().getTotalOffers()),
                        () -> assertEquals(1, response.getData().getRequestedOffers()),
                        () -> verify(dataSolutionCommunicationManager).sendLoanOffers(offerRequest, applicationId, offers, 1, 1)
                )
        ).verifyComplete();
    }


    @Test
    void buildResponseData_ordered_offers() {
        // given
        final var offerRequest = OfferRequest.builder()
                .userUUID("8359fcc3-6bc8-4586-a899-464801bb184e")
                .requestedAmount(5000)
                .requestedPurpose("car")
                .isRequestedBonifyLoans(false)
                .build();
        final var applicationId = "application-id-123";
        final var userStatesStore = new UserStatesStore();
        final var offerDataStateDetails = OfferDataStateDetails.builder()
                .amount(offerRequest.getRequestedAmount())
                .purpose(offerRequest.getRequestedPurpose())
                .applicationId(applicationId)
                .parentApplicationId(null)
                .expired(false)
                .responseDateTime(OffsetDateTime.now())
                .build();
        userStatesStore.setOfferDateStateDetailsSet(Set.of(offerDataStateDetails));
        final var offers = List.of(
                LoanOfferStore.builder()
                        .applicationId(applicationId)
                        .offer(LoanOffer.builder()
                                .amount(5000)
                                .monthlyRate(BigDecimal.ZERO)
                                .loanProvider(new LoanProvider("AION"))
                                .build())
                        .build(),
                LoanOfferStore.builder()
                        .applicationId(applicationId)
                        .offer(LoanOffer.builder()
                                .amount(5000)
                                .monthlyRate(BigDecimal.TEN)
                                .loanProvider(new LoanProvider("SANTANDER"))
                                .build())
                        .build(),
                LoanOfferStore.builder()
                        .applicationId(applicationId)
                        .offer(LoanOffer.builder()
                                .amount(4000)
                                .monthlyRate(BigDecimal.ONE)
                                .loanProvider(new LoanProvider("Consors Finanz"))
                                .build())
                        .build(),
                LoanOfferStore.builder()
                        .applicationId(applicationId)
                        .offer(LoanOffer.builder()
                                .amount(4000)
                                .monthlyRate(BigDecimal.valueOf(100))
                                .loanProvider(new LoanProvider("SWK_BANK"))
                                .build())
                        .build());

        final var userTransactionalDataStore = new UserTransactionalDataStore();
        userTransactionalDataStore.setAccountDetails(AccountDetails.builder().build());

        final var kycRelatedPersonalDetails = new KycRelatedPersonalDetails();
        final var userPersonalDataStore = new UserPersonalInformationStore();

        // when
        when(userStatesStoreService.findById(offerRequest.getUserUUID())).thenReturn(Mono.just(userStatesStore));
        when(userTransactionalDataStoreService.getKycRelatedPersonalDetails(offerRequest.getUserUUID())).thenReturn(Mono.just(userTransactionalDataStore));
        when(userPersonalInfoService.findById(offerRequest.getUserUUID())).thenReturn(Mono.just(userPersonalDataStore));
        when(userTransactionalDataStoreMapper.mapToKycRelatedPersonalDetails(userTransactionalDataStore.getAccountDetails(), userPersonalDataStore)).thenReturn(kycRelatedPersonalDetails);

        when(loanOfferStoreService.getLoanOffers(offerRequest.getUserUUID(), applicationId)).thenReturn(Mono.just(offers));
        when(offersStateValidator.areStillValidOffers(any())).thenReturn(true);

        final var result = offersReadyResponseEnhancer.buildResponseData(offerRequest);

        // then
        StepVerifier.create(result).consumeNextWith(response ->
                assertAll(
                        () -> assertThat(response.getData().getOffers().size()).isEqualTo(4),
                        () -> assertThat(response.getData().getOffers().get(0).getOffer().getMonthlyRate()).isEqualByComparingTo(BigDecimal.ZERO),
                        () -> assertThat(response.getData().getOffers().get(1).getOffer().getMonthlyRate()).isEqualByComparingTo(BigDecimal.TEN),
                        () -> assertThat(response.getData().getOffers().get(2).getOffer().getMonthlyRate()).isOne(),
                        () -> assertThat(response.getData().getOffers().get(3).getOffer().getMonthlyRate()).isEqualByComparingTo(BigDecimal.valueOf(100)),
                        () -> assertThat(response.getData().getTotalOffers()).isEqualTo(4),
                        () -> assertThat(response.getData().getRequestedOffers()).isEqualTo(4)
                )
        ).verifyComplete();
    }

}
