package de.joonko.loan.integrations.domain;

import de.joonko.loan.integrations.configuration.GetOffersConfigurations;
import de.joonko.loan.integrations.model.DacDataState;
import de.joonko.loan.integrations.model.DistributionChannel;
import de.joonko.loan.integrations.model.OffersState;
import de.joonko.loan.integrations.model.PersonalDataState;
import de.joonko.loan.user.states.OfferDataStateDetails;
import de.joonko.loan.user.states.StateDetails;
import de.joonko.loan.user.states.Status;
import de.joonko.loan.user.states.TransactionalDataStateDetails;
import de.joonko.loan.user.states.UserStatesStore;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;

import static de.joonko.loan.integrations.model.DacDataState.FETCHING_FROM_DS;
import static de.joonko.loan.integrations.model.DacDataState.FETCHING_FROM_FTS;
import static de.joonko.loan.integrations.model.DacDataState.FETCHING_FROM_FUSIONAUTH;
import static de.joonko.loan.integrations.model.DacDataState.FTS_DATA_EXISTS;
import static de.joonko.loan.integrations.model.DacDataState.MISSING_ACCOUNT_CLASSIFICATION;
import static de.joonko.loan.integrations.model.DacDataState.MISSING_SALARY_ACCOUNT;
import static de.joonko.loan.integrations.model.DacDataState.NO_ACCOUNT_ADDED;
import static de.joonko.loan.integrations.model.OffersState.IN_PROGRESS;
import static de.joonko.loan.integrations.model.OffersState.MISSING_OR_STALE;
import static de.joonko.loan.integrations.model.OffersState.OFFERS_EXIST;
import static de.joonko.loan.integrations.model.PersonalDataState.EXISTS;
import static de.joonko.loan.integrations.model.PersonalDataState.USER_INPUT_REQUIRED;
import static de.joonko.loan.user.states.Status.ERROR;
import static de.joonko.loan.user.states.Status.SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, RandomBeansExtension.class})
class UserStateEvaluatorTest {
    private static final int LOAN_AMOUNT = 10000;
    private static final String LOAN_PURPOSE = "car";

    @InjectMocks
    private UserStateEvaluator userStateEvaluator;

    @Mock
    private GetOffersConfigurations getOffersConfigurations;

    @Mock
    private OffersStateValidator offersStateValidator;

    private String uuid;

    @BeforeEach
    void setup() {
        uuid = UUID.randomUUID().toString();
    }


    @Test
    void shouldDetectStaleOffers() {
        // given
        OfferDataStateDetails offerDataStateDetails = OfferDataStateDetails.builder()
                .responseDateTime(OffsetDateTime.now().minusDays(180))
                .build();
        UserStatesStore mockedUser = mock(UserStatesStore.class);
        when(mockedUser.getOfferDateStateDetailsSet()).thenReturn(Set.of(offerDataStateDetails));

        // then
        assertThat(userStateEvaluator.getOffersState(LOAN_AMOUNT, LOAN_PURPOSE, mockedUser)).isEqualByComparingTo(MISSING_OR_STALE);
    }

    @Test
    void shouldDetectValidOffers() {
        // given
        when(getOffersConfigurations.getOffersValidityInDays()).thenReturn(14L);

        OfferDataStateDetails offerDataStateDetails = OfferDataStateDetails.builder()
                .responseDateTime(OffsetDateTime.now().minusDays(10))
                .amount(LOAN_AMOUNT)
                .purpose(LOAN_PURPOSE)
                .expired(false)
                .state(SUCCESS)
                .build();
        UserStatesStore mockedUser = mock(UserStatesStore.class);
        when(mockedUser.getOfferDateStateDetailsSet()).thenReturn(Set.of(offerDataStateDetails));

        assertThat(userStateEvaluator.getOffersState(LOAN_AMOUNT, LOAN_PURPOSE, mockedUser)).isEqualByComparingTo(OFFERS_EXIST);
    }

