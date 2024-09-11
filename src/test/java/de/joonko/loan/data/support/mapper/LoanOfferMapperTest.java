package de.joonko.loan.data.support.mapper;

import de.joonko.loan.avro.dto.loan_offers.LoanOffersMessage;
import de.joonko.loan.db.vo.LoanOfferStore;

import de.joonko.loan.integrations.model.OfferRequest;
import de.joonko.loan.integrations.model.OfferUpdateType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.time.Instant;
import java.util.List;

import io.github.glytching.junit.extension.random.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@ContextConfiguration(classes = {
        LoanOfferMapperImpl.class})
class LoanOfferMapperTest extends MapstructBaseTest {

    @Autowired
    private LoanOfferMapper loanOfferMapper;

    @Test
    void should_map_to_loan_offer_message(@Random LoanOfferStore loanOffer) {
        final var offerRequest = OfferRequest.builder()
                .bonifyUserId(123L)
                .userUUID("c59ed611-a374-48fe-9ad1-230cf89cd6e6")
                .isRequestedBonifyLoans(true)
                .requestedAmount(5500)
                .build();

        LoanOffersMessage dataLoanOffer = loanOfferMapper.mapLoanOffer(offerRequest, "abcd", List.of(loanOffer), 3, 6);
        assertAll(
                () -> assertThat(dataLoanOffer.getOffers().get(0).getKycStatus()).isEqualTo(loanOffer.getKycStatus()),
                () -> assertThat(dataLoanOffer.getOffers().get(0).getKycUrl()).isEqualTo(loanOffer.getKycUrl()),
                () -> assertThat(dataLoanOffer.getOffers().get(0).getOfferStatus()).isEqualTo(loanOffer.getOfferStatus()),
                () -> assertThat(dataLoanOffer.getOffers().get(0).getOfferId()).isEqualTo(loanOffer.getLoanOfferId()),
                () -> assertThat(dataLoanOffer.getOffers().get(0).getLoanProvider()).isEqualTo(loanOffer.getOffer().getLoanProvider().getName()),
                () -> assertThat(dataLoanOffer.getOffers().get(0).getAmount()).isEqualTo(loanOffer.getOffer().getAmount()),
                () -> assertThat(dataLoanOffer.getOffers().get(0).getDurationInMonth()).isEqualTo(loanOffer.getOffer().getDurationInMonth()),
                () -> assertThat(dataLoanOffer.getOffers().get(0).getEffectiveInterestRate()).isEqualTo(loanOffer.getOffer().getEffectiveInterestRate().floatValue()),
                () -> assertThat(dataLoanOffer.getUserUUID()).isEqualTo("c59ed611-a374-48fe-9ad1-230cf89cd6e6"),
                () -> assertThat(dataLoanOffer.getTimestamp()).isBefore(Instant.now()),
                () -> assertThat(dataLoanOffer.getUserId()).isEqualTo(123L),
                () -> assertThat(dataLoanOffer.getOffers().get(0).getMonthlyRate()).isEqualTo(loanOffer.getOffer().getMonthlyRate().floatValue()),
                () -> assertThat(dataLoanOffer.getOffers().get(0).getNominalInterestRate()).isEqualTo(loanOffer.getOffer().getNominalInterestRate().floatValue()),
                () -> assertThat(dataLoanOffer.getOffers().get(0).getTotalPayment()).isEqualTo(loanOffer.getOffer().getTotalPayment().floatValue()),
                () -> assertThat(dataLoanOffer.getBonifyLoansCount()).isEqualTo(3),
                () -> assertThat(dataLoanOffer.getOtherLoansCount()).isEqualTo(6),
                () -> assertThat(dataLoanOffer.getAskedForBonifyLoans()).isTrue(),
                () -> assertThat(dataLoanOffer.getRequestedLoanAmount()).isEqualTo(5500)
        );
    }

    @Test
    void map_to_loan_offer_message(@Random LoanOfferStore loanOffer) {
        // given
        final var userId = 83965L;

        // when
        LoanOffersMessage dataLoanOffer = loanOfferMapper.mapLoanOffer(userId, loanOffer.getUserUUID(), loanOffer.getApplicationId(), List.of(loanOffer), OfferUpdateType.STALE_OFFERS_NOTIFICATION);

        // then
        assertAll(
                () -> assertThat(dataLoanOffer.getOffers().get(0).getKycStatus()).isEqualTo(loanOffer.getKycStatus()),
                () -> assertThat(dataLoanOffer.getOffers().get(0).getKycUrl()).isEqualTo(loanOffer.getKycUrl()),
                () -> assertThat(dataLoanOffer.getOffers().get(0).getOfferStatus()).isEqualTo(loanOffer.getOfferStatus()),
                () -> assertThat(dataLoanOffer.getOffers().get(0).getOfferId()).isEqualTo(loanOffer.getLoanOfferId()),
                () -> assertThat(dataLoanOffer.getOffers().get(0).getLoanProvider()).isEqualTo(loanOffer.getOffer().getLoanProvider().getName()),
                () -> assertThat(dataLoanOffer.getOffers().get(0).getAmount()).isEqualTo(loanOffer.getOffer().getAmount()),
                () -> assertThat(dataLoanOffer.getOffers().get(0).getDurationInMonth()).isEqualTo(loanOffer.getOffer().getDurationInMonth()),
                () -> assertThat(dataLoanOffer.getOffers().get(0).getEffectiveInterestRate()).isEqualTo(loanOffer.getOffer().getEffectiveInterestRate().floatValue()),
                () -> assertThat(dataLoanOffer.getUserUUID()).isEqualTo(loanOffer.getUserUUID()),
                () -> assertThat(dataLoanOffer.getTimestamp()).isBefore(Instant.now()),
                () -> assertThat(dataLoanOffer.getUserId()).isEqualTo(83965L),
                () -> assertThat(dataLoanOffer.getOffers().get(0).getMonthlyRate()).isEqualTo(loanOffer.getOffer().getMonthlyRate().floatValue()),
                () -> assertThat(dataLoanOffer.getOffers().get(0).getNominalInterestRate()).isEqualTo(loanOffer.getOffer().getNominalInterestRate().floatValue()),
                () -> assertThat(dataLoanOffer.getOffers().get(0).getTotalPayment()).isEqualTo(loanOffer.getOffer().getTotalPayment().floatValue()),
                () -> assertThat(dataLoanOffer.getUpdateType()).isEqualTo(OfferUpdateType.STALE_OFFERS_NOTIFICATION.name())
        );
    }
}