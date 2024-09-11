package de.joonko.loan.integrations.domain.integrationhandler.loandemand;

import de.joonko.loan.db.repositories.LoanDemandRequestRepository;
import de.joonko.loan.db.service.LoanApplicationAuditTrailService;
import de.joonko.loan.db.service.LoanDemandStoreService;
import de.joonko.loan.db.vo.LoanDemandStore;
import de.joonko.loan.integrations.domain.integrationhandler.testData.LoanDemandProviderServiceTestData;
import de.joonko.loan.integrations.model.OfferRequest;
import de.joonko.loan.offer.api.GetOffersMapper;
import de.joonko.loan.offer.api.LoanDemandRequest;
import de.joonko.loan.offer.domain.LoanRecommendationEngine;
import de.joonko.loan.data.support.DataSolutionCommunicationManager;
import de.joonko.loan.user.states.UserStatesStore;
import de.joonko.loan.user.states.UserStatesStoreService;
import de.joonko.loan.offer.domain.LoanDemand;
import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(RandomBeansExtension.class)
class LoanDemandProviderServiceTest {

    private LoanDemandProviderService loanDemandProviderService;

    private LoanDemandRequestBuilder loanDemandRequestBuilder;
    private LoanDemandStoreService loanDemandStoreService;
    private LoanDemandRequestRepository loanDemandRequestRepository;
    private DataSolutionCommunicationManager dataSolutionCommunicationManager;
    private LoanApplicationAuditTrailService loanApplicationAuditTrailService;
    private LoanRecommendationEngine loanRecommendationEngine;
    private GetOffersMapper getOffersMapper;
    private UserStatesStoreService userStatesStoreService;


    private LoanDemandProviderServiceTestData testData;

    @BeforeEach
    void setUp() {
        testData = new LoanDemandProviderServiceTestData();

        loanDemandRequestBuilder = mock(LoanDemandRequestBuilder.class);
        loanDemandStoreService = mock(LoanDemandStoreService.class);
        loanDemandRequestRepository = mock(LoanDemandRequestRepository.class);
        dataSolutionCommunicationManager = mock(DataSolutionCommunicationManager.class);
        loanApplicationAuditTrailService = mock(LoanApplicationAuditTrailService.class);
        loanRecommendationEngine = mock(LoanRecommendationEngine.class);
        getOffersMapper = mock(GetOffersMapper.class);
        userStatesStoreService = mock(UserStatesStoreService.class);


        loanDemandProviderService = new LoanDemandProviderServiceImpl(loanDemandRequestBuilder, loanDemandStoreService,
                loanDemandRequestRepository, dataSolutionCommunicationManager, loanApplicationAuditTrailService,
                loanRecommendationEngine, getOffersMapper, userStatesStoreService);
    }

    @Test
    void getRequestedAndRecommendedLoanDemands(@Random OfferRequest offerRequest) {
        // given
        var requested = testData.getRequestedLoanDemandRequest(offerRequest.getUserUUID(), offerRequest.getRequestedAmount());
        var recommended1 = testData.getRecommended(requested, 3000);
        var recommended2 = testData.getRecommended(requested, 4000);

        when(loanDemandRequestBuilder.build(offerRequest)).thenReturn(Mono.just(requested));
        when(loanRecommendationEngine.recommend(requested)).thenReturn(Set.of(recommended1, recommended2));
        when(loanDemandStoreService.saveLoanDemand(requested, false, offerRequest.getUserUUID())).thenReturn(LoanDemandStore.builder().applicationId("requestedApplicationIdId").build());
        when(loanDemandStoreService.saveLoanDemand(recommended1, false, offerRequest.getUserUUID())).thenReturn(LoanDemandStore.builder().applicationId("recommendedApplicationIdId1").build());
        when(loanDemandStoreService.saveLoanDemand(recommended2, false, offerRequest.getUserUUID())).thenReturn(LoanDemandStore.builder().applicationId("recommendedApplicationIdId2").build());
        when(loanDemandRequestRepository.save(requested)).thenReturn(requested);
        when(loanDemandRequestRepository.save(recommended1)).thenReturn(recommended1);
        when(loanDemandRequestRepository.save(recommended2)).thenReturn(recommended2);
        when(getOffersMapper.fromRequest(requested, "requestedApplicationIdId", offerRequest.getUserUUID())).thenReturn(testData.getLoanDemand("requestedApplicationIdId"));
        when(getOffersMapper.fromRequest(recommended1, "recommendedApplicationIdId1", offerRequest.getUserUUID())).thenReturn(testData.getLoanDemand("recommendedApplicationIdId1"));
        when(getOffersMapper.fromRequest(recommended2, "recommendedApplicationIdId2", offerRequest.getUserUUID())).thenReturn(testData.getLoanDemand("recommendedApplicationIdId2"));


        // when
        var loanDemandReq = loanDemandProviderService.getLoanDemandFromOfferRequest(offerRequest);

        // then
        assertAll(
                () -> StepVerifier.create(loanDemandReq)
                        .expectNextMatches(loanDemands -> loanDemands.size() == 3 &&
                                loanDemands.stream().anyMatch(loanDemand ->
                                        "requestedApplicationIdId".equals(loanDemand.getLoanApplicationId()) &&
                                                loanDemand.getParentLoanApplicationId() == null) &&
                                loanDemands.stream().anyMatch(loanDemand ->
                                        "recommendedApplicationIdId1".equals(loanDemand.getLoanApplicationId()) &&
                                                "requestedApplicationIdId".equals(loanDemand.getParentLoanApplicationId())) &&
                                loanDemands.stream().anyMatch(loanDemand ->
                                        "recommendedApplicationIdId2".equals(loanDemand.getLoanApplicationId()) &&
                                                "requestedApplicationIdId".equals(loanDemand.getParentLoanApplicationId())))
                        .verifyComplete(),
                () -> verify(loanDemandRequestBuilder).build(offerRequest),
                () -> verify(loanRecommendationEngine).recommend(requested),
                () -> verify(loanDemandStoreService, times(3)).saveLoanDemand(any(LoanDemandRequest.class), eq(false), eq(offerRequest.getUserUUID())),
                () -> verify(loanApplicationAuditTrailService, times(3)).loanDemandReceived(anyString()),
                () -> verify(loanDemandRequestRepository, times(3)).save(any(LoanDemandRequest.class)),
                () -> verify(getOffersMapper, times(3)).fromRequest(any(LoanDemandRequest.class), anyString(), eq(offerRequest.getUserUUID()))
        );
    }