    @Test
    void shouldDetectErrorInOffers() {
        // given
        when(getOffersConfigurations.getOffersValidityInDays()).thenReturn(14L);

        OfferDataStateDetails offerDataStateDetails = OfferDataStateDetails.builder()
                .responseDateTime(OffsetDateTime.now().minusDays(10))
                .amount(LOAN_AMOUNT)
                .purpose(LOAN_PURPOSE)
                .expired(false)
                .state(ERROR)
                .build();
        UserStatesStore mockedUser = mock(UserStatesStore.class);
        when(mockedUser.getOfferDateStateDetailsSet()).thenReturn(Set.of(offerDataStateDetails));

        assertThat(userStateEvaluator.getOffersState(LOAN_AMOUNT, LOAN_PURPOSE, mockedUser)).isEqualByComparingTo(OffersState.ERROR);
    }

    @Test
    void shouldGetMissingOrStateForNewUser() {
        // given
        UserStatesStore userStatesStore = new UserStatesStore();
        userStatesStore.setUserUUID(uuid);
        userStatesStore.setLastRequestedLoanAmount(4000);

        // when
        var personalState = userStateEvaluator.getPersonalDataStateOld(userStatesStore);

        // then
        assertThat(personalState).isEqualByComparingTo(PersonalDataState.MISSING_OR_STALE);
    }

    @Test
        //Deprecated after disabling aion
    void shouldDetectValidPersonalDataForLowAmount() {
        // given
        UserStatesStore userStatesStore = new UserStatesStore();
        userStatesStore.setUserPersonalInformationStateDetails(StateDetails.builder()
                .requestDateTime(OffsetDateTime.now().minusMinutes(1))
                .responseDateTime(OffsetDateTime.now().minusMinutes(2))
                .state(Status.SUCCESS)
                .build());
        when(offersStateValidator.areStillValidPersonalDataOld(any())).thenReturn(true);

        // when
        // then
        assertThat(userStateEvaluator.getPersonalDataStateOld(userStatesStore)).isEqualByComparingTo(EXISTS);
    }

    @Test
        //Deprecated after disabling aion
    void shouldDetectValidPersonalDataForHighAmount() {
        // given
        UserStatesStore userStatesStore = new UserStatesStore();
        userStatesStore.setUserPersonalInformationStateDetails(StateDetails.builder()
                .requestDateTime(OffsetDateTime.now().minusMinutes(1))
                .responseDateTime(OffsetDateTime.now().minusMinutes(2))
                .state(Status.SUCCESS)
                .additionalFieldsForHighAmountAdded(true)
                .build());
        when(offersStateValidator.areStillValidPersonalDataOld(any())).thenReturn(true);

        // when
        // then
        assertThat(userStateEvaluator.getPersonalDataStateOld(userStatesStore)).isEqualByComparingTo(EXISTS);
    }

    @Test
        //Deprecated after disabling aion
    void shouldDetectInvalidPersonalDataForHighAmountWhenUserHighAmountDataIsMissing() {
        // given
        UserStatesStore userStatesStore = new UserStatesStore();
        userStatesStore.setUserPersonalInformationStateDetails(StateDetails.builder()
                .requestDateTime(OffsetDateTime.now().minusMinutes(1))
                .responseDateTime(OffsetDateTime.now().minusMinutes(2))
                .state(Status.SUCCESS)
                .additionalFieldsForHighAmountAdded(false)
                .build());
        when(offersStateValidator.areStillValidPersonalDataOld(any())).thenReturn(true);

        // when
        // then
        assertThat(userStateEvaluator.getPersonalDataStateOld(userStatesStore)).isEqualByComparingTo(EXISTS);
    }

    @Test
    void shouldDetectStalePersonalData() {
        // given
        UserStatesStore userStatesStore = new UserStatesStore();
        userStatesStore.setUserPersonalInformationStateDetails(StateDetails.builder()
                .requestDateTime(OffsetDateTime.now().minusMinutes(300))
                .responseDateTime(OffsetDateTime.now().minusMinutes(320))
                .state(Status.SUCCESS)
                .build());
        when(offersStateValidator.areStillValidPersonalDataOld(any())).thenReturn(false);

        // when
        // then
        assertThat(userStateEvaluator.getPersonalDataStateOld(userStatesStore)).isEqualByComparingTo(PersonalDataState.MISSING_OR_STALE);
    }

