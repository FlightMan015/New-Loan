package de.joonko.loan.partner.santander;

import de.joonko.loan.partner.santander.model.GetKreditvertragsangebotResponse;
import de.joonko.loan.partner.santander.model.SantanderOffer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SantanderStoreService {
    private final SantanderOfferRepository santanderOfferRepository;

    public void saveOffer(SantanderOffer kreditOffer) {
        log.info("Saving offer in Santander store for loanApplication Id  {} ", kreditOffer.getApplicationId());
        santanderOfferRepository.save(kreditOffer);
    }

    public String getScbAntragId(String applicationId, Integer duration) {
        List<SantanderOffer> kreditOffer = santanderOfferRepository.findByApplicationId(applicationId);

        GetKreditvertragsangebotResponse santanderOfferObject = kreditOffer.stream().filter(offer ->
                (offer.getKreditOffer().getDuration() == duration)).findFirst()
                .orElseThrow(() -> new RuntimeException("Santander : Error in retrieving ScbAntragId "))
                .getKreditOffer();
        return santanderOfferObject.getScbAntragId();
    }

    public Flux<SantanderOffer> deleteByApplicationId(final String applicationId) {
        return Mono.fromCallable(() -> santanderOfferRepository.deleteByApplicationId(applicationId))
                .flatMapIterable(list -> list)
                .subscribeOn(Schedulers.elastic());
    }
}
