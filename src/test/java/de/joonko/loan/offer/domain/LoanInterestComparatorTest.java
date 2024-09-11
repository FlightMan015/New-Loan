package de.joonko.loan.offer.domain;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;

import java.util.List;

import static de.joonko.loan.offer.domain.FinanceFixtures.getFinanceFreeToSpend;
import static de.joonko.loan.offer.domain.FinanceFixtures.getLoanOfferWithMonthlyRateAmountAndDuration;
import static org.junit.jupiter.api.Assertions.assertEquals;

// Happy Path without Weight
class LoanInterestComparatorTest {

    @Test
    void compare_offers_with_equal_total_interest() {
        LoanDemand loanDemand = createLoanDemand(5000, LoanDuration.TWENTY_FOUR, Double.valueOf(30000).intValue());
        LoanInterestComparator comparator = new LoanInterestComparator(loanDemand.getPersonalDetails().getFinance());
        LoanOffer firstOfferInterest350 = getLoanOfferWithMonthlyRateAmountAndDuration(150, 1000, 8);
        LoanOffer firstOfferInterest200 = getLoanOfferWithMonthlyRateAmountAndDuration(200, 1000, 6);
        int compare = comparator.compare(firstOfferInterest350, firstOfferInterest200);
        assertEquals(0, compare);
    }

    @Test
    void compare_to_offer_with_higher_total_interest() {
        LoanDemand loanDemand = createLoanDemand(5000, LoanDuration.TWENTY_FOUR, Double.valueOf(30000).intValue());
        LoanInterestComparator comparator = new LoanInterestComparator(loanDemand.getPersonalDetails().getFinance());
        LoanOffer firstOfferInterest200 = getLoanOfferWithMonthlyRateAmountAndDuration(200, 1000, 6);
        LoanOffer firstOfferInterest350 = getLoanOfferWithMonthlyRateAmountAndDuration(150, 1000, 9);
        int compare = comparator.compare(firstOfferInterest200, firstOfferInterest350);
        assertEquals(-150, compare);
    }

    @Test
    void compare_to_offer_with_lower_total_interest() {
        LoanDemand loanDemand = createLoanDemand(5000, LoanDuration.TWENTY_FOUR, Double.valueOf(30000).intValue());
        LoanInterestComparator comparator = new LoanInterestComparator(loanDemand.getPersonalDetails().getFinance());
        LoanOffer firstOfferInterest200 = getLoanOfferWithMonthlyRateAmountAndDuration(200, 1000, 6);
        LoanOffer firstOfferInterest350 = getLoanOfferWithMonthlyRateAmountAndDuration(150, 1000, 9);
        int compare = comparator.compare(firstOfferInterest350, firstOfferInterest200);
        assertEquals(150, compare);
    }

    private LoanDemand createLoanDemand(int loanAsked, LoanDuration loanDuration, int freeToSpend) {
        return new LoanDemand(RandomStringUtils.randomAlphabetic(20), loanAsked, "car", loanDuration, LoanCategory.FURNITURE_RENOVATION_MOVE, PersonalDetails.builder()
                .finance(getFinanceFreeToSpend(freeToSpend))
                .build(), null, null, null, null, null, null, null, null, null, List.of(), null);
    }
}
