package de.joonko.loan.partner.auxmoney;

import de.joonko.loan.partner.auxmoney.model.AuxmoneySingleCallResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuxmoneySingleOfferCallResponseStoreService {

    private final AuxmoneySingleOfferCallResponseStoreRepository auxmoneySingleOfferCallResponseStoreRepository;

    public AuxmoneySingleOfferCallResponseStore saveSingleOfferResponse(AuxmoneySingleCallResponse auxmoneySingleCallResponse, String applicationId) {
        log.info("Saving Auxmoney Offer Response for applicationId {}  ", applicationId);
        AuxmoneySingleOfferCallResponseStore auxmoneySingleOfferCallResponseStore = AuxmoneySingleOfferCallResponseStore.builder()
                .loanApplicationId(applicationId)
                .auxmoneySingleCallResponse(auxmoneySingleCallResponse)
                .build();
        return auxmoneySingleOfferCallResponseStoreRepository.save(auxmoneySingleOfferCallResponseStore);
    }

    public AuxmoneySingleOfferCallResponseStore getAuxmoneySingleOfferCallResponseStoreByLoanApplicationId(String applicationId) {
        return auxmoneySingleOfferCallResponseStoreRepository
                .findByLoanApplicationId(applicationId)
                .orElseThrow(() -> new RuntimeException("applicationId not found from Auxmoney Single Offer Call Response Store:  " + applicationId));
    }
}
