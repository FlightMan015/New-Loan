package de.joonko.loan.partner.swk.mapper;


import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.partner.swk.stub.CreditApplicationServiceStub;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class SwkCreditUserArrayMapper {

    @Autowired
    SwkCreditUsersRequestMapper mapper;

    public CreditApplicationServiceStub.CreditUser[] from(LoanDemand loanDemand) {
        CreditApplicationServiceStub.CreditUser[] creditUsers = new CreditApplicationServiceStub.CreditUser[1];
        CreditApplicationServiceStub.CreditUser creditUser = mapper.toCreditUsers(loanDemand);
        creditUsers[0] = creditUser;
        return creditUsers;
    }
}
