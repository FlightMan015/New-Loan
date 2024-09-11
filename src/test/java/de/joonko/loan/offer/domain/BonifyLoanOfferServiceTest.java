package de.joonko.loan.offer.domain;

import de.joonko.loan.common.domain.LoanProvider;
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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(RandomBeansExtension.class)
class BonifyLoanOfferServiceTest {

    private AionLoanDemandGateway aionLoanDemandGateway;

    private AionStoreService aionStoreService;

    private BonifyLoanOfferService bonifyLoanOfferService;

    private AionPropertiesConfig aionPropertiesConfig;

    @BeforeEach
    void setUp() {
        aionLoanDemandGateway = mock(AionLoanDemandGateway.class);
        aionStoreService = mock(AionStoreService.class);
        aionPropertiesConfig = mock(AionPropertiesConfig.class);
        bonifyLoanOfferService = new BonifyLoanOfferService(aionLoanDemandGateway, aionStoreService, aionPropertiesConfig);
    }

    @Test
    void combineBonifyOffersWithOtherBankOffers_whenAionIsDisabled(@Random LoanDemand loanDemand, @Random List<LoanOffer> loanOfferList) {
        // when
        when(aionPropertiesConfig.getEnabled()).thenReturn(false);

        final var loanOffers = bonifyLoanOfferService.combineBonifyOffersWithOtherBankOffers(loanDemand, loanOfferList);

        // then
        StepVerifier.create(loanOffers)
                .expectSubscription()
                .expectNextCount(loanOfferList.size())
                .verifyComplete();
    }

    @Test
    void combineBonifyOffersWithOtherBankOffers_whenEmptyListAndNoAionDataInDb(@Random LoanDemand loanDemand, @Random List<LoanOffer> loanOfferList) {
        // when
        when(aionPropertiesConfig.getEnabled()).thenReturn(true);
        when(aionStoreService.findByApplicationId(loanDemand.getLoanApplicationId())).thenReturn(Mono.just(Optional.empty()));

        final var loanOffers = bonifyLoanOfferService.combineBonifyOffersWithOtherBankOffers(loanDemand, loanOfferList);

        // then
        StepVerifier.create(loanOffers)
                .expectSubscription()
                .expectNextCount(loanOfferList.size())
                .verifyComplete();
    }

    @Test
    void combineBonifyOffersWithOtherBankOffers_whenExistsAionPositiveResponse_butEmptyListFromCompetitors(@Random LoanDemand loanDemand, @Random List<LoanOffer> loanOffers) {
        // given
        final var creditApplicationResponse = createCreditApplicationPositiveResponse();
        final var bestOfferRequest = mappedBestOffers(loanDemand, List.of());

        // when
        when(aionPropertiesConfig.getEnabled()).thenReturn(true);
        when(aionStoreService.findByApplicationId(loanDemand.getLoanApplicationId())).thenReturn(Mono.just(Optional.of(creditApplicationResponse)));
        when(aionStoreService.addBestOffers(creditApplicationResponse.getApplicationId(), List.of())).thenReturn(Mono.just(creditApplicationResponse));
        when(aionLoanDemandGateway.getOffers(loanDemand.getLoanApplicationId(), creditApplicationResponse.getProcessId(), bestOfferRequest)).thenReturn(Mono.just(loanOffers).flatMapMany(Flux::fromIterable));

        final var resultLoanOffers = bonifyLoanOfferService.combineBonifyOffersWithOtherBankOffers(loanDemand, List.of());

        // then
        StepVerifier.create(resultLoanOffers)
                .expectSubscription()
                .expectNextCount(loanOffers.size())
                .verifyComplete();
    }