    @Test
    void shouldDetectMissingUserInput() {
        // given
        UserStatesStore userStatesStore = new UserStatesStore();
        userStatesStore.setUserPersonalInformationStateDetails(StateDetails.builder()
                .requestDateTime(OffsetDateTime.now().minusMinutes(300))
                .responseDateTime(OffsetDateTime.now().minusMinutes(320))
                .state(Status.MISSING_USER_INPUT)
                .build());

        // then
        assertThat(userStateEvaluator.getPersonalDataStateOld(userStatesStore)).isEqualByComparingTo(USER_INPUT_REQUIRED);
    }

    @Test
    void shouldDetectOfferFetchTimeout() {
        // given
        OfferDataStateDetails offerDataStateDetails = OfferDataStateDetails.builder()
                .requestDateTime(OffsetDateTime.now().minusMinutes(5))
                .amount(LOAN_AMOUNT)
                .build();
        UserStatesStore mockedUser = mock(UserStatesStore.class);
        when(mockedUser.getOfferDateStateDetailsSet()).thenReturn(Set.of(offerDataStateDetails));

        assertThat(userStateEvaluator.getOffersState(LOAN_AMOUNT, LOAN_PURPOSE, mockedUser)).isEqualByComparingTo(OffersState.MISSING_OR_STALE);
    }

    @Test
    void shouldDetectOfferFetchInProgress() {
        // given
        OfferDataStateDetails offerDataStateDetails = OfferDataStateDetails.builder()
                .requestDateTime(OffsetDateTime.now().minusMinutes(1))
                .amount(LOAN_AMOUNT)
                .build();
        UserStatesStore mockedUser = mock(UserStatesStore.class);
        when(mockedUser.getOfferDateStateDetailsSet()).thenReturn(Set.of(offerDataStateDetails));
        when(getOffersConfigurations.getStaleFetchingOffersRequestDurationInSeconds()).thenReturn(1000L);

        assertThat(userStateEvaluator.getOffersState(LOAN_AMOUNT, LOAN_PURPOSE, mockedUser)).isEqualByComparingTo(IN_PROGRESS);
    }

    @Test
    void shouldDetectFTSDacData() {
        // given
        when(getOffersConfigurations.getTransactionalDataValidityInDays()).thenReturn(30L);

        TransactionalDataStateDetails transactionalDataStateDetails = TransactionalDataStateDetails.builder()
                .userVerifiedByBankAccount(true)
                .responseDateTime(OffsetDateTime.now().minusMinutes(9))
                .sentForClassification(OffsetDateTime.now().minusMinutes(10))
                .state(Status.SUCCESS)
                .requestFromDataSolution(OffsetDateTime.now().minusMinutes(13))
                .responseFromDataSolution(OffsetDateTime.now().minusMinutes(15))
                .build();

        UserStatesStore mockedUser = mock(UserStatesStore.class);
        when(mockedUser.getTransactionalDataStateDetails()).thenReturn(transactionalDataStateDetails);

        // then
        assertThat(userStateEvaluator.getTransactionalDataState(mockedUser)).isEqualByComparingTo(FTS_DATA_EXISTS);
    }

    @Test
    void shouldDetectFTSInProgress() {
        // given
        TransactionalDataStateDetails transactionalDataStateDetails = TransactionalDataStateDetails.builder()
                .userVerifiedByBankAccount(true)
                .responseDateTime(null)
                .sentForClassification(OffsetDateTime.now().minusSeconds(1))
                .state(Status.SENT_FOR_CLASSIFICATION)
                .requestFromDataSolution(OffsetDateTime.now().minusMinutes(13))
                .responseFromDataSolution(OffsetDateTime.now().minusMinutes(15))
                .build();

        UserStatesStore mockedUser = mock(UserStatesStore.class);
        when(mockedUser.getTransactionalDataStateDetails()).thenReturn(transactionalDataStateDetails);
        when(getOffersConfigurations.getStaleFetchingTransactionsClassificationInSeconds()).thenReturn(60L);

        // then
        assertThat(userStateEvaluator.getTransactionalDataState(mockedUser)).isEqualByComparingTo(FETCHING_FROM_FTS);
    }

