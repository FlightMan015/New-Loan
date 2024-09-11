package de.joonko.loan.partner.creditPlus.mapper;

import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.partner.creditPlus.stub.EfinComparerServiceStub;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CreditPlusCreateCreditOfferRequestMapper.class})
public interface CreditPlusCreateCreditOfferDacEMapper {

    @Mapping(target = "efinComparerCreditOffer", source = ".")
    EfinComparerServiceStub.CreateCreditOfferDac toCreateCreditOfferRequest(LoanDemand loanDemand);
}
