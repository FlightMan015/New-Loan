package de.joonko.loan.offer.domain;

import java.util.Comparator;

/**
 * @deprecated
 * Default sorting by monthly repayment amount ASC
 */
@Deprecated (forRemoval = true)
public class LoanInterestComparator implements Comparator<LoanOffer>  {

    // Add weight to offers where monthlyrate greater than free to spend to push them down the comparator.
    private static final int WEIGHT = 100000;
    private Finance finance;

    public LoanInterestComparator(Finance finance){
        this.finance = finance;
    }

    @Override
    public int compare(LoanOffer first, LoanOffer second) {

        boolean addWeightToFirstOffer = first.getMonthlyRate().intValue() >
                        first.halfOfFreeToSpend(finance);

        boolean addWeightToSecondOffer = second.getMonthlyRate().intValue() >
                second.halfOfFreeToSpend(finance);

        int firstOfferInterest = first.interestPayment();
        if(addWeightToFirstOffer) {
            firstOfferInterest += WEIGHT;
        }

        int secondOfferInterest = second.interestPayment();
        if(addWeightToSecondOffer) {
            secondOfferInterest += WEIGHT;
        }
        return firstOfferInterest - secondOfferInterest;
    }
}
