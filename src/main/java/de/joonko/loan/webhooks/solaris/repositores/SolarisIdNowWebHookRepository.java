package de.joonko.loan.webhooks.solaris.repositores;

import de.joonko.loan.webhooks.solaris.model.SolarisIdNowWebhookRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository("SolarisIdNowWebHookRepository")
public interface SolarisIdNowWebHookRepository  extends MongoRepository<SolarisIdNowWebhookRequest, String> {
}
