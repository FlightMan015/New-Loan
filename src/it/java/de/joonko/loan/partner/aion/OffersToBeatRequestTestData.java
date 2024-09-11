package de.joonko.loan.partner.aion;

import de.joonko.loan.partner.aion.model.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class OffersToBeatRequestTestData {

    public static BestOffersRequest buildBestOffersRequest() {
        return BestOffersRequest.builder()
                .transmissionDataType(TransmissionDataType.OFFERS_TO_BEAT)
                .transmissionData(BestOfferTransmissionData.builder()
                        .requestedLoanAmount(BigDecimal.valueOf(2000))
                        .offers(List.of(
                                BestOfferValue.builder()
                                        .category(BestOfferCategory.APR.getLabel())
                                        .offerDetails(OfferDetails.builder()
                                                .id("55babd2a-16ef-4167-a4f0-5b5be458bbb6")
                                                .amount(BigDecimal.valueOf(2000))
                                                .maturity(12)
                                                .annualPercentageRate(new BigDecimal("0.082"))
                                                .nominalInterestRate(new BigDecimal("0.082"))
                                                .monthlyInstalmentAmount(new BigDecimal("122.00"))
                                                .totalRepaymentAmount(new BigDecimal("2252.00"))
                                                .build())
                                        .build(),
                                BestOfferValue.builder()
                                        .category(BestOfferCategory.MONTHLY_INSTALLMENT.getLabel())
                                        .offerDetails(OfferDetails.builder()
                                                .id(UUID.randomUUID().toString())
                                                .amount(BigDecimal.valueOf(2000))
                                                .maturity(12)
                                                .annualPercentageRate(new BigDecimal("0.082"))
                                                .nominalInterestRate(new BigDecimal("0.082"))
                                                .monthlyInstalmentAmount(new BigDecimal("122.00"))
                                                .totalRepaymentAmount(new BigDecimal("2252.00"))
                                                .build())
                                        .build(),
                                BestOfferValue.builder()
                                        .category(BestOfferCategory.TOTAL_REPAYMENT.getLabel())
                                        .offerDetails(OfferDetails.builder()
                                                .id(UUID.randomUUID().toString())
                                                .amount(BigDecimal.valueOf(2000))
                                                .maturity(12)
                                                .annualPercentageRate(new BigDecimal("0.082"))
                                                .nominalInterestRate(new BigDecimal("0.082"))
                                                .monthlyInstalmentAmount(new BigDecimal("122.00"))
                                                .totalRepaymentAmount(new BigDecimal("2252.00"))
                                                .build())
                                        .build()
                        ))

                        .build())
                .build();
    }
}
