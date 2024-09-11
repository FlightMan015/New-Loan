package de.joonko.loan.integrations.domain.integrationhandler.fts;

import de.joonko.loan.avro.dto.salary_account.QuerySalaryAccountResponse;
import de.joonko.loan.data.support.mapper.MapstructBaseTest;
import de.joonko.loan.integrations.domain.integrationhandler.fts.mapper.*;
import io.github.glytching.junit.extension.random.Random;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.assertAll;


@ContextConfiguration(classes = {
        TransactionDraftMapperImpl.class, AccountMapperImpl.class, BalanceDomainMapperImpl.class,
        SalaryAccountResponseToTransactionalDraftDataMapperImpl.class})
class QuerySalaryResponseMapperTest extends MapstructBaseTest {

    @Autowired
    private SalaryAccountResponseToTransactionalDraftDataMapper dsResponseMapper;


    @Test
    void should_map_to_draft_data_store(@Random QuerySalaryAccountResponse querySalaryAccountResponse) {
        querySalaryAccountResponse.getBalance().setDate("2021-10-18 08:45:35.029");
        var transactionalDraftDataStore = dsResponseMapper.map(querySalaryAccountResponse);
        assertAll(
                () -> Assertions.assertThat(transactionalDraftDataStore).isNotNull(),
                () -> Assertions.assertThat(transactionalDraftDataStore.getBalance().getDate()).hasToString("2021-10-18"),
                () -> Assertions.assertThat(transactionalDraftDataStore.getBalance().getBalance()).isEqualTo( querySalaryAccountResponse.getBalance().getBalance()),
                () -> Assertions.assertThat(transactionalDraftDataStore.getTransactions().size()).isEqualTo(querySalaryAccountResponse.getTransactions().size()),
                () -> Assertions.assertThat(transactionalDraftDataStore.getUserUUID()).isEqualTo(querySalaryAccountResponse.getUserUUID()),
                () -> Assertions.assertThat(transactionalDraftDataStore.getAccountInternalId()).isEqualTo(querySalaryAccountResponse.getAccountInternalId()),
                () -> Assertions.assertThat(transactionalDraftDataStore.getAccount().getJointAccount()).isEqualTo(querySalaryAccountResponse.getAccount().getIsJointlyManaged())
                );
    }
}