    @Test
    void shouldDetectDSRequestComplete() {
        //given
        TransactionalDataStateDetails transactionalDataStateDetails = TransactionalDataStateDetails.builder()
                .userVerifiedByBankAccount(true)
                .state(Status.WAITING_TO_SEND_FOR_CLASSIFICATION)
                .requestFromDataSolution(OffsetDateTime.now().minusMinutes(13))
                .responseFromDataSolution(OffsetDateTime.now().minusMinutes(15))
                .build();

        UserStatesStore mockedUser = mock(UserStatesStore.class);
        when(mockedUser.getTransactionalDataStateDetails()).thenReturn(transactionalDataStateDetails);

        // then
        assertThat(userStateEvaluator.getTransactionalDataState(mockedUser)).isEqualByComparingTo(MISSING_ACCOUNT_CLASSIFICATION);
    }

    @Test
    void shouldDetectDSRequestInProgress() {
        // given
        TransactionalDataStateDetails transactionalDataStateDetails = TransactionalDataStateDetails.builder()
                .userVerifiedByBankAccount(true)
                .requestFromDataSolution(OffsetDateTime.now().minusMinutes(1))
                .build();

        UserStatesStore mockedUser = mock(UserStatesStore.class);
        when(mockedUser.getTransactionalDataStateDetails()).thenReturn(transactionalDataStateDetails);
        when(getOffersConfigurations.getStaleFetchingSalaryAccountInSeconds()).thenReturn(1000L);

        // then
        assertThat(userStateEvaluator.getTransactionalDataState(mockedUser)).isEqualByComparingTo(FETCHING_FROM_DS);
    }

    @Test
    void shouldDetectDSFailureTimedOut() {
        // given
        TransactionalDataStateDetails transactionalDataStateDetails = TransactionalDataStateDetails.builder()
                .userVerifiedByBankAccount(true)
                .responseDateTime(null)
                .requestFromDataSolution(OffsetDateTime.now().minusMinutes(13))
                .responseFromDataSolution(null)
                .build();

        UserStatesStore mockedUser = mock(UserStatesStore.class);
        when(mockedUser.getTransactionalDataStateDetails()).thenReturn(transactionalDataStateDetails);
        when(getOffersConfigurations.getStaleFetchingSalaryAccountInSeconds()).thenReturn(60L);

        // then
        assertThat(userStateEvaluator.getTransactionalDataState(mockedUser)).isEqualByComparingTo(MISSING_SALARY_ACCOUNT);
    }


    @Test
    void shouldDetectMissingSalaryAccountComingFromDS() {
        // given
        TransactionalDataStateDetails transactionalDataStateDetails = TransactionalDataStateDetails.builder()
                .userVerifiedByBankAccount(true)
                .responseDateTime(null)
                .requestFromDataSolution(OffsetDateTime.now().minusMinutes(2))
                .responseFromDataSolution(OffsetDateTime.now().minusMinutes(1))
                .state(Status.MISSING_SALARY_ACCOUNT)
                .build();

        UserStatesStore mockedUser = mock(UserStatesStore.class);
        when(mockedUser.getTransactionalDataStateDetails()).thenReturn(transactionalDataStateDetails);

        // then
        assertThat(userStateEvaluator.getTransactionalDataState(mockedUser)).isEqualByComparingTo(MISSING_SALARY_ACCOUNT);
    }

    @Test
    void shouldDetectMissingSalaryAccountComingFromDAC() {
        // given
        TransactionalDataStateDetails transactionalDataStateDetails = TransactionalDataStateDetails.builder()
                .userVerifiedByBankAccount(true)
                .responseDateTime(OffsetDateTime.now().minusMinutes(1))
                .requestFromDataSolution(OffsetDateTime.now().minusMinutes(2))
                .responseFromDataSolution(null)
                .state(Status.MISSING_SALARY_ACCOUNT)
                .build();

        UserStatesStore mockedUser = mock(UserStatesStore.class);
        when(mockedUser.getTransactionalDataStateDetails()).thenReturn(transactionalDataStateDetails);

        // then
        assertThat(userStateEvaluator.getTransactionalDataState(mockedUser)).isEqualByComparingTo(MISSING_SALARY_ACCOUNT);
    }