    @Test
    void getRequestedLoanDemand(@Random OfferRequest offerRequest) {
        // given
        var requested = testData.getRequestedLoanDemandRequest(offerRequest.getUserUUID(), offerRequest.getRequestedAmount());

        when(loanDemandRequestBuilder.build(offerRequest)).thenReturn(Mono.just(requested));
        when(loanRecommendationEngine.recommend(requested)).thenReturn(Set.of());
        when(loanDemandStoreService.saveLoanDemand(requested, false, offerRequest.getUserUUID())).thenReturn(LoanDemandStore.builder().applicationId("requestedApplicationIdId").build());
        when(loanDemandRequestRepository.save(requested)).thenReturn(requested);
        when(getOffersMapper.fromRequest(requested, "requestedApplicationIdId", offerRequest.getUserUUID())).thenReturn(testData.getLoanDemand("requestedApplicationIdId"));

        // when
        var loanDemandReq = loanDemandProviderService.getLoanDemandFromOfferRequest(offerRequest);

        // then
        assertAll(
                () -> StepVerifier.create(loanDemandReq)
                        .expectNextMatches(loanDemands -> loanDemands.size() == 1 &&
                                loanDemands.stream().anyMatch(loanDemand ->
                                        "requestedApplicationIdId".equals(loanDemand.getLoanApplicationId())))
                        .verifyComplete(),
                () -> verify(loanDemandRequestBuilder).build(offerRequest),
                () -> verify(loanRecommendationEngine).recommend(requested),
                () -> verify(loanDemandStoreService).saveLoanDemand(any(LoanDemandRequest.class), eq(false), eq(offerRequest.getUserUUID())),
                () -> verify(loanApplicationAuditTrailService).loanDemandReceived("requestedApplicationIdId"),
                () -> verify(loanDemandRequestRepository).save(any(LoanDemandRequest.class)),
                () -> verify(getOffersMapper).fromRequest(any(LoanDemandRequest.class), anyString(), eq(offerRequest.getUserUUID()))
        );
    }

    @Test
    void getErrorWhenFailedBuildingLoanDemandRequest(@Random OfferRequest offerRequest) {
        // given
        var requested = testData.getRequestedLoanDemandRequest(offerRequest.getUserUUID(), offerRequest.getRequestedAmount());

        when(loanDemandRequestBuilder.build(offerRequest)).thenReturn(Mono.error(new IllegalStateException("Unable to find personal information for userId: " + offerRequest.getUserUUID())));

        // when
        var loanDemandReq = loanDemandProviderService.getLoanDemandFromOfferRequest(offerRequest);

        // then
        StepVerifier.create(loanDemandReq).verifyError();
    }

    @Test
    void savePrechecksToRequestAndPublish(@Random LoanDemand loanDemand) {
        // given
        final var loanDemandRequest = LoanDemandRequest.builder()
                .applicationId("12")
                .userUUID("657")
                .build();
        final var userStatesStore = new UserStatesStore();
        userStatesStore.setBonifyUserId(123L);
        ArgumentCaptor<LoanDemandRequest> argument = ArgumentCaptor.forClass(LoanDemandRequest.class);

        // when
        when(loanDemandRequestRepository.findByApplicationId(loanDemand.getLoanApplicationId())).thenReturn(Optional.of(loanDemandRequest));
        when(loanDemandRequestRepository.save(loanDemandRequest)).thenReturn(loanDemandRequest);
        when(userStatesStoreService.findById(loanDemand.getUserUUID())).thenReturn(Mono.just(userStatesStore));
        doNothing().when(dataSolutionCommunicationManager).sendLoanDemandRequest(userStatesStore.getBonifyUserId(), loanDemandRequest);

        final var result = loanDemandProviderService.savePrechecksToRequestAndPublish(loanDemand);

        assertAll(
                () -> StepVerifier.create(result).expectNextCount(1).verifyComplete(),
                () -> verify(loanDemandRequestRepository).findByApplicationId(loanDemand.getLoanApplicationId()),
                () -> verify(loanDemandRequestRepository).save(argument.capture()),
                () -> verify(userStatesStoreService).findById(loanDemand.getUserUUID()),
                () -> verify(dataSolutionCommunicationManager).sendLoanDemandRequest(userStatesStore.getBonifyUserId(), loanDemandRequest),
                () -> assertEquals(loanDemand.getPreChecks(), argument.getValue().getPreChecks())
        );
    }
}
