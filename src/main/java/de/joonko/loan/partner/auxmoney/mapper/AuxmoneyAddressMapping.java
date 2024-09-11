package de.joonko.loan.partner.auxmoney.mapper;

import de.joonko.loan.offer.domain.Gender;
import de.joonko.loan.partner.auxmoney.model.Salutation;
import org.mapstruct.Mapper;
import org.mapstruct.ValueMapping;
import org.mapstruct.ValueMappings;

@Mapper(componentModel = "spring")
public interface AuxmoneyAddressMapping {

    @ValueMappings({
            @ValueMapping(source = "FEMALE", target = "FRAU"),
            @ValueMapping(source = "MALE", target = "HERR")
    })
    Salutation toAuxmoneySalutation(Gender gender);
}
