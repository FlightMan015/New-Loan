package de.joonko.loan.identification.mapper.idnow;

import de.joonko.loan.identification.config.IdentificationPropConfig;
import de.joonko.loan.identification.model.CreateIdentRequest;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import lombok.extern.slf4j.Slf4j;

@Mapper(componentModel = "spring")
@Slf4j
public abstract class AionCreateIdentRequestMapper {

    @Autowired
    public IdentificationPropConfig identificationPropConfig;
    
    @Mapping(target = "custom1", source = "representativeId")
    @Mapping(target = "custom2", source = "createIdentRequest", qualifiedByName = "toCustom2")
    public abstract de.joonko.loan.identification.model.idnow.CreateIdentRequest toIdNowCreateIdentRequest(CreateIdentRequest createIdentRequest, String representativeId);

    @Named("toCustom2")
    String toCustom2(CreateIdentRequest createIdentRequest) {
        log.info(String.format("AION: Identification config value is: %s", identificationPropConfig.getAutoidentification()));
        if (identificationPropConfig.getAutoidentification()) {
            log.info("AION: Setting Idnow HappyPath flow for Testing ");
            return "X-MANUALTEST-HAPPYPATH";
        }
        return null;
    }

}
