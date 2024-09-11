package de.joonko.loan.partner.fake;

import de.joonko.loan.offer.domain.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import javax.validation.constraints.NotNull;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FakeLoanDemandGatewayTest {

    private FakeLoanDemandGateway gateway;

    @BeforeEach
    void setUp() {
        gateway = new FakeLoanDemandGateway(null);
    }

    @Test
    void return_three_fake_offers() {
        List<LoanOffer> offers = gateway.callApi(
                        loanDemandAskingFor(1234),
                        UUID.randomUUID()
                                .toString())
                .block();

        assertThat(offers).hasSize(3);
    }

    @Test
    void return_fake_offers_with_asked_amount() {
        List<LoanOffer> offers = gateway.callApi(
                        loanDemandAskingFor(1234),
                        UUID.randomUUID()
                                .toString())
                .block();

        assertThat(offers).allMatch(offer -> offer.getAmount() == 1234);
    }

    @NotNull
    private LoanDemand loanDemandAskingFor(int loanAsked) {
        return new LoanDemand(
                UUID.randomUUID()
                        .toString(),
                loanAsked,
                "car",
                LoanDuration.TWENTY_FOUR,
                LoanCategory.FURNITURE_RENOVATION_MOVE,
                PersonalDetails.builder()
                        .build(),
                CreditDetails.builder().build(),
                EmploymentDetails.builder()
                        .build(),
                new ContactData("Düsseldorf", "Königsallee", "60 F", new ZipCode("40212"), LocalDate.of(2010, 01, 01), null, new Email("someOne@joonko.io"), "+491748273421011"),
                DigitalAccountStatements.builder()
                        .build(),
                null, null, null, null, null, List.of(), null);
    }

    @Test
    void offer() {
        LoanOffer loanOfferByNominalInterestRate = gateway.getLoanOfferByNominalInterestRate(50000, 5.99, 60);
        assertEquals(BigDecimal.valueOf(966.41), loanOfferByNominalInterestRate.getMonthlyRate()
                .setScale(2, BigDecimal.ROUND_UP));
        assertEquals(BigDecimal.valueOf(57984.46), loanOfferByNominalInterestRate.getTotalPayment()
                .setScale(2, BigDecimal.ROUND_UP));

    }

}
