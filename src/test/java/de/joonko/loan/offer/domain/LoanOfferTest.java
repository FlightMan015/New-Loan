package de.joonko.loan.offer.domain;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static de.joonko.loan.offer.domain.FinanceFixtures.getFinanceFreeToSpend;
import static de.joonko.loan.offer.domain.FinanceFixtures.getLoanOfferWithMonthlyRate;
import static org.junit.jupiter.api.Assertions.assertEquals;


class LoanOfferTest {


    @ParameterizedTest
    @MethodSource("creatFreeToSpendData")
    void calculateRank(LoanOffer loanOffer, Finance finance, int expectedScore) {
        int score = loanOffer.calculateScore(finance);
        assertEquals(expectedScore, score);

    }


    static Stream<Arguments> creatFreeToSpendData() {
        return Stream.of(
                Arguments.of(getLoanOfferWithMonthlyRate(100), getFinanceFreeToSpend(200), 0),
                Arguments.of(getLoanOfferWithMonthlyRate(50), getFinanceFreeToSpend(200), 50),
                Arguments.of(getLoanOfferWithMonthlyRate(150), getFinanceFreeToSpend(200), 50),
                Arguments.of(getLoanOfferWithMonthlyRate(10), getFinanceFreeToSpend(200), 90),
                Arguments.of(getLoanOfferWithMonthlyRate(170), getFinanceFreeToSpend(200), 70));
    }



}
