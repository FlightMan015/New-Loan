package de.joonko.loan.integrations.domain.integrationhandler.fts;

import de.joonko.loan.avro.dto.dac.DacAccountSnapshot;
import de.joonko.loan.avro.dto.salary_account.QuerySalaryAccountResponse;
import reactor.core.publisher.Mono;

public interface UserTransactionalDataIngester {

    Mono<Void> handleQuerySalaryAccountResponseFromDS(final QuerySalaryAccountResponse querySalaryAccountResponse);

    Mono<Void> handleDacResponse(final DacAccountSnapshot dacAccountSnapshot);
}