    @Test
    void shouldDetectDSError() {
        // given
        TransactionalDataStateDetails transactionalDataStateDetails = TransactionalDataStateDetails.builder()
                .userVerifiedByBankAccount(true)
                .responseDateTime(null)
                .requestFromDataSolution(OffsetDateTime.now().minusMinutes(3))
                .responseFromDataSolution(null)
                .build();

        UserStatesStore mockedUser = mock(UserStatesStore.class);
        when(mockedUser.getTransactionalDataStateDetails()).thenReturn(transactionalDataStateDetails);
        when(getOffersConfigurations.getStaleFetchingSalaryAccountInSeconds()).thenReturn(60L);

        // then
        assertThat(userStateEvaluator.getTransactionalDataState(mockedUser)).isEqualByComparingTo(MISSING_SALARY_ACCOUNT);
    }

    @Test
    void shouldDetectFTSCleanupFailure() {
        // given
        TransactionalDataStateDetails transactionalDataStateDetails = TransactionalDataStateDetails.builder()
                .userVerifiedByBankAccount(true)
                .responseDateTime(null)
                .sentForClassification(OffsetDateTime.now().minusMinutes(11))
                .state(Status.SENT_FOR_CLASSIFICATION)
                .requestFromDataSolution(OffsetDateTime.now().minusMinutes(13))
                .responseFromDataSolution(OffsetDateTime.now().minusMinutes(15))
                .build();

        UserStatesStore mockedUser = mock(UserStatesStore.class);
        when(mockedUser.getTransactionalDataStateDetails()).thenReturn(transactionalDataStateDetails);
        when(getOffersConfigurations.getStaleFetchingTransactionsClassificationInSeconds()).thenReturn(60L);


        // then
        assertThat(userStateEvaluator.getTransactionalDataState(mockedUser)).isEqualByComparingTo(MISSING_SALARY_ACCOUNT);
    }

    @Test
    void shouldDetectFTSError() {
        // given
        TransactionalDataStateDetails transactionalDataStateDetails = TransactionalDataStateDetails.builder()
                .userVerifiedByBankAccount(true)
                .responseDateTime(null)
                .sentForClassification(OffsetDateTime.now().minusMinutes(9))
                .state(Status.SENT_FOR_CLASSIFICATION)
                .requestFromDataSolution(OffsetDateTime.now().minusMinutes(13))
                .responseFromDataSolution(OffsetDateTime.now().minusMinutes(15))
                .build();

        UserStatesStore mockedUser = mock(UserStatesStore.class);
        when(mockedUser.getTransactionalDataStateDetails()).thenReturn(transactionalDataStateDetails);
        when(getOffersConfigurations.getStaleFetchingTransactionsClassificationInSeconds()).thenReturn(60L);


        // then
        assertThat(userStateEvaluator.getTransactionalDataState(mockedUser)).isEqualByComparingTo(MISSING_SALARY_ACCOUNT);
    }

    @Test
    void shouldWaitForFusionAuthWhenNoDistributionChannelExist(@Random UserStatesStore userStatesStore) {
        //given
        userStatesStore.setDistributionChannel(null);
        userStatesStore.setTransactionalDataStateDetails(null);

        // when
        final var result = userStateEvaluator.getTransactionalDataState(userStatesStore);

        // then
        assertThat(result).isEqualByComparingTo(FETCHING_FROM_FUSIONAUTH);
    }

    @Test
    void shouldWaitForFusionAuthWhenNoUserStateStoreDetailsExist() {
        //given
        final var userStatesStore = new UserStatesStore();

        // when
        final var result = userStateEvaluator.getTransactionalDataState(userStatesStore);

        // then
        assertThat(result).isEqualByComparingTo(FETCHING_FROM_FUSIONAUTH);
    }

