package de.joonko.loan.partner.solaris.mapper;

import de.joonko.loan.acceptoffer.domain.OfferStatus;
import de.joonko.loan.partner.solaris.model.SolarisAcceptOfferResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", uses = SolarisLoanStatusMapper.class,nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SolarisAcceptOfferResponseMapper {

    @Mapping(target = "kycUrl", source = "url")
    OfferStatus fromSolarisResponse(SolarisAcceptOfferResponse solarisAcceptOfferResponse);
}
