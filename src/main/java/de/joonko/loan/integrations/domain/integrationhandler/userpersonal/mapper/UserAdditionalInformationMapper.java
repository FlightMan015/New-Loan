package de.joonko.loan.integrations.domain.integrationhandler.userpersonal.mapper;

import de.joonko.loan.avro.dto.user_additional_information.UserAdditionalInformation;
import de.joonko.loan.offer.api.CustomDacPersonalDetailsMapper;
import de.joonko.loan.offer.api.model.UserPersonalDetails;

import de.joonko.loan.user.service.UserAdditionalInformationStore;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {CustomDacPersonalDetailsMapper.class})
public interface UserAdditionalInformationMapper {

    UserAdditionalInformationStore map(UserPersonalDetails userPersonalDetails);
    UserAdditionalInformationStore map(UserAdditionalInformation userAdditionalInformation);
}
