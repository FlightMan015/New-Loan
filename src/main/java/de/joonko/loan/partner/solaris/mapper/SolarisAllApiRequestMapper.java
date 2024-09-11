package de.joonko.loan.partner.solaris.mapper;

import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.partner.solaris.model.SolarisAllApiRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {SolarisGetOffersRequestMapper.class, SolarisCreatePersonRequestMapper.class})
public interface SolarisAllApiRequestMapper {

    @Mapping(target = "solarisGetOffersRequest" , source = ".")
    @Mapping(target = "solarisCreatePersonRequest" , source = ".")
    SolarisAllApiRequest toSolarisRequest(LoanDemand loanDemand);

}