    @Test
    void combineBonifyOffersWithOtherBankOffers_whenExistsAionPositiveResponse(@Random LoanDemand loanDemand) {
        // given
        final var calculatedBestOffers = BestOfferService.calculateBestOffersPerCategory(loanOffers());

        final var creditApplicationResponse = createCreditApplicationPositiveResponse();
        final var creditApplicationResponseWithBestOffers = addOffersToBeatToCreditApplicationresponse(calculatedBestOffers, creditApplicationResponse);
        final var bestOfferRequest = mappedBestOffers(loanDemand, calculatedBestOffers);

        // when
        when(aionPropertiesConfig.getEnabled()).thenReturn(true);
        when(aionStoreService.findByApplicationId(loanDemand.getLoanApplicationId())).thenReturn(Mono.just(Optional.of(creditApplicationResponse)));
        when(aionStoreService.addBestOffers(any(String.class), any(List.class))).thenReturn(Mono.just(creditApplicationResponseWithBestOffers));
        when(aionLoanDemandGateway.getOffers(loanDemand.getLoanApplicationId(), creditApplicationResponse.getProcessId(), bestOfferRequest)).thenReturn(Mono.just(bonifyLoanOffers()).flatMapMany(Flux::fromIterable));

        final var resultLoanOffers = bonifyLoanOfferService.combineBonifyOffersWithOtherBankOffers(loanDemand, loanOffers());

        // then
        StepVerifier.create(resultLoanOffers)
                .expectSubscription()
                .expectNextCount(loanOffers().size() + bonifyLoanOffers().size())
                .verifyComplete();
    }


    @Test
    void combineBonifyOffersWithOtherBankOffers_whenExistsAionNoDecisionResponse(@Random LoanDemand loanDemand) {
        // given
        final var calculatedBestOffers = BestOfferService.calculateBestOffersPerCategory(loanOffers());

        final var creditApplicationResponse = createCreditApplicationPositiveResponse().toBuilder()
                .variables(List.of(CreditApplicationResponseStore.Variable.builder()
                        .name(AionResponseValueType.DECISION)
                        .build()))
                .build();
        final var creditApplicationResponseWithBestOffers = addOffersToBeatToCreditApplicationresponse(calculatedBestOffers, creditApplicationResponse);
        final var bestOfferRequest = mappedBestOffers(loanDemand, calculatedBestOffers);

        // when
        when(aionPropertiesConfig.getEnabled()).thenReturn(true);
        when(aionStoreService.findByApplicationId(loanDemand.getLoanApplicationId())).thenReturn(Mono.just(Optional.of(creditApplicationResponse)));
        when(aionStoreService.addBestOffers(any(String.class), any(List.class))).thenReturn(Mono.just(creditApplicationResponseWithBestOffers));
        when(aionLoanDemandGateway.getOffers(loanDemand.getLoanApplicationId(), creditApplicationResponse.getProcessId(), bestOfferRequest)).thenReturn(Flux.empty());

        final var resultLoanOffers = bonifyLoanOfferService.combineBonifyOffersWithOtherBankOffers(loanDemand, loanOffers());

        // then

        assertAll(
                () -> StepVerifier.create(resultLoanOffers)
                        .expectSubscription()
                        .expectNextCount(loanOffers().size())
                        .verifyComplete(),
                () -> verify(aionLoanDemandGateway).getOffers(loanDemand.getLoanApplicationId(), creditApplicationResponse.getProcessId(), bestOfferRequest)
        );
    }

    @Test
    void combineBonifyOffersWithOtherBankOffers_whenExistsAionNegativeResponse(@Random LoanDemand loanDemand) {
        // given
        final var calculatedBestOffers = BestOfferService.calculateBestOffersPerCategory(loanOffers());

        final var creditApplicationResponse = createCreditApplicationPositiveResponse().toBuilder()
                .variables(List.of(CreditApplicationResponseStore.Variable.builder()
                        .name(AionResponseValueType.DECISION)
                        .value("NEGATIVE")
                        .build()))
                .build();
        final var creditApplicationResponseWithBestOffers = addOffersToBeatToCreditApplicationresponse(calculatedBestOffers, creditApplicationResponse);
        final var bestOfferRequest = mappedBestOffers(loanDemand, calculatedBestOffers);

        // when
        when(aionPropertiesConfig.getEnabled()).thenReturn(true);
        when(aionStoreService.findByApplicationId(loanDemand.getLoanApplicationId())).thenReturn(Mono.just(Optional.of(creditApplicationResponse)));
        when(aionStoreService.addBestOffers(any(String.class), any(List.class))).thenReturn(Mono.just(creditApplicationResponseWithBestOffers));
        when(aionLoanDemandGateway.getOffers(loanDemand.getLoanApplicationId(), creditApplicationResponse.getProcessId(), bestOfferRequest)).thenReturn(Flux.empty());

        final var resultLoanOffers = bonifyLoanOfferService.combineBonifyOffersWithOtherBankOffers(loanDemand, loanOffers());

        // then
        assertAll(
                () -> StepVerifier.create(resultLoanOffers)
                        .expectSubscription()
                        .expectNextCount(loanOffers().size())
                        .verifyComplete(),
                () -> verify(aionLoanDemandGateway).getOffers(loanDemand.getLoanApplicationId(), creditApplicationResponse.getProcessId(), bestOfferRequest)
        );
    }

