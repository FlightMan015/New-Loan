package de.joonko.loan.dac.fts;


import de.joonko.loan.dac.fts.api.WizardSessionDetails;
import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("integration")
@SpringBootTest
@ExtendWith(RandomBeansExtension.class)
class FTSWizardSessionDetailsServiceTest {

    @Autowired
    private FTSWizardSessionDetailsService ftsWizardSessionDetailsService;

    @Autowired
    private FTSWizardSessionDetailsRepository ftsWizardSessionDetailsRepository;


    @Test
    public void saveFTSWizardSessionDetails(@Random WizardSessionDetails wizardSessionDetails) {
        ftsWizardSessionDetailsRepository.deleteAll();

        FTSWizardSessionDetailsStore ftsWizardSessionDetailsStore = ftsWizardSessionDetailsService.saveWizardSessionDetails(FTSWizardSessionDetailsStore.builder()
                .wizardSessionKey(wizardSessionDetails.getWizardSessionKey())
                .bankCode(wizardSessionDetails.getBankCode())
                .bankName(wizardSessionDetails.getBankName())
                .transactionId(wizardSessionDetails.getTransactionId())
                .status(wizardSessionDetails.getStatus())
                .build());
        Assert.assertNotNull(ftsWizardSessionDetailsStore);
        Assert.assertEquals(wizardSessionDetails.getWizardSessionKey(), ftsWizardSessionDetailsStore.getWizardSessionKey());
        Assert.assertEquals(wizardSessionDetails.getBankCode(), ftsWizardSessionDetailsStore.getBankCode());
        Assert.assertEquals(wizardSessionDetails.getBankName(), ftsWizardSessionDetailsStore.getBankName());
        Assert.assertEquals(wizardSessionDetails.getStatus(), ftsWizardSessionDetailsStore.getStatus());
    }



}