    @Test
    void shouldWaitForFusionAuthWhenBonifyUserWithNoBonifyUserId(@Random UserStatesStore userStatesStore) {
        //given
        TransactionalDataStateDetails transactionalDataStateDetails = TransactionalDataStateDetails.builder()
                .userVerifiedByBankAccount(null)
                .requestFromDataSolution(null)
                .responseFromDataSolution(null)
                .build();
        userStatesStore.setDistributionChannel(DistributionChannel.BONIFY);
        userStatesStore.setBonifyUserId(null);
        userStatesStore.setTransactionalDataStateDetails(transactionalDataStateDetails);

        // when
        final var result = userStateEvaluator.getTransactionalDataState(userStatesStore);

        // then
        assertThat(result).isEqualByComparingTo(FETCHING_FROM_FUSIONAUTH);
    }

    @Test
    void shouldWaitForFusionAuthWhenBonifyUserWithNoVerifiedBankAccount(@Random UserStatesStore userStatesStore) {
        //given
        TransactionalDataStateDetails transactionalDataStateDetails = TransactionalDataStateDetails.builder()
                .requestFromDataSolution(null)
                .responseFromDataSolution(null)
                .build();
        userStatesStore.setDistributionChannel(DistributionChannel.BONIFY);
        userStatesStore.setBonifyUserId(123L);
        userStatesStore.setTransactionalDataStateDetails(transactionalDataStateDetails);

        // when
        final var result = userStateEvaluator.getTransactionalDataState(userStatesStore);

        // then
        assertThat(result).isEqualByComparingTo(FETCHING_FROM_FUSIONAUTH);
    }

    @Test
    void shouldAskUserInputWhenBonifyUserWithNotVerifiedBankAccount(@Random UserStatesStore userStatesStore) {
        //given
        TransactionalDataStateDetails transactionalDataStateDetails = TransactionalDataStateDetails.builder()
                .requestFromDataSolution(null)
                .responseFromDataSolution(null)
                .userVerifiedByBankAccount(false)
                .build();
        userStatesStore.setDistributionChannel(DistributionChannel.BONIFY);
        userStatesStore.setBonifyUserId(123L);
        userStatesStore.setTransactionalDataStateDetails(transactionalDataStateDetails);

        // when
        final var result = userStateEvaluator.getTransactionalDataState(userStatesStore);

        // then
        assertThat(result).isEqualByComparingTo(NO_ACCOUNT_ADDED);
    }

    @Test
    void shouldRequestDSForSalaryAccountWhenAccountIsMissingOrStale(@Random UserStatesStore userStatesStore) {
        //given
        TransactionalDataStateDetails transactionalDataStateDetails = TransactionalDataStateDetails.builder()
                .requestFromDataSolution(null)
                .responseFromDataSolution(null)
                .userVerifiedByBankAccount(true)
                .build();
        userStatesStore.setDistributionChannel(DistributionChannel.BONIFY);
        userStatesStore.setBonifyUserId(123L);
        userStatesStore.setTransactionalDataStateDetails(transactionalDataStateDetails);

        // when
        final var result = userStateEvaluator.getTransactionalDataState(userStatesStore);

        // then
        assertThat(result).isEqualByComparingTo(DacDataState.MISSING_OR_STALE);
    }

    @Test
    void shouldAskUserInputWhenNotBonifyUserButTransactionalDataExists(@Random UserStatesStore userStatesStore) {
        //given
        TransactionalDataStateDetails transactionalDataStateDetails = TransactionalDataStateDetails.builder()
                .userVerifiedByBankAccount(true)
                .requestFromDataSolution(null)
                .responseFromDataSolution(null)
                .build();
        userStatesStore.setDistributionChannel(DistributionChannel.OTHERS);
        userStatesStore.setTransactionalDataStateDetails(transactionalDataStateDetails);

        // when
        final var result = userStateEvaluator.getTransactionalDataState(userStatesStore);

        // then
        assertThat(result).isEqualByComparingTo(NO_ACCOUNT_ADDED);
    }

    @Test
    void shouldAskUserInputWhenNotBonifyUserButNoTransactionalDataExists(@Random UserStatesStore userStatesStore) {
        //given
        userStatesStore.setDistributionChannel(DistributionChannel.OTHERS);
        userStatesStore.setTransactionalDataStateDetails(null);

        // when
        final var result = userStateEvaluator.getTransactionalDataState(userStatesStore);

        // then
        assertThat(result).isEqualByComparingTo(NO_ACCOUNT_ADDED);
    }
}
