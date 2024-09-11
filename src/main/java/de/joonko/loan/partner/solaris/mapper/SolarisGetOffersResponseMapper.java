package de.joonko.loan.partner.solaris.mapper;

import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.common.domain.LoanProvider;
import de.joonko.loan.offer.domain.LoanOffer;
import de.joonko.loan.partner.solaris.model.LoanStatus;
import de.joonko.loan.partner.solaris.model.Offer;
import de.joonko.loan.partner.solaris.model.SolarisGetOffersResponse;
import org.mapstruct.Mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface SolarisGetOffersResponseMapper {

    default List<LoanOffer> fromLoanProviderResponse(List<SolarisGetOffersResponse> solarisGetOffersResponse) {
        return solarisGetOffersResponse.stream()
                .filter(response -> response.getLoanDecision().equalsIgnoreCase(LoanStatus.APPROVED.getStatus()))
                .map(this::toSingleOffer)
                .collect(Collectors.toList());
    }

    private LoanOffer toSingleOffer(SolarisGetOffersResponse solarisGetOffersResponse) {

        Offer offer = solarisGetOffersResponse.getOffer();
        return new LoanOffer(Math.toIntExact(offer.getLoanAmount()
                .getValue() / 100),
                Math.toIntExact(offer.getLoanTerm()),
                BigDecimal.valueOf(offer.getEffectiveInterestRate() * 100),
                BigDecimal.valueOf(offer.getIntertestRate() * 100),
                BigDecimal.valueOf(offer.getMonthlyInstallment()
                        .getValue() / 100.00),
                BigDecimal.valueOf(offer.getApproximateTotalLoanExpenses()
                        .getValue() / 100.00),
                new LoanProvider(Bank.DEUTSCHE_FINANZ_SOZIETÄT.label));
    }
}
