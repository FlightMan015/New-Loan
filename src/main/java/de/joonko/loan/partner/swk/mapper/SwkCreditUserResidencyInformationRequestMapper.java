package de.joonko.loan.partner.swk.mapper;

import de.joonko.loan.offer.domain.PersonalDetails;
import de.joonko.loan.partner.swk.SwkDefaults;
import de.joonko.loan.partner.swk.stub.CreditApplicationServiceStub;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SwkCreditUserResidencyInformationRequestMapper {

    @Mapping(target = "citizenship", source = "nationality.countryCode.alpha2")
    @Mapping(target = "limitedResidencePermit", constant = SwkDefaults.SWK_FALSE)
    @Mapping(target = "residencePermitUntilDate", ignore = true)
    CreditApplicationServiceStub.ResidencyInformation toResidency(PersonalDetails personalDetails);

}
