package de.joonko.loan.integrations.domain.integrationhandler.userpersonal;

import de.joonko.loan.integrations.model.OfferRequest;
import de.joonko.loan.integrations.model.OffersState;
import de.joonko.loan.integrations.model.PersonalDataState;
import de.joonko.loan.integrations.model.UserState;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserPersonalDataFilterTest {

    private static UserPersonalDataFilter filter;

    private static final String USER_ID = "2f20a660-f0f2-4ca5-9fe6-b24b52cd1070";
    private static final String REQUESTED_LOAN_PURPOSE = "car";
    private static final boolean REQUESTED_BONIFY_LOANS = false;

    @BeforeAll
    static void beforeAll() {
        filter = new UserPersonalDataFilter();
    }

    @ParameterizedTest
    @MethodSource("getTestData")
    void passingLoanDemandIntegrationHandlerFilter(OfferRequest input, boolean expectedFiltered, String errorMsg) {
        // given
        // when
        boolean isFiltered = filter.test(input);

        // then
        assertEquals(expectedFiltered, isFiltered, errorMsg);
    }

    private static Stream<Arguments> getTestData() {
        return Stream.of(
                Arguments.of(
                        getOffersExistState(),
                        false,
                        "should return false for offersState=OFFERS_EXIST"),
                Arguments.of(
                        getUserInputRequiredState(),
                        false,
                        "should return false for personalDataState=USER_INPUT_REQUIRED"),
                Arguments.of(
                        getMissingOrStaleState(),
                        true,
                        "should return true for personalDataState=MISSING_OR_STALE")
        );
    }

    private static OfferRequest getMissingOrStaleState() {
        return new OfferRequest(USER_ID, 7823951320L, 5000, REQUESTED_LOAN_PURPOSE, REQUESTED_BONIFY_LOANS, null, null, UserState.builder()
                .offersState(OffersState.MISSING_OR_STALE)
                .personalDataState(PersonalDataState.MISSING_OR_STALE).build());
    }

    private static OfferRequest getUserInputRequiredState() {
        return new OfferRequest(USER_ID, 7823951320L, 5000, REQUESTED_LOAN_PURPOSE, REQUESTED_BONIFY_LOANS, null, null, UserState.builder()
                .offersState(OffersState.MISSING_OR_STALE)
                .personalDataState(PersonalDataState.USER_INPUT_REQUIRED).build());
    }

    private static OfferRequest getOffersExistState() {
        return new OfferRequest(USER_ID, 7823951320L, 5000, REQUESTED_LOAN_PURPOSE, REQUESTED_BONIFY_LOANS, null, null, UserState.builder()
                .offersState(OffersState.OFFERS_EXIST)
                .personalDataState(PersonalDataState.MISSING_OR_STALE).build());
    }
}
