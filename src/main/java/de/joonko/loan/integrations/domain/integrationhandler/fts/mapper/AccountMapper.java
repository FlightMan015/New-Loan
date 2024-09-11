package de.joonko.loan.integrations.domain.integrationhandler.fts.mapper;

import de.joonko.loan.acceptoffer.domain.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    @Mapping(source = "isJointlyManaged", target = "jointAccount")
    Account map(de.joonko.loan.avro.dto.salary_account.Account account);

    @Mapping(source = "jointAccount", target = "isJointlyManaged")
    de.joonko.loan.acceptoffer.api.Account map(Account draftDataStore);

}
