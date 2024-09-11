package de.joonko.loan.identification.mapper.idnow;


import de.joonko.loan.identification.config.IdentificationPropConfig;
import de.joonko.loan.identification.model.CreateIdentRequest;
import de.joonko.loan.partner.consors.ConsorsPropertiesConfig;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.Date;

@Mapper(componentModel = "spring")
@Slf4j
public abstract class ConsorsCreateIdentRequestMapper {

    @Autowired
    public IdentificationPropConfig identificationPropConfig;

    @Autowired
    public ConsorsPropertiesConfig consorsPropertiesConfig;

    @Named("toDate")
    static String toDate(CreateIdentRequest createIdentRequest) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        return format.format(new Date());
    }

    @Mapping(target = "preferredLang", source = "language")
    @Mapping(target = "custom1", source = ".", qualifiedByName = "toCustom1")
    @Mapping(target = "custom2", constant = "100")
    @Mapping(target = "custom3", source = ".", qualifiedByName = "toDate")
    @Mapping(target = "custom4", constant = "181")
    @Mapping(target = "custom5", constant = "0")
    public abstract de.joonko.loan.identification.model.idnow.CreateIdentRequest toIdNowCreateIdentRequest(CreateIdentRequest createIdentRequest);

    @Named("toCustom1")
    String toCustom1(CreateIdentRequest createIdentRequest) {
        if (identificationPropConfig.getAutoidentification()) {
            log.info("Setting Idnow HappyPath flow for Testing ");
            return "X-MANUALTEST-HAPPYPATH";
        }
        return consorsPropertiesConfig.getPartnerId();
    }
}
