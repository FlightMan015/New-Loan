package de.joonko.loan.partner.auxmoney;

import de.joonko.loan.partner.auxmoney.model.AuxmoneySingleCallResponse;
import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

@ActiveProfiles("integration")
@SpringBootTest
@ExtendWith(RandomBeansExtension.class)
class AuxmoneySingleOfferCallResponseStoreServiceTest {

    @Autowired
    private AuxmoneySingleOfferCallResponseStoreService auxmoneySingleOfferCallResponseStoreService;
    @Autowired
    private AuxmoneySingleOfferCallResponseStoreRepository auxmoneySingleOfferCallResponseStoreRepository;

    @Test
    void shouldSaveResponse(@Random AuxmoneySingleCallResponse auxmoneySingleCallResponse) {
        auxmoneySingleOfferCallResponseStoreRepository.deleteAll();
        AuxmoneySingleOfferCallResponseStore auxmoneySingleOfferCallResponseStore = auxmoneySingleOfferCallResponseStoreService.saveSingleOfferResponse(auxmoneySingleCallResponse, "123456");
        Optional<AuxmoneySingleOfferCallResponseStore> fromDatabase = auxmoneySingleOfferCallResponseStoreRepository.findById(auxmoneySingleOfferCallResponseStore.getAuxmoneySingleOfferCallResponseId());
        Assert.assertNotNull(fromDatabase.get());
        Assert.assertNotNull(fromDatabase.get()
                .getLoanApplicationId(), "123456");
    }

    @Test
    void shouldReturnAuxmoneySingleResponseByLoanApplicationId(@Random AuxmoneySingleCallResponse auxmoneySingleCallResponse) {
        auxmoneySingleCallResponse.setUserId("78910");
        auxmoneySingleCallResponse.setCreditId("123456");
        auxmoneySingleOfferCallResponseStoreRepository.deleteAll();
        AuxmoneySingleOfferCallResponseStore auxmoneySingleOfferCallResponseStore = auxmoneySingleOfferCallResponseStoreService.saveSingleOfferResponse(auxmoneySingleCallResponse, "123456");
        AuxmoneySingleOfferCallResponseStore byLoanApplicationId = auxmoneySingleOfferCallResponseStoreRepository.findByLoanApplicationId(auxmoneySingleOfferCallResponseStore.getLoanApplicationId()).get();
        Assert.assertNotNull(byLoanApplicationId);
        Assert.assertNotNull(byLoanApplicationId.getAuxmoneySingleCallResponse()
                .getCreditId(), "123456");
        Assert.assertNotNull(byLoanApplicationId.getAuxmoneySingleCallResponse()
                .getUserId(), "78910");
    }

}
