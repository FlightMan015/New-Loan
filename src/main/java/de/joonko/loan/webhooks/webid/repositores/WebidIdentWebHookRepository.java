package de.joonko.loan.webhooks.webid.repositores;

import de.joonko.loan.webhooks.webid.model.request.Ident;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository("webidIdentWebHookRepository")
public interface WebidIdentWebHookRepository extends MongoRepository<Ident, String> {
}
