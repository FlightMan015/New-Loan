package de.joonko.loan.identification.service;

import de.joonko.loan.identification.exception.ExternalIdentIdNotFoundException;
import de.joonko.loan.identification.model.IdentificationLink;
import de.joonko.loan.identification.model.IdentificationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
public class IdentificationLinkService {
    private final IdentificationLinkRepository identificationLinkRepository;

    public Flux<IdentificationLink> deleteByApplicationId(final String applicationId) {
        return Mono.fromCallable(() -> identificationLinkRepository.deleteByApplicationId(applicationId))
                .flatMapIterable(list -> list)
                .subscribeOn(Schedulers.elastic());
    }

    public void add(String applicationId, String offerId, String loanProvider, IdentificationProvider identProvider, String externalIdentId, String kycUrl) {
        if (identificationLinkRepository.findByOfferIdAndKycUrl(offerId, kycUrl).isEmpty()) {
            IdentificationLink identificationAuditTrail = IdentificationLink.builder()
                    .applicationId(applicationId)
                    .offerId(offerId)
                    .loanProvider(loanProvider)
                    .externalIdentId(externalIdentId)
                    .identProvider(identProvider)
                    .kycUrl(kycUrl)
                    .build();
            identificationLinkRepository.save(identificationAuditTrail);
        }
    }

    public IdentificationLink getByExternalIdentId(String externalIdentId) {
        return identificationLinkRepository.findByExternalIdentIdOrderByInsertTsDesc(externalIdentId).stream().findFirst()
                .orElseThrow(() -> new ExternalIdentIdNotFoundException("Not able to find IdentificationLink for externalIdentId: " + externalIdentId));
    }

    public Mono<IdentificationLink> getIdentificationByExternalIdentId(String externalIdentId) {
        return Mono.fromCallable(() -> identificationLinkRepository.findByExternalIdentIdOrderByInsertTsDesc(externalIdentId).stream().findFirst()
                        .orElseThrow(() -> new ExternalIdentIdNotFoundException("Not able to find IdentificationLink for externalIdentId: " + externalIdentId)))
                .subscribeOn(Schedulers.elastic());
    }
}
