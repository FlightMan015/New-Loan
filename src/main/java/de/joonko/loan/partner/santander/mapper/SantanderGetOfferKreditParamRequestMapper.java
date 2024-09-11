package de.joonko.loan.partner.santander.mapper;

import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.partner.santander.stub.ScbCapsBcoWSStub;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = SantanderGetOfferKreditantragRequestMapper.class)
public interface SantanderGetOfferKreditParamRequestMapper {
    @Mapping(target = "kreditantrag", source = ".")
    ScbCapsBcoWSStub.GetKreditvertragsangebotParams toKreditParamRequest(LoanDemand loanDemand);
}
