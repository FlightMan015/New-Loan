package de.joonko.loan.integrations.domain;

import de.joonko.loan.integrations.configuration.GetOffersConfigurations;
import de.joonko.loan.user.states.OfferDataStateDetails;
import de.joonko.loan.user.states.StateDetails;
import de.joonko.loan.user.states.Status;
import de.joonko.loan.user.states.TransactionalDataStateDetails;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OffersStateValidatorTest {

    private static OffersStateValidator offersStateValidator;

    private static GetOffersConfigurations getOffersConfigurations;

    @BeforeAll
    static void setUp() {
        getOffersConfigurations = mock(GetOffersConfigurations.class);
        offersStateValidator = new OffersStateValidator(getOffersConfigurations);

        when(getOffersConfigurations.getTransactionalDataValidityInDays()).thenReturn(30L);
        when(getOffersConfigurations.getOffersValidityInDays()).thenReturn(14L);
        when(getOffersConfigurations.getUserPersonalInformationValidityInHours()).thenReturn(24L);
        when(getOffersConfigurations.getUserAdditionalInformationValidityInHours()).thenReturn(24L);

        when(getOffersConfigurations.getStaleFetchingTransactionalDataRequestInSeconds()).thenReturn(120L);
        when(getOffersConfigurations.getStaleFetchingPersonalDetailsInSeconds()).thenReturn(120L);
        when(getOffersConfigurations.getStaleFetchingAdditionalUserInformationRequestInSeconds()).thenReturn(120L);
        when(getOffersConfigurations.getStaleFetchingOffersRequestDurationInSeconds()).thenReturn(120L);
        when(getOffersConfigurations.getStaleFetchingSalaryAccountInSeconds()).thenReturn(120L);
        when(getOffersConfigurations.getStaleFetchingTransactionsClassificationInSeconds()).thenReturn(120L);
    }

    @Test
    void getting_transactional_data_is_not_stale() {
        // given
        TransactionalDataStateDetails transactionalDataStateDetails = TransactionalDataStateDetails.builder()
                .requestFromDataSolution(OffsetDateTime.now().minusSeconds(30))
                .build();

        // when
        boolean isStale = offersStateValidator.isStaleGettingTransactionData(transactionalDataStateDetails);

        // then
        assertFalse(isStale);
    }

    @Test
    void getting_transactional_data_is_stale() {
        // given
        TransactionalDataStateDetails transactionalDataStateDetails = TransactionalDataStateDetails.builder()
                .requestFromDataSolution(OffsetDateTime.now().minusSeconds(180))
                .build();

        // when
        boolean isStale = offersStateValidator.isStaleGettingTransactionData(transactionalDataStateDetails);

        // then
        assertTrue(isStale);
    }

    @Test
    void getting_transactional_data_is_stale_cause_null() {
        // given
        TransactionalDataStateDetails transactionalDataStateDetails = TransactionalDataStateDetails.builder().build();

        // when
        boolean isStale = offersStateValidator.isStaleGettingTransactionData(transactionalDataStateDetails);

        // then
        assertTrue(isStale);
    }

    @Test
    void getting_personal_data_is_not_stale() {
        // given
        StateDetails stateDetails = StateDetails.builder()
                .requestDateTime(OffsetDateTime.now().minusSeconds(30))
                .build();

        // when
        boolean isStale = offersStateValidator.isStaleGettingPersonalData(stateDetails);

        // then
        assertFalse(isStale);
    }

    @Test
    void getting_personal_data_is_stale() {
        // given
        StateDetails stateDetails = StateDetails.builder()
                .requestDateTime(OffsetDateTime.now().minusSeconds(180))
                .build();

        // when
        boolean isStale = offersStateValidator.isStaleGettingPersonalData(stateDetails);

        // then
        assertTrue(isStale);
    }

    @Test
    void getting_personal_data_is_stale_cause_null() {
        // given
        StateDetails stateDetails = StateDetails.builder().build();

        // when
        boolean isStale = offersStateValidator.isStaleGettingPersonalData(stateDetails);

        // then
        assertTrue(isStale);
    }

    @Test
    void getting_additional_data_is_not_stale() {
        // given
        StateDetails stateDetails = StateDetails.builder()
                .requestDateTime(OffsetDateTime.now().minusSeconds(30))
                .build();

        // when
        boolean isStale = offersStateValidator.isStaleGettingAdditionalInformation(stateDetails);

        // then
        assertFalse(isStale);
    }

    @Test
    void getting_additional_data_is_stale() {
        // given
        StateDetails stateDetails = StateDetails.builder()
                .requestDateTime(OffsetDateTime.now().minusSeconds(180))
                .build();

        // when
        boolean isStale = offersStateValidator.isStaleGettingAdditionalInformation(stateDetails);

        // then
        assertTrue(isStale);
    }

    @Test
    void getting_additional_data_is_stale_cause_null() {
        // given
        StateDetails stateDetails = StateDetails.builder().build();

        // when
        boolean isStale = offersStateValidator.isStaleGettingAdditionalInformation(stateDetails);

        // then
        assertTrue(isStale);
    }

    @Test
    void getting_offers_is_not_stale() {
        // given
        OfferDataStateDetails offerDataStateDetails = OfferDataStateDetails.builder()
                .requestDateTime(OffsetDateTime.now().minusSeconds(30))
                .build();

        // when
        boolean isStale = offersStateValidator.isStaleGettingOffers(offerDataStateDetails);

        // then
        assertFalse(isStale);
    }

    @Test
    void getting_offers_is_stale() {
        // given
        OfferDataStateDetails offerDataStateDetails = OfferDataStateDetails.builder()
                .requestDateTime(OffsetDateTime.now().minusSeconds(180))
                .build();

        // when
        boolean isStale = offersStateValidator.isStaleGettingOffers(offerDataStateDetails);

        // then
        assertTrue(isStale);
    }

    @Test
    void getting_offers_is_stale_cause_null() {
        // given
        OfferDataStateDetails offerDataStateDetails = OfferDataStateDetails.builder().build();

        // when
        boolean isStale = offersStateValidator.isStaleGettingOffers(offerDataStateDetails);

        // then
        assertTrue(isStale);
    }

    @Test
    void getting_salary_account_is_not_stale() {
        // given
        TransactionalDataStateDetails transactionalDataStateDetails = TransactionalDataStateDetails.builder()
                .requestFromDataSolution(OffsetDateTime.now().minusMinutes(1))
                .build();

        // when
        boolean isStale = offersStateValidator.isStaleGettingSalaryAccount(transactionalDataStateDetails);

        // then
        assertFalse(isStale);
    }

    @Test
    void getting_salary_account_is_stale() {
        // given
        TransactionalDataStateDetails transactionalDataStateDetails = TransactionalDataStateDetails.builder()
                .requestFromDataSolution(OffsetDateTime.now().minusMinutes(5))
                .build();

        // when
        boolean isStale = offersStateValidator.isStaleGettingSalaryAccount(transactionalDataStateDetails);

        // then
        assertTrue(isStale);
    }

    @Test
    void getting_salary_account_is_stale_cause_null() {
        // given
        TransactionalDataStateDetails transactionalDataStateDetails = TransactionalDataStateDetails.builder().build();

        // when
        boolean isStale = offersStateValidator.isStaleGettingSalaryAccount(transactionalDataStateDetails);

        // then
        assertTrue(isStale);
    }

    @Test
    void getting_transactions_classification_is_not_stale() {
        // given
        TransactionalDataStateDetails transactionalDataStateDetails = TransactionalDataStateDetails.builder()
                .sentForClassification(OffsetDateTime.now().minusMinutes(1))
                .build();

        // when
        boolean isStale = offersStateValidator.isStaleGettingTransactionsClassification(transactionalDataStateDetails);

        // then
        assertFalse(isStale);
    }

    @Test
    void getting_transactions_classification_is_stale() {
        // given
        TransactionalDataStateDetails transactionalDataStateDetails = TransactionalDataStateDetails.builder()
                .sentForClassification(OffsetDateTime.now().minusMinutes(5))
                .build();

        // when
        boolean isStale = offersStateValidator.isStaleGettingTransactionsClassification(transactionalDataStateDetails);

        // then
        assertTrue(isStale);
    }

    @Test
    void getting_transactions_classification_is_stale_cause_null() {
        // given
        TransactionalDataStateDetails transactionalDataStateDetails = TransactionalDataStateDetails.builder().build();

        // when
        boolean isStale = offersStateValidator.isStaleGettingTransactionsClassification(transactionalDataStateDetails);

        // then
        assertTrue(isStale);
    }

    @Test
    void transactional_data_is_still_valid() {
        // given
        TransactionalDataStateDetails transactionalDataStateDetails = TransactionalDataStateDetails.builder()
                .responseDateTime(OffsetDateTime.now().minusDays(5))
                .build();

        // when
        boolean isValid = offersStateValidator.areStillValidTransactionalData(transactionalDataStateDetails);

        // then
        assertTrue(isValid);
    }

    @Test
    void transactional_data_is_not_valid_anymore() {
        // given
        TransactionalDataStateDetails transactionalDataStateDetails = TransactionalDataStateDetails.builder()
                .responseDateTime(OffsetDateTime.now().minusDays(35))
                .build();

        // when
        boolean isValid = offersStateValidator.areStillValidTransactionalData(transactionalDataStateDetails);

        // then
        assertFalse(isValid);
    }

    @Test
    void transactional_data_is_not_valid_cause_null() {
        // given
        TransactionalDataStateDetails transactionalDataStateDetails = TransactionalDataStateDetails.builder().build();

        // when
        boolean isValid = offersStateValidator.areStillValidTransactionalData(transactionalDataStateDetails);

        // then
        assertFalse(isValid);
    }

    @Test
    void personal_data_is_still_valid() {
        // given
        StateDetails stateDetails = StateDetails.builder()
                .state(Status.SUCCESS)
                .responseDateTime(OffsetDateTime.now().minusHours(5))
                .build();

        // when
        boolean isValid = offersStateValidator.areStillValidPersonalDataOld(stateDetails);

        // then
        assertTrue(isValid);
    }

    @Test
    void personal_data_is_not_valid() {
        // given
        StateDetails stateDetails = StateDetails.builder()
                .state(Status.MISSING_USER_INPUT)
                .responseDateTime(OffsetDateTime.now().minusHours(1))
                .build();

        // when
        boolean isValid = offersStateValidator.areStillValidPersonalDataOld(stateDetails);

        // then
        assertFalse(isValid);
    }

    @Test
    void personal_data_is_not_valid_anymore() {
        // given
        StateDetails stateDetails = StateDetails.builder()
                .state(Status.SUCCESS)
                .responseDateTime(OffsetDateTime.now().minusHours(50))
                .build();

        // when
        boolean isValid = offersStateValidator.areStillValidPersonalDataOld(stateDetails);

        // then
        assertFalse(isValid);
    }

    @Test
    void personal_data_is_not_valid_cause_null() {
        // given
        StateDetails stateDetails = StateDetails.builder().build();

        // when
        boolean isValid = offersStateValidator.areStillValidPersonalDataOld(stateDetails);

        // then
        assertFalse(isValid);
    }

    @Test
    void validUserDataForRequestedAmount_smallAmountCase_valid() {
        // given
        StateDetails stateDetails = StateDetails.builder()
                .requestDateTime(OffsetDateTime.now().minusMinutes(1))
                .responseDateTime(OffsetDateTime.now().minusMinutes(2))
                .state(Status.SUCCESS)
                .additionalFieldsForHighAmountAdded(false)
                .build();

        // when
        when(getOffersConfigurations.getUserAdditionalInputRequiredMinAmount()).thenReturn(7000);

        // then
        boolean isValid = offersStateValidator.validUserDataForRequestedAmount(1000, stateDetails);

        assertTrue(isValid);
    }

    @Test
    void validUserDataForRequestedAmount_highAmountCaseAndNullValue_invalid() {
        // given
        StateDetails stateDetails = StateDetails.builder()
                .requestDateTime(OffsetDateTime.now().minusMinutes(1))
                .responseDateTime(OffsetDateTime.now().minusMinutes(2))
                .state(Status.SUCCESS)
                .additionalFieldsForHighAmountAdded(null)
                .build();

        // when
        when(getOffersConfigurations.getUserAdditionalInputRequiredMinAmount()).thenReturn(7000);

        // then
        boolean isValid = offersStateValidator.validUserDataForRequestedAmount(10000, stateDetails);

        assertFalse(isValid);
    }

    @Test
    void validUserDataForRequestedAmount_highAmountCaseAndFalseValue_invalid() {
        // given
        StateDetails stateDetails = StateDetails.builder()
                .requestDateTime(OffsetDateTime.now().minusMinutes(1))
                .responseDateTime(OffsetDateTime.now().minusMinutes(2))
                .state(Status.SUCCESS)
                .additionalFieldsForHighAmountAdded(false)
                .build();

        // when
        when(getOffersConfigurations.getUserAdditionalInputRequiredMinAmount()).thenReturn(7000);

        // then
        boolean isValid = offersStateValidator.validUserDataForRequestedAmount(10000, stateDetails);

        assertFalse(isValid);
    }

    @Test
    void validUserDataForRequestedAmount_highAmountCaseAndTrueValue_valid() {
        // given
        StateDetails stateDetails = StateDetails.builder()
                .requestDateTime(OffsetDateTime.now().minusMinutes(1))
                .responseDateTime(OffsetDateTime.now().minusMinutes(2))
                .state(Status.SUCCESS)
                .additionalFieldsForHighAmountAdded(true)
                .build();

        // when
        when(getOffersConfigurations.getUserAdditionalInputRequiredMinAmount()).thenReturn(7000);

        // then
        boolean isValid = offersStateValidator.validUserDataForRequestedAmount(10000, stateDetails);

        assertTrue(isValid);
    }

    @Test
    void offers_are_still_valid() {
        // given
        OfferDataStateDetails offerDataStateDetails = OfferDataStateDetails.builder()
                .responseDateTime(OffsetDateTime.now().minusDays(5))
                .build();

        // when
        boolean isValid = offersStateValidator.areStillValidOffers(offerDataStateDetails);

        // then
        assertTrue(isValid);
    }

    @Test
    void offers_are_not_valid_anymore() {
        // given
        OfferDataStateDetails offerDataStateDetails = OfferDataStateDetails.builder()
                .responseDateTime(OffsetDateTime.now().minusDays(31))
                .build();

        // when
        boolean isValid = offersStateValidator.areStillValidOffers(offerDataStateDetails);

        // then
        assertFalse(isValid);
    }

    @Test
    void offers_are_not_valid_cause_null() {
        // given
        OfferDataStateDetails offerDataStateDetails = OfferDataStateDetails.builder().build();

        // when
        boolean isValid = offersStateValidator.areStillValidOffers(offerDataStateDetails);

        // then
        assertFalse(isValid);
    }


}
