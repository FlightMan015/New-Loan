package de.joonko.loan.partner.santander.mapper;

import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.partner.santander.stub.ScbCapsBcoWSStub;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {SantanderGetOfferLoanDetailRequestMapper.class, SantanderGetOfferPersonalDetailRequestMapper.class})
public interface SantanderGetOfferKreditantragRequestMapper {
    @Mapping(target = "finanzierung", source = ".")
    @Mapping(target = "darlehnsnehmer", source = ".")
    @Mapping(target = "antragId", source = "loanApplicationId")
    ScbCapsBcoWSStub.KreditantragXO toKreditantrag(LoanDemand loanDemand);
}
