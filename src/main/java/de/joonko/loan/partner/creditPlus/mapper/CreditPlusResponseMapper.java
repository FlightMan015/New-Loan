package de.joonko.loan.partner.creditPlus.mapper;

import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.common.domain.LoanProvider;
import de.joonko.loan.offer.domain.LoanOffer;
import de.joonko.loan.partner.creditPlus.stub.EfinComparerServiceStub;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface CreditPlusResponseMapper {


    default List<LoanOffer> fromLoanProviderResponse(List<EfinComparerServiceStub.Contract> contracts) {
        return contracts.stream()
                .map(this::toSingleOffer)
                .collect(Collectors.toList());
    }

    private LoanOffer toSingleOffer(EfinComparerServiceStub.Contract contract) {
        return new LoanOffer(
                contract.getAmount().intValue()
                , contract.getDuration()
                , contract.getInterest()//TODO effective interest rate
                , contract.getNominalInterest()
                , contract.getRate()
                , contract.getFullAmount()
                , new LoanProvider(Bank.CREDIT_PLUS.label));
    }
}
