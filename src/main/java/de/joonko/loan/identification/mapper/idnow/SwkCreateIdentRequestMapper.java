package de.joonko.loan.identification.mapper.idnow;

import de.joonko.loan.identification.config.IdentificationPropConfig;
import de.joonko.loan.identification.model.CreateIdentRequest;
import de.joonko.loan.partner.swk.SwkStoreService;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import lombok.extern.slf4j.Slf4j;

@Mapper(componentModel = "spring")
@Slf4j
public abstract class SwkCreateIdentRequestMapper {

    @Autowired
    private IdentificationPropConfig identificationPropConfig;

    @Autowired
    private SwkStoreService swkStoreService;

    @Mapping(target = "custom5", source = ".", qualifiedByName = "toCustomerNumber")
    @Mapping(target = "custom1", source = ".", qualifiedByName = "toCustom1")
    @Mapping(target = "custom3", source = "applicationId")
    public abstract de.joonko.loan.identification.model.idnow.CreateIdentRequest toIdNowCreateIdentRequest(CreateIdentRequest createIdentRequest);

    @Named("toCustom1")
    String toCustom1(CreateIdentRequest createIdentRequest) {
        if (identificationPropConfig.getAutoidentification()) {
            log.info("Setting Idnow HappyPath flow for Testing ");
            return "X-MANUALTEST-HAPPYPATH";
        }
        return null;
    }

    @Named("toCustomerNumber")
    String toCustomerNumber(CreateIdentRequest createIdentRequest) {
        return swkStoreService.getCustomerNumber(createIdentRequest.getApplicationId(), createIdentRequest.getDuration());
    }


}
