package de.joonko.loan.integrations.domain.enhancers;

import de.joonko.loan.integrations.model.*;
import de.joonko.loan.offer.api.LoanDemandRequest;
import de.joonko.loan.offer.api.model.*;
import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import javax.annotation.Resource;

import static de.joonko.loan.offer.api.model.OfferResponseState.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(RandomBeansExtension.class)
class ResponseEnhancerImplTest {


    @InjectMocks
    @Resource
    private ResponseEnhancer responseEnhancer = new ResponseEnhancerImpl();

    @Mock
    private OffersReadyResponseEnhancer offersReadyResponseEnhancer;

    @Mock
    private PersonalDetailsResponseEnhancer personalDetailsResponseEnhancer;

    @Mock
    private AccountDetailsResponseEnhancer accountDetailsResponseEnhancer;

    @Mock
    private WaitingResponseEnhancer waitingResponseEnhancer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void buildResponseData_forFailureOnOffers(@Random OfferRequest offerRequest) {
        // given
        offerRequest.setUserState(UserState.builder()
                .offersState(OffersState.ERROR)
                .personalDataState(PersonalDataState.EXISTS)
                .build());

        // when
        final var result = responseEnhancer.buildResponseData(offerRequest);

        // then
        assertAll(
                () -> StepVerifier.create(result).verifyError(),
                () -> verifyNoInteractions(offersReadyResponseEnhancer),
                () -> verifyNoInteractions(personalDetailsResponseEnhancer),
                () -> verifyNoInteractions(accountDetailsResponseEnhancer)
        );
    }

    @Test
    void buildResponseData_forFailureOnUserDetails(@Random OfferRequest offerRequest) {
        // given
        offerRequest.setUserState(UserState.builder()
                .offersState(OffersState.OFFERS_EXIST)
                .personalDataState(PersonalDataState.ERROR)
                .dacDataState(DacDataState.FTS_DATA_EXISTS)
                .build());

        // when
        final var result = responseEnhancer.buildResponseData(offerRequest);

        // then
        assertAll(
                () -> StepVerifier.create(result).verifyError(),
                () -> verifyNoInteractions(offersReadyResponseEnhancer),
                () -> verifyNoInteractions(personalDetailsResponseEnhancer),
                () -> verifyNoInteractions(accountDetailsResponseEnhancer)
        );
    }

    @Test
    void buildResponseData_forFailureOnDACDetails(@Random OfferRequest offerRequest) {
        // given
        offerRequest.setUserState(UserState.builder()
                .offersState(OffersState.OFFERS_EXIST)
                .personalDataState(PersonalDataState.EXISTS)
                .dacDataState(DacDataState.ERROR)
                .build());

        // when
        final var result = responseEnhancer.buildResponseData(offerRequest);

        // then
        assertAll(
                () -> StepVerifier.create(result).verifyError(),
                () -> verifyNoInteractions(offersReadyResponseEnhancer),
                () -> verifyNoInteractions(personalDetailsResponseEnhancer),
                () -> verifyNoInteractions(accountDetailsResponseEnhancer)
        );
    }

    @Test
    void buildResponseData_forWaiting(@Random OfferRequest offerRequest) {
        // given
        offerRequest.setUserState(UserState.builder()
                .offersState(OffersState.MISSING_OR_STALE)
                .personalDataState(PersonalDataState.EXISTS)
                .dacDataState(DacDataState.FTS_DATA_EXISTS)
                .build());
        final var offersResponse = OffersResponse.builder().state(OfferResponseState.CLASSIFYING_TRANSACTIONS).build();

        when(waitingResponseEnhancer.buildResponseData(offerRequest)).thenReturn(Mono.just(offersResponse));

        // when
        final Mono<OffersResponse> result = responseEnhancer.buildResponseData(offerRequest);

        // then
        assertAll(
                () -> StepVerifier.create(result).expectNextMatches(offerResponse ->
                        CLASSIFYING_TRANSACTIONS.equals(offerResponse.getState()) &&
                                offerResponse.getData() == null
                ).verifyComplete(),
                () -> verifyNoInteractions(offersReadyResponseEnhancer),
                () -> verifyNoInteractions(personalDetailsResponseEnhancer),
                () -> verifyNoInteractions(accountDetailsResponseEnhancer)
        );
    }

    @Test
    void buildResponseData_for_offersExist(@Random OfferRequest offerRequest) {
        // given
        offerRequest.setUserState(UserState.builder()
                .offersState(OffersState.OFFERS_EXIST)
                .personalDataState(PersonalDataState.EXISTS)
                .dacDataState(DacDataState.FTS_DATA_EXISTS)
                .build());

        final var offersResponse = OffersResponse.<OffersReadyResponse>builder()
                .state(OFFERS_READY)
                .data(OffersReadyResponse.builder()
                        .build())
                .build();
        // when
        when(offersReadyResponseEnhancer.buildResponseData(offerRequest)).thenReturn(Mono.just(offersResponse));

        final Mono<OffersResponse> result = responseEnhancer.buildResponseData(offerRequest);

        // then
        StepVerifier.create(result).consumeNextWith(response -> assertAll(
                () -> assertEquals(OFFERS_READY, response.getState()),
                () -> assertEquals(offersResponse.getData(), response.getData())
        )).verifyComplete();
    }

