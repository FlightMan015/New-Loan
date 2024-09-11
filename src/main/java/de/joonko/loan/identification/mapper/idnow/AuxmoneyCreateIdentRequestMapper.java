package de.joonko.loan.identification.mapper.idnow;


import de.joonko.loan.identification.config.IdentificationPropConfig;
import de.joonko.loan.identification.model.CreateIdentRequest;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
@Slf4j
public abstract class AuxmoneyCreateIdentRequestMapper {
    @Autowired
    private IdentificationPropConfig identificationPropConfig;

    @Mapping(target = "custom1", source = ".", qualifiedByName = "toCustom1")
    public abstract de.joonko.loan.identification.model.idnow.CreateIdentRequest toIdNowCreateIdentRequest(CreateIdentRequest createIdentRequest);

    @Named("toCustom1")
    String toCustom1(CreateIdentRequest createIdentRequest) {
        if (identificationPropConfig.getAutoidentification()) {
            log.info("Setting Idnow HappyPath flow for Testing ");
            return "X-MANUALTEST-HAPPYPATH";
        }
        return null;
    }
}
