package de.joonko.loan.dac.fts;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface FTSWizardSessionDetailsRepository extends MongoRepository<FTSWizardSessionDetailsStore, String> {
}