    @Test
    void buildResponseData_for_userInputRequired(@Random OfferRequest offerRequest) {
        // given
        offerRequest.setUserState(UserState.builder()
                .offersState(OffersState.MISSING_OR_STALE)
                .personalDataState(PersonalDataState.USER_INPUT_REQUIRED)
                .dacDataState(DacDataState.FTS_DATA_EXISTS)
                .build());

        final var offersResponse = OffersResponse.<LoanDemandRequest>builder()
                .state(MISSING_PERSONAL_DATA)
                .data(LoanDemandRequest.builder().build()).build();
        // when
        when(personalDetailsResponseEnhancer.buildResponseData(offerRequest)).thenReturn(Mono.just(offersResponse));

        final Mono<OffersResponse> result = responseEnhancer.buildResponseData(offerRequest);

        // then
        StepVerifier.create(result).consumeNextWith(response -> assertAll(
                () -> assertEquals(MISSING_PERSONAL_DATA, response.getState()),
                () -> assertEquals(offersResponse.getData(), response.getData())
        )).verifyComplete();
    }

    @Test
    void buildResponseData_for_userInputRequired_butNoFTSDataAvailable(@Random OfferRequest offerRequest) {
        // given
        offerRequest.setUserState(UserState.builder()
                .offersState(OffersState.MISSING_OR_STALE)
                .personalDataState(PersonalDataState.USER_INPUT_REQUIRED)
                .dacDataState(DacDataState.FETCHING_FROM_FUSIONAUTH)
                .build());
        final var offersResponse = OffersResponse.builder().state(OfferResponseState.CLASSIFYING_TRANSACTIONS).build();

        // when
        when(waitingResponseEnhancer.buildResponseData(offerRequest)).thenReturn(Mono.just(offersResponse));
        final Mono<OffersResponse> result = responseEnhancer.buildResponseData(offerRequest);

        // then
        StepVerifier.create(result).consumeNextWith(response -> assertAll(
                () -> assertEquals(CLASSIFYING_TRANSACTIONS, response.getState()),
                () -> assertNull(response.getData())
        )).verifyComplete();
    }

    @Test
    void buildResponseData_forAccountMissing(@Random OfferRequest offerRequest) {
        // given
        offerRequest.setUserState(UserState.builder()
                .offersState(OffersState.MISSING_OR_STALE)
                .personalDataState(PersonalDataState.EXISTS)
                .dacDataState(DacDataState.NO_ACCOUNT_ADDED)
                .build());

        final var offersResponse = OffersResponse.<CustomErrorResponse>builder()
                .state(MISSING_SALARY_ACCOUNT)
                .build();
        // when
        when(accountDetailsResponseEnhancer.buildResponseData(offerRequest)).thenReturn(Mono.just(offersResponse));

        final Mono<OffersResponse> result = responseEnhancer.buildResponseData(offerRequest);

        // then
        StepVerifier.create(result).consumeNextWith(response -> assertAll(
                () -> assertEquals(MISSING_SALARY_ACCOUNT, response.getState()),
                () -> assertEquals(offersResponse.getData(), response.getData())
        )).verifyComplete();
    }

    @Test
    void buildResponseData_forNonSalaryAccountAdded(@Random OfferRequest offerRequest) {
        // given
        offerRequest.setUserState(UserState.builder()
                .offersState(OffersState.MISSING_OR_STALE)
                .personalDataState(PersonalDataState.EXISTS)
                .dacDataState(DacDataState.MISSING_SALARY_ACCOUNT)
                .build());

        final var offersResponse = OffersResponse.<CustomErrorResponse>builder()
                .state(MISSING_SALARY_ACCOUNT)
                .data(CustomErrorResponse.builder()
                        .messageKey(CustomErrorMessageKey.NON_SALARY_ACCOUNT_ADDED)
                        .build())
                .build();
        // when
        when(accountDetailsResponseEnhancer.buildResponseData(offerRequest)).thenReturn(Mono.just(offersResponse));

        final Mono<OffersResponse> result = responseEnhancer.buildResponseData(offerRequest);

        // then
        StepVerifier.create(result).consumeNextWith(response -> assertAll(
                () -> assertEquals(MISSING_SALARY_ACCOUNT, response.getState()),
                () -> assertEquals(offersResponse.getData(), response.getData())
        )).verifyComplete();
    }

}
