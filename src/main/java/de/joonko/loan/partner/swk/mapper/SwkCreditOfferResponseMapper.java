package de.joonko.loan.partner.swk.mapper;

import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.common.domain.LoanProvider;
import de.joonko.loan.offer.domain.LoanOffer;
import de.joonko.loan.partner.solaris.model.LoanStatus;
import de.joonko.loan.partner.swk.stub.CreditApplicationServiceStub;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface SwkCreditOfferResponseMapper {

    default List<LoanOffer> fromLoanProviderResponse(List<CreditApplicationServiceStub.CreditOffer> creditOffer) {
        List<LoanOffer> offers = creditOffer.stream()
                .map(this::toSingleOffer)
                .collect(Collectors.toList());
        return new ArrayList<>(offers.stream().collect(Collectors.toConcurrentMap(LoanOffer::getDurationInMonth, Function.identity(), (p, q) -> p)).values());
    }

    private LoanOffer toSingleOffer(CreditApplicationServiceStub.CreditOffer creditOffer) {
        return new LoanOffer(
                (int) creditOffer.getNetCreditAmount(),
                creditOffer.getDuration(),
                BigDecimal.valueOf(creditOffer.getEffectiveInterest()),
                BigDecimal.valueOf(creditOffer.getNominalInterest()),
                BigDecimal.valueOf(creditOffer.getFirstInstallmentAmount()),//TODO wrong mapping alert!!!
                BigDecimal.valueOf(creditOffer.getTotalCreditAmount()),
                new LoanProvider(Bank.SWK_BANK.label));
    }
}
