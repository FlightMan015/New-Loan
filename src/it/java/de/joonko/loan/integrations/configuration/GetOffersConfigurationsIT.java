package de.joonko.loan.integrations.configuration;

import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.integrations.configuration.GetOffersConfigurations;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("integration")
@ExtendWith({MockitoExtension.class})
@SpringBootTest
public class GetOffersConfigurationsIT {

    @Autowired
    private GetOffersConfigurations getOffersConfigurations;

    @Test
    void minimalLoanAmount_is_fetched_correctly() {
        final var minLoanAmount = getOffersConfigurations.getMinimalLoanAmount();

        assertEquals(1000, minLoanAmount);
    }

    @Test
    void recommendedLoanPercentage_is_fetched_correctly() {
        final var recommendedLoanPercentage = getOffersConfigurations.getRecommendedLoanPercentage();

        assertEquals(50, recommendedLoanPercentage);
    }

    @Test
    void recommendation_properties_are_fetched_correctly() {
        final var loanRecommendationsEnabled = getOffersConfigurations.getLoanRecommendationsEnabled();
        final var loanRecommendationsEnabledBanks = getOffersConfigurations.getListOfLoanRecommendationsEnabledBanks();

        assertEquals(true, loanRecommendationsEnabled);
        assertEquals(List.of(Bank.SWK_BANK, Bank.SANTANDER, Bank.CONSORS), loanRecommendationsEnabledBanks);
    }
}
