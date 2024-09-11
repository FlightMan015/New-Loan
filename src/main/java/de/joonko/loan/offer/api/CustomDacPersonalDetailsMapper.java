package de.joonko.loan.offer.api;

import de.joonko.loan.avro.dto.dac.PersonalDetails;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CustomDacPersonalDetailsMapper {

    @Mapping(target = "firstName", source = "forename")
    @Mapping(target = "lastName", source = "surname")
    CustomDacPersonalDetails toCustomDacPersonalDetails(PersonalDetails personalDetails);
}
