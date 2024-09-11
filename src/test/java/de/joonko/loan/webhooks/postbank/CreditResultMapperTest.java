package de.joonko.loan.webhooks.postbank;

import de.joonko.loan.webhooks.postbank.model.CreditResultWithContracts;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDateTime;

import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(RandomBeansExtension.class)
class CreditResultMapperTest {

    private CreditResultMapper creditResultMapper;

    @BeforeEach
    void setUp() {
        creditResultMapper = new CreditResultMapperImpl();
    }

    @Test
    void toCreditResult(@Random CreditResultWithContracts creditResultWithContracts) {
        final var now = LocalDateTime.now();

        final var creditResult = creditResultMapper.toCreditResult(creditResultWithContracts);

        assertAll(
                () -> assertNotNull(creditResult.getReceivedTimeStamp()),

                () -> assertEquals(creditResultWithContracts.getContractNumber(), creditResult.getContractNumber()),
                () -> assertEquals(creditResultWithContracts.getContractState(), creditResult.getContractState()),
                () -> assertEquals(creditResultWithContracts.getPartnerContractNumber(), creditResult.getPartnerContractNumber()),
                () -> assertEquals(creditResultWithContracts.getAlternativeOffer(), creditResult.getAlternativeOffer()),
                () -> assertEquals(creditResultWithContracts.getDateOfFirstRate(), creditResult.getDateOfFirstRate()),
                () -> assertEquals(creditResultWithContracts.getDebtorInformation(), creditResult.getDebtorInformation()),
                () -> assertEquals(creditResultWithContracts.getDecisionText(), creditResult.getDecisionText()),
                () -> assertEquals(creditResultWithContracts.getDuration(), creditResult.getDuration()),
                () -> assertEquals(creditResultWithContracts.getDateOfLastRate(), creditResult.getDateOfLastRate()),
                () -> assertEquals(creditResultWithContracts.getEffectiveInterest(), creditResult.getEffectiveInterest()),
                () -> assertEquals(creditResultWithContracts.getFreeIncome(), creditResult.getFreeIncome()),
                () -> assertEquals(creditResultWithContracts.getSchufaInformations(), creditResult.getSchufaInformations()),
                () -> assertEquals(creditResultWithContracts.getScore(), creditResult.getScore()),
                () -> assertEquals(creditResultWithContracts.getInterestRate(), creditResult.getInterestRate()),
                () -> assertEquals(creditResultWithContracts.getInsurance(), creditResult.getInsurance()),
                () -> assertEquals(creditResultWithContracts.getLoanAmount(), creditResult.getLoanAmount()),
                () -> assertEquals(creditResultWithContracts.getLastRate(), creditResult.getLastRate()),
                () -> assertEquals(creditResultWithContracts.getLoanAmountTotal(), creditResult.getLoanAmountTotal()),
                () -> assertEquals(creditResultWithContracts.getMonthlyRate(), creditResult.getMonthlyRate()),
                () -> assertEquals(creditResultWithContracts.getNominalInterest(), creditResult.getNominalInterest()),
                () -> assertEquals(creditResultWithContracts.getRapClass(), creditResult.getRapClass()),
                () -> assertEquals(creditResultWithContracts.getResidualDebtAmount(), creditResult.getResidualDebtAmount())
        );
    }

}