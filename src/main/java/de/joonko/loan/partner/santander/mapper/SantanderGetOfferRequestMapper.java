package de.joonko.loan.partner.santander.mapper;

import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.partner.santander.stub.ScbCapsBcoWSStub;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {SantanderGetOfferKreditParamRequestMapper.class})
public interface SantanderGetOfferRequestMapper {
    @Mapping(target = "getKreditvertragsangebot", source = ".")
    ScbCapsBcoWSStub.GetKreditvertragsangebot toSantanderGetOfferRequest(LoanDemand loanDemand);
}
