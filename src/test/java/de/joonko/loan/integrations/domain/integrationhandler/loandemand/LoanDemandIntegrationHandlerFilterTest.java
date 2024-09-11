package de.joonko.loan.integrations.domain.integrationhandler.loandemand;

import de.joonko.loan.integrations.model.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoanDemandIntegrationHandlerFilterTest {

    private static LoanDemandIntegrationHandlerFilter filter;

    private static final String LOAN_PURPOSE = "car";
    private static final boolean BONIFY_LOANS = false;

    @BeforeAll
    static void beforeAll() {
        filter = new LoanDemandIntegrationHandlerFilter();
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
                        getOfferRequestWithEmptyUserState(),
                        false,
                        "should return false for null user state"),
                Arguments.of(
                        getInvalidOfferRequest(),
                        false,
                        "should return false for invalid user state"),
                Arguments.of(
                        getValidOfferRequest(),
                        true,
                        "should return true for valid user state")
        );
    }

    private static OfferRequest getValidOfferRequest() {
        return new OfferRequest("2f20a660-f0f2-4ca5-9fe6-b24b52cd1070", 7823951320L, 5000, LOAN_PURPOSE, BONIFY_LOANS,
                null, null,
                UserState.builder()
                        .offersState(OffersState.MISSING_OR_STALE)
                        .dacDataState(DacDataState.FTS_DATA_EXISTS)
                        .personalDataState(PersonalDataState.EXISTS).build());
    }

    private static OfferRequest getOfferRequestWithEmptyUserState() {
        return new OfferRequest("2f20a660-f0f2-4ca5-9fe6-b24b52cd1070", 7823951320L, 5000, LOAN_PURPOSE, BONIFY_LOANS,
                null, null,
                null);
    }

    private static OfferRequest getInvalidOfferRequest() {
        return new OfferRequest("2f20a660-f0f2-4ca5-9fe6-b24b52cd1070", 7823951320L, 5000, LOAN_PURPOSE, BONIFY_LOANS,
                null, null,
                UserState.builder()
                        .offersState(OffersState.IN_PROGRESS)
                        .dacDataState(DacDataState.FTS_DATA_EXISTS)
                        .personalDataState(PersonalDataState.EXISTS).build());
    }
}
