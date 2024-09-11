package de.joonko.loan.integrations.domain.integrationhandler.fts.mapper;

import de.joonko.loan.avro.dto.salary_account.QuerySalaryAccountResponse;
import de.joonko.loan.integrations.domain.integrationhandler.fts.domain.UserTransactionalDraftDataStore;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {TransactionDraftMapper.class,AccountMapper.class, BalanceDomainMapper.class})
public interface SalaryAccountResponseToTransactionalDraftDataMapper {

    UserTransactionalDraftDataStore map(QuerySalaryAccountResponse querySalaryAccountResponse);
}
