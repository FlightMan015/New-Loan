package de.joonko.loan.offer.domain;

import de.joonko.loan.util.BigDecimalUtil;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static de.joonko.loan.util.LoanCalculatorUtil.calculateSimpleLoan;
import static java.util.Collections.min;
import static java.util.stream.Collectors.toList;

@Service
public class BestOfferService {

    private static final BigDecimal MIN_DEVIATION = BigDecimal.valueOf(0.02);
    private static final BigDecimal MAX_DEVIATION = BigDecimal.valueOf(0.07);

    private BestOfferService() {
    }

    static List<BestLoanOffer> calculateBestOffersPerCategory(List<LoanOffer> loanOfferList) {
        if (loanOfferList.isEmpty()) {
            return List.of();
        }

        return Arrays.stream(OfferCategory.values()).map(category ->
                calculateBestOfferForCategory(loanOfferList, category)
        ).collect(toList());
    }

    private static BestLoanOffer calculateBestOfferForCategory(final List<LoanOffer> loanOfferList, final OfferCategory offerCategory) {
        final var bestOfferInCategory = getLoanOffer(loanOfferList, offerCategory);

        final var desiredInterestRate = calculateDesiredInterestRate(loanOfferList, bestOfferInCategory);
        return calculateOfferForCategory(offerCategory, desiredInterestRate, bestOfferInCategory.getDurationInMonth(), bestOfferInCategory.getAmount());
    }

    private static BigDecimal calculateDesiredInterestRate(final List<LoanOffer> loanOfferList, final LoanOffer bestOffer) {
        final var minInterestRate = bestOffer.getEffectiveInterestRate();
        final BigDecimal averageInterestRate = findAverageInterestRate(loanOfferList, bestOffer.getDurationInMonth());
        final var diffInterestRate = MAX_DEVIATION.subtract(averageInterestRate.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
        final BigDecimal deviation = normalizeRateInRanges(diffInterestRate);
        return minInterestRate.subtract(deviation);
    }

    private static BigDecimal normalizeRateInRanges(final BigDecimal diffInterestRate) {
        if (diffInterestRate.compareTo(MIN_DEVIATION) < 0) {
            return MIN_DEVIATION;
        } else if (diffInterestRate.compareTo(MAX_DEVIATION) > 0) {
            return MAX_DEVIATION;
        }
        return diffInterestRate;
    }

    private static LoanOffer getLoanOffer(final List<LoanOffer> loanOfferList, final OfferCategory offerCategory) {
        LoanOffer bestOfferPerCategory;
        switch (offerCategory) {
            case APR:
                bestOfferPerCategory = min(loanOfferList, Comparator.comparing(LoanOffer::getEffectiveInterestRate).thenComparing(LoanOffer::getDurationInMonth));
                break;
            case MONTHLY_INSTALLMENT_AMOUNT:
                bestOfferPerCategory = min(loanOfferList, Comparator.comparing(LoanOffer::getMonthlyRate).thenComparing(LoanOffer::getDurationInMonth));
                break;
            case TOTAL_REPAYMENT_AMOUNT:
                bestOfferPerCategory = min(loanOfferList, Comparator.comparing(LoanOffer::getTotalPayment).thenComparing(LoanOffer::getDurationInMonth));
                break;
            default:
                throw new RuntimeException(String.format("Offer category for best offer definition not supported - %s", offerCategory));
        }

        return bestOfferPerCategory;
    }

    private static BigDecimal findAverageInterestRate(final List<LoanOffer> loanOfferList, final int duration) {
        final var offersWithSameDuration = loanOfferList.stream()
                .filter(offer -> offer.getDurationInMonth() == duration)
                .map(LoanOffer::getEffectiveInterestRate)
                .collect(Collectors.toSet());

        return BigDecimalUtil.average(offersWithSameDuration)
                .orElseThrow(() -> new RuntimeException(String.format("Could not get average of the loan offers for duration - %s", duration)));
    }

    private static BestLoanOffer calculateOfferForCategory(final OfferCategory offerCategory, final BigDecimal effectiveInterestRate, final int durationInMonth, final int amount) {
        final var simpleLoan = calculateSimpleLoan(BigDecimal.valueOf(amount), durationInMonth, effectiveInterestRate);
        return BestLoanOffer.builder()
                .offerCategory(offerCategory)
                .amount(amount)
                .durationInMonth(durationInMonth)
                .effectiveInterestRate(effectiveInterestRate)
                .nominalInterestRate(simpleLoan.getNominalInterestRate())
                .apr(effectiveInterestRate.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP))
                .monthlyRate(simpleLoan.getMonthlyRate())
                .totalPayment(simpleLoan.getTotalPayment())
                .build();
    }
}
