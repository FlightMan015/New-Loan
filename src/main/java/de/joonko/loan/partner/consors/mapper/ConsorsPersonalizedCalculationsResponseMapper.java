package de.joonko.loan.partner.consors.mapper;

import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.common.domain.LoanProvider;
import de.joonko.loan.offer.domain.LoanOffer;
import de.joonko.loan.partner.consors.model.PersonalizedCalculationsResponse;
import org.mapstruct.Mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ConsorsPersonalizedCalculationsResponseMapper {

    default List<LoanOffer> fromLoanProviderResponse(PersonalizedCalculationsResponse response) {

        if (null == response || null == response.getFinancialCalculations() || null == response.getFinancialCalculations()
                .getFinancialCalculation()) {
            return List.of();
        }
        return response.getFinancialCalculations()
                .getFinancialCalculation()
                .stream()
                .map(consorsOffer -> new LoanOffer(Math.toIntExact(consorsOffer.getCreditAmount()),
                        Math.toIntExact(consorsOffer.getDuration()),
                        BigDecimal.valueOf(consorsOffer.getEffectiveRate()),
                        BigDecimal.valueOf(consorsOffer.getNominalRate()),
                        BigDecimal.valueOf(consorsOffer.getMonthlyRate()),
                        BigDecimal.valueOf(consorsOffer.getTotalPayment()),
                        new LoanProvider(Bank.CONSORS.label)))
                .collect(Collectors.toList());


    }
}
