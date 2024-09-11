package de.joonko.loan.integrations.domain.integrationhandler.fts;

import de.joonko.loan.integrations.domain.integrationhandler.fts.model.FinleapToFtsTransactionalData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "dacFeignClient", url = "${microservices.dac-api.url}")
public interface DacFeignClient {

    @PostMapping(path = "/internal/finleap-to-fts")
    void finleapToFts(@RequestBody FinleapToFtsTransactionalData finleapToFtsTransactionalData);
}
