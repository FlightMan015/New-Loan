package de.joonko.loan.offer.domain;

import de.joonko.loan.partner.aion.AionLoanDemandGateway;
import de.joonko.loan.partner.aion.AionPropertiesConfig;
import de.joonko.loan.partner.aion.AionStoreService;
import de.joonko.loan.partner.aion.model.AionResponseValueType;
import de.joonko.loan.partner.aion.model.BestOfferCategory;
import de.joonko.loan.partner.aion.model.BestOfferTransmissionData;
import de.joonko.loan.partner.aion.model.BestOfferValue;
import de.joonko.loan.partner.aion.model.BestOffersRequest;
import de.joonko.loan.partner.aion.model.CreditApplicationResponseStore;
import de.joonko.loan.partner.aion.model.OfferDetails;
import de.joonko.loan.partner.aion.model.TransmissionDataType;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static java.util.UUID.randomUUID;
import static java.util.stream.Collectors.toList;

@Component
@Slf4j
@RequiredArgsConstructor
public class BonifyLoanOfferService {

    private static final String POSITIVE_RESPONSE = "POSITIVE";

    private final AionLoanDemandGateway aionLoanDemandGateway;

    private final AionStoreService aionStoreService;

    private final AionPropertiesConfig aionPropertiesConfig;

    public Flux<LoanOffer> combineBonifyOffersWithOtherBankOffers(final LoanDemand loanDemand, final List<LoanOffer> list) {
        return Flux.just(aionPropertiesConfig.getEnabled())
                .flatMap(enabled -> {
                            if (Boolean.TRUE.equals(enabled) && aionStoreService != null) {
                                return calculateBonifyOffers(loanDemand, list);
                            }
                            return Flux.empty();
                        }
                )
                .collectList()
                .flatMapIterable(bonifyOffers -> {
                            bonifyOffers.addAll(list);
                            return bonifyOffers;
                        }
                );
    }

    private Flux<LoanOffer> calculateBonifyOffers(final LoanDemand loanDemand, final List<LoanOffer> loanOffers) {
        return getCreditApplicationPositiveProcess(loanDemand)
                .flatMap(process -> {
                    if (!loanOffers.isEmpty()) {
                        final var bestOffers = BestOfferService.calculateBestOffersPerCategory(loanOffers);
                        bestOffers.forEach(bestOffer ->
                                bestOffer.setOfferId(randomUUID().toString()));
                        return aionStoreService.addBestOffers(process.getApplicationId(), bestOffers);
                    }
                    return Mono.just(process);
                })
                .flatMapMany(existingProcess ->
                {
                    final BestOffersRequest bestOfferRequest = buildBestOfferRequest(loanDemand, existingProcess.getOffersToBeat());
                    final BestOffersRequest[] bestOffersRequests = new BestOffersRequest[]{bestOfferRequest};

                    return aionLoanDemandGateway.getOffers(loanDemand.getLoanApplicationId(), existingProcess.getProcessId(), bestOffersRequests);
                });
    }

    private BestOffersRequest buildBestOfferRequest(LoanDemand loanDemand, List<BestLoanOffer> calculatedBestOffers) {
        return BestOffersRequest.builder()
                .transmissionDataType(TransmissionDataType.OFFERS_TO_BEAT)
                .transmissionData(BestOfferTransmissionData.builder()
                        .requestedLoanAmount(BigDecimal.valueOf(loanDemand.getLoanAsked()))
                        .requestedLoanCurrency(Currency.EUR.name())
                        .offers(map(calculatedBestOffers))
                        .build())
                .build();
    }

    private Mono<CreditApplicationResponseStore> getCreditApplicationPositiveProcess(LoanDemand loanDemand) {
        return aionStoreService.findByApplicationId(loanDemand.getLoanApplicationId())
                .flatMap(optionalExistingProcess -> optionalExistingProcess
                        .map(Mono::just)
                        .orElse(Mono.empty()));
    }

    private boolean isPositiveCreditApplicationResponse(CreditApplicationResponseStore existingProcess) {
        return existingProcess.getVariables().stream()
                .anyMatch(variable -> AionResponseValueType.DECISION.equals(variable.getName())
                        && POSITIVE_RESPONSE.equals(variable.getValue()));
    }


    private List<BestOfferValue> map(final List<BestLoanOffer> bestOffers) {
        return bestOffers.stream()
                .map(offer -> BestOfferValue.builder()
                        .category(mapCategory(offer.getOfferCategory()).getLabel())
                        .offerDetails(OfferDetails.builder()
                                .id(offer.getOfferId())
                                .amount(BigDecimal.valueOf(offer.getAmount()))
                                .maturity(offer.getDurationInMonth())
                                .annualPercentageRate(offer.getApr())
                                .nominalInterestRate(offer.getNominalInterestRate().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP))
                                .monthlyInstalmentAmount(offer.getMonthlyRate())
                                .totalRepaymentAmount(offer.getTotalPayment())
                                .build())
                        .build())
                .collect(toList());
    }

    private BestOfferCategory mapCategory(final OfferCategory offerCategory) {
        BestOfferCategory category;
        switch (offerCategory) {
            case APR:
                category = BestOfferCategory.APR;
                break;
            case MONTHLY_INSTALLMENT_AMOUNT:
                category = BestOfferCategory.MONTHLY_INSTALLMENT;
                break;
            case TOTAL_REPAYMENT_AMOUNT:
                category = BestOfferCategory.TOTAL_REPAYMENT;
                break;
            default:
                throw new IllegalStateException(String.format("%s OfferCategory not supported for best offer", offerCategory));
        }
        return category;
    }
}
