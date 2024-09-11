package de.joonko.loan.identification.service;

import de.joonko.loan.identification.model.IdentificationLink;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("IdentificationLinkRepository")
public interface IdentificationLinkRepository extends MongoRepository<IdentificationLink, String> {
    List<IdentificationLink> findByOfferIdAndKycUrl(String offerId, String kycUrl);

    List<IdentificationLink> findByExternalIdentIdOrderByInsertTsDesc(String externalIdentId);

    List<IdentificationLink> deleteByApplicationId(final String applicationId);
}