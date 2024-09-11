package de.joonko.loan.integrations.domain.integrationhandler.fts.mapper;

import de.joonko.loan.integrations.domain.integrationhandler.fts.domain.UserTransactionalDraftDataStore;
import de.joonko.loan.integrations.domain.integrationhandler.fts.model.FinleapToFtsTransactionalData;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring",uses = {AccountMapper.class})
public interface TransactionalDraftDataToFtsRequestMapper {

    FinleapToFtsTransactionalData map(UserTransactionalDraftDataStore querySalaryAccountResponse);
}
