package de.joonko.loan.partner.creditPlus;

import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.partner.creditPlus.mapper.CreditPlusCreateCreditOfferDacEMapper;
import de.joonko.loan.partner.creditPlus.stub.EfinComparerServiceStub;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CreditPlusCreateCreditOfferDacEMapper.class})
public interface CreditPlusRequestMapper {

    @Mapping(target = "createCreditOfferDac", source = ".")
    EfinComparerServiceStub.CreateCreditOfferDacE toCreateCreditOfferDacE(LoanDemand loanDemand);

}
