package de.joonko.loan.webhooks.aion.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository("AionWebHookRepository")
public interface AionWebHookRepository extends MongoRepository<AionWebhookStore, String> {
}
