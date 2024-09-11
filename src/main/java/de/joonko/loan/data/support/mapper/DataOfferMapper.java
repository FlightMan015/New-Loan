package de.joonko.loan.data.support.mapper;

import de.joonko.loan.data.support.model.DataLoanOffer;
import de.joonko.loan.offer.api.LoanOffer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DataOfferMapper {

    @Mapping(source = "loanOfferId", target = "loanOfferId")
    @Mapping(source = "loanOffer.amount", target = "amount")
    @Mapping(source = "loanOffer.durationInMonth", target = "durationInMonth")
    @Mapping(source = "loanOffer.effectiveInterestRate", target = "effectiveInterestRate")
    @Mapping(source = "loanOffer.nominalInterestRate", target = "nominalInterestRate")
    @Mapping(source = "loanOffer.monthlyRate", target = "monthlyRate")
    @Mapping(source = "loanOffer.totalPayment", target = "totalPayment")
    DataLoanOffer mapLoanOffer(String loanOfferId, LoanOffer loanOffer);
}
