package de.joonko.loan.offer.api;

import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.common.domain.LoanProvider;
import de.joonko.loan.db.service.LoanOfferStoreService;
import de.joonko.loan.integrations.configuration.GetOffersConfigurations;
import de.joonko.loan.offer.domain.LoanDemandGateway;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;

import static java.util.stream.Collectors.toList;

@AllArgsConstructor
@Component
@Slf4j
public class LoanProvidersService {

    @Getter
    private final List<LoanDemandGateway> gateways;
    private final LoanOfferStoreService loanOfferStoreService;
    private final GetOffersConfigurations getOffersConfigurations;

    public List<String> getLoanOffersProviders(String applicationId) {
        return loanOfferStoreService.findAllByLoanApplicationId(applicationId).stream()
                .map(loanOfferStore -> loanOfferStore.getOffer().getLoanProvider().getName())
                .distinct()
                .collect(toList());
    }

    public Flux<String> getActiveLoanProviders() {
        return Flux.fromIterable(gateways)
                .map(g -> g.getLoanProvider().getName());
    }

    public List<String> getEnabledRecommendedLoanProviders() {
        final var recommendationEnabledBanks = getOffersConfigurations.getListOfLoanRecommendationsEnabledBanks().stream()
                .map(Bank::getLabel).collect(toList());
        return recommendationEnabledBanks;
    }

    public boolean isRecommendedEnabled(LoanProvider loanProvider) {
        return getEnabledRecommendedLoanProviders().contains(loanProvider.getName());
    }
}
