package de.joonko.loan.userdata.infrastructure.draft.mapper;

import de.joonko.loan.userdata.domain.model.UserData;
import de.joonko.loan.userdata.infrastructure.draft.model.UserDraftInformationStore;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserDraftInformationMapper {

    UserData toDomainModel(UserDraftInformationStore dbModel);

    @Mapping(target = "userUUID", source = "userUuid")
    UserDraftInformationStore toUserDraftStore(String userUuid, UserData domainModel);
}
