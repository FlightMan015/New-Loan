package de.joonko.loan.partner.solaris.mapper;

import de.joonko.loan.offer.domain.Expenses;
import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.partner.auxmoney.getoffers.BaseMapperTest;
import de.joonko.loan.partner.solaris.SolarisPropertiesConfig;
import de.joonko.loan.partner.solaris.model.SolarisGetOffersRequest;
import io.github.glytching.junit.extension.random.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SolarisGetOffersRequestMapperTest extends BaseMapperTest {

    @Autowired
    private SolarisGetOffersRequestMapper solarisGetOffersRequestMapper;

    @Autowired
    private SolarisPropertiesConfig solarisPropertiesConfig;

    @Test
    @DisplayName("Should map partnerReferenceNumber")
    void firstName(@Random LoanDemand loanDemand) {
        SolarisGetOffersRequest solarisGetOffersRequest = solarisGetOffersRequestMapper.toSolarisRequest(loanDemand);
        assertEquals(loanDemand.getLoanApplicationId(), solarisGetOffersRequest.getPartnerReferenceNumber());
    }

    @Nested
    class livingSituationAmount {
        @Test
        @DisplayName("Should map livingSituationAmount to 0 when tweaked")
        void with_tweak(@Random LoanDemand loanDemand) {
            solarisPropertiesConfig.setTweakSnapshot(true);
            SolarisGetOffersRequest solarisGetOffersRequest = solarisGetOffersRequestMapper.toSolarisRequest(loanDemand);
            assertEquals(0, solarisGetOffersRequest.getLivingSituationAmount().getValue());
        }

        @Test
        @DisplayName("Should map livingSituationAmount")
        void without_tweak(@Random LoanDemand loanDemand) {
            solarisPropertiesConfig.setTweakSnapshot(false);
            SolarisGetOffersRequest solarisGetOffersRequest = solarisGetOffersRequestMapper.toSolarisRequest(loanDemand);
            Expenses expenses = loanDemand.getPersonalDetails().getFinance().getExpenses();
            Integer expectedAmount = expenses.getAcknowledgedMortgagesInEuroCent() + expenses.getAcknowledgedRentInEuroCent();
            assertEquals(expectedAmount, solarisGetOffersRequest.getLivingSituationAmount().getValue());
        }
    }

    @Nested
    class existingCreditRepaymentExcludingMortgage {
        @Test
        @DisplayName("Should map to 0 when tweaked")
        void with_tweak(@Random LoanDemand loanDemand) {
            solarisPropertiesConfig.setTweakSnapshot(true);
            SolarisGetOffersRequest solarisGetOffersRequest = solarisGetOffersRequestMapper.toSolarisRequest(loanDemand);
            assertEquals(0, solarisGetOffersRequest.getExistingCreditRepaymentExcludingMortgage().getValue());
        }

        @Test
        @DisplayName("Should map existingCreditRepaymentExcludingMortgage")
        void without_tweak(@Random LoanDemand loanDemand) {
            solarisPropertiesConfig.setTweakSnapshot(false);
            SolarisGetOffersRequest solarisGetOffersRequest = solarisGetOffersRequestMapper.toSolarisRequest(loanDemand);
            Expenses expenses = loanDemand.getPersonalDetails().getFinance().getExpenses();
            Integer expectedAmount = expenses.getLoanInstalmentsInEuroCent();
            assertEquals(expectedAmount, solarisGetOffersRequest.getExistingCreditRepaymentExcludingMortgage().getValue());
        }
    }

    @Nested
    class maintenanceObligationsAmount {
        @Test
        @DisplayName("Should map to 0 when tweaked")
        void with_tweak(@Random LoanDemand loanDemand) {
            solarisPropertiesConfig.setTweakSnapshot(true);
            SolarisGetOffersRequest solarisGetOffersRequest = solarisGetOffersRequestMapper.toSolarisRequest(loanDemand);
            assertEquals(0, solarisGetOffersRequest.getMaintenanceObligationsAmount().getValue());
        }

        @Test
        @DisplayName("Should map existingCreditRepaymentExcludingMortgage")
        void without_tweak(@Random LoanDemand loanDemand) {
            solarisPropertiesConfig.setTweakSnapshot(false);
            SolarisGetOffersRequest solarisGetOffersRequest = solarisGetOffersRequestMapper.toSolarisRequest(loanDemand);
            Expenses expenses = loanDemand.getPersonalDetails().getFinance().getExpenses();
            Integer expectedAmount = expenses.getAlimonyInEuroCent();
            assertEquals(expectedAmount, solarisGetOffersRequest.getMaintenanceObligationsAmount().getValue());
        }
    }

    @Nested
    class privateInsuranceAmount {
        @Test
        @DisplayName("Should map to 0 when tweaked")
        void with_tweak(@Random LoanDemand loanDemand) {
            solarisPropertiesConfig.setTweakSnapshot(true);
            SolarisGetOffersRequest solarisGetOffersRequest = solarisGetOffersRequestMapper.toSolarisRequest(loanDemand);
            assertEquals(0, solarisGetOffersRequest.getPrivateInsuranceAmount().getValue());
        }

        @Test
        @DisplayName("Should map existingCreditRepaymentExcludingMortgage")
        void without_tweak(@Random LoanDemand loanDemand) {
            solarisPropertiesConfig.setTweakSnapshot(false);
            SolarisGetOffersRequest solarisGetOffersRequest = solarisGetOffersRequestMapper.toSolarisRequest(loanDemand);
            Expenses expenses = loanDemand.getPersonalDetails().getFinance().getExpenses();
            Integer expectedAmount = expenses.getPrivateHealthInsuranceInEuroCent();
            assertEquals(expectedAmount, solarisGetOffersRequest.getPrivateInsuranceAmount().getValue());
        }
    }

    @Nested
    class recipientIban {
        @Test
        @DisplayName("Should map to DE92370601930002130041 when tweaked")
        void with_tweak(@Random LoanDemand loanDemand) {
            solarisPropertiesConfig.setTweakSnapshot(true);
            SolarisGetOffersRequest solarisGetOffersRequest = solarisGetOffersRequestMapper.toSolarisRequest(loanDemand);
            assertEquals("DE92370601930002130041", solarisGetOffersRequest.getRecipientIban());
        }

        @Test
        @DisplayName("Should map recipientIban")
        void without_tweak(@Random LoanDemand loanDemand) {
            solarisPropertiesConfig.setTweakSnapshot(false);
            SolarisGetOffersRequest solarisGetOffersRequest = solarisGetOffersRequestMapper.toSolarisRequest(loanDemand);
            String expectedIban = loanDemand.getDigitalAccountStatements().getIban();
            assertEquals(expectedIban, solarisGetOffersRequest.getRecipientIban());
        }
    }
}
