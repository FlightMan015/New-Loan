package de.joonko.loan.partner.fake;

import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.common.domain.LoanProvider;
import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.offer.domain.LoanDemandGateway;
import de.joonko.loan.offer.domain.LoanDuration;
import de.joonko.loan.offer.domain.LoanOffer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(
        value = "fake.enabled",
        havingValue = "true"
)
public class FakeLoanDemandGateway implements LoanDemandGateway<FakeLoanProviderApiMapper, LoanDemand, List<LoanOffer>> {

    private static final LoanProvider PROVIDER = new LoanProvider(Bank.MOUNTAIN_BANK.label);

    private static List<LoanOffer> getFakeOffers(int amount) {

        return Arrays.asList(
                getLoanOfferByNominalInterestRate(amount, 6.99, 60),
                getLoanOfferByNominalInterestRate(amount, 5.99, 48),
                getLoanOfferByNominalInterestRate(amount, 3.81, 12)

        );
    }

    private final FakeLoanProviderApiMapper mapper;

    @Override
    public LoanProvider getLoanProvider() {
        return LoanProvider.builder().name("FakeLoanProvider").build();
    }

    @Override
    public FakeLoanProviderApiMapper getMapper() {
        return mapper;
    }

    @Override
    public Mono<List<LoanOffer>> callApi(LoanDemand loanDemand, String id) {
        log.info("Getting fake loan offers for {}", loanDemand);
        return Mono.just(getFakeOffers(loanDemand.getLoanAsked()));
    }

    @Override
    public Boolean filterGateway(LoanDemand loanDemand) {
        return false;
    }

    @Override
    public List<LoanDuration> getDurations(Integer loanAsked) {
        return List.of(LoanDuration.FORTY_EIGHT);
    }

    public static LoanOffer getLoanOfferByNominalInterestRate(int amount, Double interestRate, int months) {
        Double interestRatePerMonth = interestRate / 100 / 12;
        double pow = Math.pow((1 + interestRatePerMonth), months);
        double monthlyPayment = ((amount) * (interestRatePerMonth) * pow) / (pow - 1);
        double totalPayment = monthlyPayment * months;
        return new LoanOffer(amount, months,
                BigDecimal.valueOf(interestRate),
                BigDecimal.valueOf(interestRate),
                BigDecimal.valueOf(monthlyPayment),
                BigDecimal.valueOf(totalPayment),
                PROVIDER);

    }


}
