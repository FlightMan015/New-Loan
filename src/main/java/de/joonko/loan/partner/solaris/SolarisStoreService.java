package de.joonko.loan.partner.solaris;

import de.joonko.loan.partner.solaris.model.SolarisGetOffersResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SolarisStoreService {

    private final SolarisGetOfferResponseStoreRepository solarisGetOfferResponseStoreRepository;

    private final SolarisAcceptOfferResponseStoreRepository solarisAcceptOfferResponseStoreRepository;

    public SolarisGetOfferResponseStore saveGetOfferResponse(SolarisGetOffersResponse solarisGetOffersResponse, String applicationId) {
        log.info("Saving Solaris Offer Response for {} " + applicationId);
        SolarisGetOfferResponseStore solarisGetOfferResponseStore = SolarisGetOfferResponseStore.builder()
                .solarisGetOffersResponse(solarisGetOffersResponse)
                .applicationId(applicationId)
                .build();
        return solarisGetOfferResponseStoreRepository.save(solarisGetOfferResponseStore);
    }

    public List<SolarisGetOfferResponseStore> getSolarisGetOfferResponseStoreByLoanApplicationId(String applicationId) {
        return solarisGetOfferResponseStoreRepository.findByApplicationId(applicationId);
    }

    public SolarisAcceptOfferResponseStore saveAcceptOfferResponse(String identificationId, String applicationId, String personId, String signingId) {
        log.info("Saving Solaris Accept Offer Response for {} and signing Id {} " + identificationId, signingId);
        SolarisAcceptOfferResponseStore solarisAcceptOfferResponseStore = SolarisAcceptOfferResponseStore.builder()
                .applicationId(applicationId)
                .identificationId(identificationId)
                .personId(personId)
                .signingId(signingId)
                .build();
        return solarisAcceptOfferResponseStoreRepository.save(solarisAcceptOfferResponseStore);
    }

    public List<SolarisAcceptOfferResponseStore> getAcceptOfferResponseStoreByIdentificationId(String identificationId) {
        return solarisAcceptOfferResponseStoreRepository.findByIdentificationId(identificationId);
    }

    public List<SolarisAcceptOfferResponseStore> getAcceptOfferResponseStoreByApplicationId(String applicationId) {
        return solarisAcceptOfferResponseStoreRepository.findByApplicationId(applicationId);
    }

    public boolean updateConsent(String applicationId, Boolean consent) {
        List<SolarisAcceptOfferResponseStore> solarisAcceptOffers = solarisAcceptOfferResponseStoreRepository.findByApplicationId(applicationId);
        solarisAcceptOffers.stream().forEach(offer -> offer.setConsentAccepted(consent));
        return solarisAcceptOfferResponseStoreRepository.saveAll(solarisAcceptOffers).size() > 0;
    }
}
