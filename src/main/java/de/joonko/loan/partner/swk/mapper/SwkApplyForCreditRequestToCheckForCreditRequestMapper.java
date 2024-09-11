package de.joonko.loan.partner.swk.mapper;

import de.joonko.loan.partner.swk.stub.CreditApplicationServiceStub;
import de.joonko.loan.partner.swk.stub.PreCheckServiceStub;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SwkApplyForCreditRequestToCheckForCreditRequestMapper {

   PreCheckServiceStub.CheckForCredit toCheckForCreditRequest(CreditApplicationServiceStub.ApplyForCredit applyForCredit);
}
