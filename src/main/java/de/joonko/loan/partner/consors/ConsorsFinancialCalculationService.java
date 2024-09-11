package de.joonko.loan.partner.consors;

import de.joonko.loan.offer.api.LoanDemandRequest;
import de.joonko.loan.partner.consors.model.FinancialCalculation;
import de.joonko.loan.partner.consors.model.PersonalizedCalculationsResponse;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;

import java.util.List;

import static java.util.stream.Collectors.toList;

@AllArgsConstructor
@Component
public class ConsorsFinancialCalculationService {

    private final FinancialCalculationsFilter financialCalculationsFilter;

    public Mono<PersonalizedCalculationsResponse> removeNotValidOffers(@NotNull PersonalizedCalculationsResponse personalizedCalculationsResponse, final LoanDemandRequest loanDemandRequest) {
        var financialCalculations = personalizedCalculationsResponse.getFinancialCalculations();

        List<FinancialCalculation> offers = financialCalculations.getFinancialCalculation();
        if (offers != null) {
            offers = offers.stream()
                    .filter(financialCalculationsFilter)
                    .filter(offer -> offer.getCreditAmount().equals(loanDemandRequest.getLoanAsked()))
                    .sorted(financialCalculationsFilter::compare)
                    .collect(toList());

            financialCalculations.setFinancialCalculation(offers);
        }

        return Mono.just(personalizedCalculationsResponse);
    }
}
