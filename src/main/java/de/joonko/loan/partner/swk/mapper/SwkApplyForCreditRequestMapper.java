package de.joonko.loan.partner.swk.mapper;


import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.partner.swk.stub.CreditApplicationServiceStub;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {SwkApplicationRequestMapper.class})
public interface SwkApplyForCreditRequestMapper {


    @Mapping(target = "request", source = ".")
    CreditApplicationServiceStub.ApplyForCredit toApplyForCredit(LoanDemand loanDemand);


}
