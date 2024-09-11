package de.joonko.loan.partner.solaris.mapper;

import de.joonko.loan.acceptoffer.domain.OfferRequest;
import de.joonko.loan.partner.solaris.model.SolarisAcceptOfferRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SolarisAcceptOfferRequestMapper {
    @Mapping(target = "loanAsked", source = "loanAsked")
    @Mapping(target = "duration", source = "duration")
    SolarisAcceptOfferRequest toSolarisRequest(OfferRequest offerRequest);
}