    private CreditApplicationResponseStore addOffersToBeatToCreditApplicationresponse(List<BestLoanOffer> calculatedBestOffers, CreditApplicationResponseStore creditApplicationResponse) {
        return creditApplicationResponse.toBuilder()
                .offersToBeat(calculatedBestOffers)
                .build();
    }

    private CreditApplicationResponseStore createCreditApplicationPositiveResponse() {
        return CreditApplicationResponseStore.builder()
                .applicationId("1")
                .processId("2")
                .variables(List.of(CreditApplicationResponseStore.Variable.builder()
                        .name(AionResponseValueType.DECISION)
                        .value("POSITIVE")
                        .build()))
                .build();
    }

    private BestOffersRequest[] mappedBestOffers(LoanDemand loanDemand, List<BestLoanOffer> calculatedBestOffers) {
        return new BestOffersRequest[]{BestOffersRequest.builder()
                .transmissionDataType(TransmissionDataType.OFFERS_TO_BEAT)
                .transmissionData(BestOfferTransmissionData.builder()
                        .requestedLoanAmount(BigDecimal.valueOf(loanDemand.getLoanAsked()))
                        .requestedLoanCurrency("EUR")
                        .offers(map(calculatedBestOffers))
                        .build())
                .build()};
    }

    private List<LoanOffer> loanOffers() {
        return List.of(
                new LoanOffer(5000, 42, BigDecimal.valueOf(5.99), BigDecimal.ONE, BigDecimal.valueOf(131.89), BigDecimal.valueOf(5539.38), LoanProvider.builder().name("1").build()),
                new LoanOffer(5000, 24, BigDecimal.valueOf(5.99), BigDecimal.ONE, BigDecimal.valueOf(221.22), BigDecimal.valueOf(5309.28), LoanProvider.builder().name("1").build()),
                new LoanOffer(5000, 36, BigDecimal.valueOf(5.99), BigDecimal.ONE, BigDecimal.valueOf(151.72), BigDecimal.valueOf(5461.92), LoanProvider.builder().name("1").build()),
                new LoanOffer(5000, 66, BigDecimal.valueOf(5.99), BigDecimal.ONE, BigDecimal.valueOf(88.73), BigDecimal.valueOf(5856.18), LoanProvider.builder().name("1").build()),
                new LoanOffer(5000, 18, BigDecimal.valueOf(5.99), BigDecimal.ONE, BigDecimal.valueOf(290.77), BigDecimal.valueOf(5233.86), LoanProvider.builder().name("1").build()),
                new LoanOffer(5000, 12, BigDecimal.valueOf(5.99), BigDecimal.ONE, BigDecimal.valueOf(429.94), BigDecimal.valueOf(5159.28), LoanProvider.builder().name("1").build()),
                new LoanOffer(5000, 54, BigDecimal.valueOf(5.99), BigDecimal.ONE, BigDecimal.valueOf(105.49), BigDecimal.valueOf(5696.46), LoanProvider.builder().name("1").build()),
                new LoanOffer(5000, 60, BigDecimal.valueOf(5.99), BigDecimal.ONE, BigDecimal.valueOf(96.27), BigDecimal.valueOf(5776.2), LoanProvider.builder().name("1").build()),
                new LoanOffer(5000, 90, BigDecimal.valueOf(5.99), BigDecimal.ONE, BigDecimal.valueOf(68.72), BigDecimal.valueOf(6184.8), LoanProvider.builder().name("1").build()),
                new LoanOffer(5000, 96, BigDecimal.valueOf(5.99), BigDecimal.ONE, BigDecimal.valueOf(65.29), BigDecimal.valueOf(6267.84), LoanProvider.builder().name("1").build()),
                new LoanOffer(5000, 6, BigDecimal.valueOf(5.99), BigDecimal.ONE, BigDecimal.valueOf(847.56), BigDecimal.valueOf(5085.36), LoanProvider.builder().name("1").build()),
                new LoanOffer(5000, 30, BigDecimal.valueOf(5.99), BigDecimal.ONE, BigDecimal.valueOf(179.51), BigDecimal.valueOf(5385.3), LoanProvider.builder().name("1").build()),
                new LoanOffer(5000, 72, BigDecimal.valueOf(5.99), BigDecimal.ONE, BigDecimal.valueOf(82.46), BigDecimal.valueOf(5937.12), LoanProvider.builder().name("1").build()),
                new LoanOffer(5000, 84, BigDecimal.valueOf(5.99), BigDecimal.ONE, BigDecimal.valueOf(72.63), BigDecimal.valueOf(6100.92), LoanProvider.builder().name("1").build()),
                new LoanOffer(5000, 48, BigDecimal.valueOf(5.99), BigDecimal.ONE, BigDecimal.valueOf(117.03), BigDecimal.valueOf(5617.44), LoanProvider.builder().name("1").build()),
                new LoanOffer(5000, 78, BigDecimal.valueOf(5.99), BigDecimal.ONE, BigDecimal.valueOf(77.17), BigDecimal.valueOf(6019.26), LoanProvider.builder().name("1").build()),
                new LoanOffer(5000, 6, BigDecimal.valueOf(5.99), BigDecimal.ONE, BigDecimal.valueOf(847.56), BigDecimal.valueOf(5085.36), LoanProvider.builder().name("1").build()),
                new LoanOffer(5000, 72, BigDecimal.valueOf(5.99), BigDecimal.ONE, BigDecimal.valueOf(82.46), BigDecimal.valueOf(5937.12), LoanProvider.builder().name("1").build()),
                new LoanOffer(5000, 96, BigDecimal.valueOf(5.99), BigDecimal.ONE, BigDecimal.valueOf(65.29), BigDecimal.valueOf(6267.84), LoanProvider.builder().name("1").build()),
                new LoanOffer(5000, 18, BigDecimal.valueOf(5.99), BigDecimal.ONE, BigDecimal.valueOf(290.77), BigDecimal.valueOf(5233.86), LoanProvider.builder().name("1").build()),
                new LoanOffer(5000, 84, BigDecimal.valueOf(5.99), BigDecimal.ONE, BigDecimal.valueOf(72.63), BigDecimal.valueOf(6100.92), LoanProvider.builder().name("1").build()),
                new LoanOffer(5000, 36, BigDecimal.valueOf(5.99), BigDecimal.ONE, BigDecimal.valueOf(151.72), BigDecimal.valueOf(5461.92), LoanProvider.builder().name("1").build()),
                new LoanOffer(5000, 60, BigDecimal.valueOf(5.99), BigDecimal.ONE, BigDecimal.valueOf(96.27), BigDecimal.valueOf(5776.2), LoanProvider.builder().name("1").build()),
                new LoanOffer(5000, 48, BigDecimal.valueOf(5.99), BigDecimal.ONE, BigDecimal.valueOf(117.03), BigDecimal.valueOf(5617.44), LoanProvider.builder().name("1").build()),
                new LoanOffer(5000, 24, BigDecimal.valueOf(5.99), BigDecimal.ONE, BigDecimal.valueOf(221.22), BigDecimal.valueOf(5309.28), LoanProvider.builder().name("1").build()),
                new LoanOffer(5000, 12, BigDecimal.valueOf(5.99), BigDecimal.ONE, BigDecimal.valueOf(429.94), BigDecimal.valueOf(5159.28), LoanProvider.builder().name("1").build()),
                new LoanOffer(5000, 96, BigDecimal.valueOf(5.49), BigDecimal.ONE, BigDecimal.valueOf(64.15), BigDecimal.valueOf(6158.4), LoanProvider.builder().name("1").build()),
                new LoanOffer(5000, 18, BigDecimal.valueOf(5.49), BigDecimal.ONE, BigDecimal.valueOf(289.7), BigDecimal.valueOf(5214.6), LoanProvider.builder().name("1").build()),
                new LoanOffer(5000, 6, BigDecimal.valueOf(5.49), BigDecimal.ONE, BigDecimal.valueOf(846.4), BigDecimal.valueOf(5078.4), LoanProvider.builder().name("1").build()),
                new LoanOffer(5000, 84, BigDecimal.valueOf(5.49), BigDecimal.ONE, BigDecimal.valueOf(71.51), BigDecimal.valueOf(6006.84), LoanProvider.builder().name("1").build()),
                new LoanOffer(5000, 12, BigDecimal.valueOf(5.49), BigDecimal.ONE, BigDecimal.valueOf(428.85), BigDecimal.valueOf(5146.2), LoanProvider.builder().name("1").build()),
                new LoanOffer(5000, 48, BigDecimal.valueOf(5.49), BigDecimal.ONE, BigDecimal.valueOf(115.95), BigDecimal.valueOf(5565.6), LoanProvider.builder().name("1").build()),
                new LoanOffer(5000, 72, BigDecimal.valueOf(5.49), BigDecimal.ONE, BigDecimal.valueOf(81.35), BigDecimal.valueOf(5857.2), LoanProvider.builder().name("1").build()),
                new LoanOffer(5000, 36, BigDecimal.valueOf(5.49), BigDecimal.ONE, BigDecimal.valueOf(150.65), BigDecimal.valueOf(5423.4), LoanProvider.builder().name("1").build()),
                new LoanOffer(5000, 24, BigDecimal.valueOf(5.49), BigDecimal.ONE, BigDecimal.valueOf(220.15), BigDecimal.valueOf(5283.6), LoanProvider.builder().name("1").build()),
                new LoanOffer(5000, 60, BigDecimal.valueOf(5.49), BigDecimal.ONE, BigDecimal.valueOf(95.17), BigDecimal.valueOf(5710.2), LoanProvider.builder().name("1").build()),
                new LoanOffer(5000, 96, BigDecimal.valueOf(3.29), BigDecimal.ONE, BigDecimal.valueOf(59.2), BigDecimal.valueOf(5682.95), LoanProvider.builder().name("1").build()),
                new LoanOffer(5000, 48, BigDecimal.valueOf(4.19), BigDecimal.ONE, BigDecimal.valueOf(113.2), BigDecimal.valueOf(5430.75), LoanProvider.builder().name("1").build()),
                new LoanOffer(5000, 72, BigDecimal.valueOf(2.99), BigDecimal.ONE, BigDecimal.valueOf(75.9), BigDecimal.valueOf(5461.35), LoanProvider.builder().name("1").build()),
                new LoanOffer(5000, 60, BigDecimal.valueOf(2.99), BigDecimal.ONE, BigDecimal.valueOf(89.8), BigDecimal.valueOf(5383.61), LoanProvider.builder().name("1").build()),
                new LoanOffer(5000, 84, BigDecimal.valueOf(2.99), BigDecimal.ONE, BigDecimal.valueOf(66), BigDecimal.valueOf(5539.66), LoanProvider.builder().name("1").build()),
                new LoanOffer(5000, 120, BigDecimal.valueOf(3.29), BigDecimal.ONE, BigDecimal.valueOf(48.9), BigDecimal.valueOf(5859.44), LoanProvider.builder().name("1").build()),
                new LoanOffer(5000, 108, BigDecimal.valueOf(3.29), BigDecimal.ONE, BigDecimal.valueOf(53.5), BigDecimal.valueOf(5770.38), LoanProvider.builder().name("1").build()),
                new LoanOffer(5000, 36, BigDecimal.valueOf(4.55), BigDecimal.ONE, BigDecimal.valueOf(148.7), BigDecimal.valueOf(5350.91), LoanProvider.builder().name("1").build()),
                new LoanOffer(5000, 24, BigDecimal.valueOf(4.65), BigDecimal.ONE, BigDecimal.valueOf(218.4), BigDecimal.valueOf(5240.57), LoanProvider.builder().name("1").build())
        );
    }

    private List<LoanOffer> bonifyLoanOffers() {
        return List.of(
                new LoanOffer(5000, 60, BigDecimal.valueOf(2.97), BigDecimal.valueOf(0.0297), BigDecimal.valueOf(89.78), new BigDecimal("5386.80"), LoanProvider.builder().name("AION").build()),
                new LoanOffer(5000, 120, BigDecimal.valueOf(3.25), BigDecimal.valueOf(0.0325), BigDecimal.valueOf(48.87), new BigDecimal("5864.40"), LoanProvider.builder().name("AION").build()),
                new LoanOffer(5000, 6, BigDecimal.valueOf(5.47), BigDecimal.valueOf(0.0547), BigDecimal.valueOf(846.02), new BigDecimal("5076.12"), LoanProvider.builder().name("AION").build())
        );
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