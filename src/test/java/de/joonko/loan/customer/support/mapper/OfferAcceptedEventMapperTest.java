package de.joonko.loan.customer.support.mapper;

import de.joonko.loan.acceptoffer.domain.OfferRequest;
import de.joonko.loan.acceptoffer.domain.OfferStatus;
import de.joonko.loan.customer.support.model.OfferAcceptedEvent;
import de.joonko.loan.db.service.LoanDemandStoreService;
import de.joonko.loan.db.vo.LoanDemandStore;
import de.joonko.loan.offer.api.LoanOffer;
import de.joonko.loan.partner.auxmoney.getoffers.BaseMapperTest;
import de.joonko.loan.util.EncrDecrService;
import io.github.glytching.junit.extension.random.Random;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

class OfferAcceptedEventMapperTest extends BaseMapperTest {

    @Autowired
    private OfferAcceptedEventMapper mapper;

    @Autowired
    private EncrDecrService encrDecrService;

    @MockBean
    private LoanDemandStoreService loanDemandStoreService;


    @Random
    private LoanDemandStore loanDemandStore;

    @BeforeEach
    void setup() {
        loanDemandStore.setEmailId(encrDecrService.anonymize("test@email.com"));
        Mockito.when(loanDemandStoreService.findById(any()))
                .thenReturn(Optional.of(loanDemandStore));
    }

    @Test
    @DisplayName("Should set email to event.email")
    void toEmail(@Random OfferStatus offerStatus, @Random LoanOffer loanOffer) {
        OfferAcceptedEvent event = mapper.mapToOfferAcceptedEvent("12345", offerStatus, loanOffer);
        Assert.assertEquals("test@email.com", event.getEmail());
    }

    @Test
    @DisplayName("Should set bank to event.bank")
    void toBank(@Random OfferStatus offerStatus, @Random LoanOffer loanOffer) {
        OfferAcceptedEvent event = mapper.mapToOfferAcceptedEvent("12345", offerStatus, loanOffer);
        Assert.assertEquals(loanOffer.getLoanProvider().getName().toString(), event.getBank());
    }

    @Test
    @DisplayName("Should set createdAt")
    void toCreatedAt(@Random OfferStatus offerStatus, @Random LoanOffer loanOffer) {
        OfferAcceptedEvent event = mapper.mapToOfferAcceptedEvent("12345", offerStatus, loanOffer);
        Assert.assertNotNull(event.getCreatedAt());
    }

    @Test
    @DisplayName("Should set duration to event.duration")
    void toDuration(@Random OfferStatus offerStatus, @Random LoanOffer loanOffer) {
        OfferAcceptedEvent event = mapper.mapToOfferAcceptedEvent("12345", offerStatus, loanOffer);
        Assert.assertEquals(loanOffer.getDurationInMonth(), event.getDuration().intValue());
    }

    @Test
    @DisplayName("Should set interestRate to event.interestRate")
    void toInterestRate(@Random OfferStatus offerStatus, @Random LoanOffer loanOffer) {
        OfferAcceptedEvent event = mapper.mapToOfferAcceptedEvent("12345", offerStatus, loanOffer);
        Assert.assertEquals(loanOffer.getEffectiveInterestRate(), event.getInterestRate());
    }

    @Test
    @DisplayName("Should ignore kyc Url")
    void toKycUrl(@Random OfferStatus offerStatus, @Random LoanOffer loanOffer) {
        OfferAcceptedEvent event = mapper.mapToOfferAcceptedEvent("12345", offerStatus, loanOffer);
        Assert.assertNull(event.getKycUrl());
    }

    @Test
    @DisplayName("Should set loanApplicationStatus to event.loanApplicationStatus")
    void toLoanApplicationStatus(@Random OfferStatus offerStatus, @Random LoanOffer loanOffer) {
        OfferAcceptedEvent event = mapper.mapToOfferAcceptedEvent("12345", offerStatus, loanOffer);
        Assert.assertEquals(offerStatus.getStatus().name(), event.getLoanApplicationStatus());
    }

    @Test
    @DisplayName("Should set monthlyRate to event.monthlyRate")
    void toMonthlyRate(@Random OfferStatus offerStatus, @Random LoanOffer loanOffer) {
        OfferAcceptedEvent event = mapper.mapToOfferAcceptedEvent("12345", offerStatus, loanOffer);
        Assert.assertEquals(loanOffer.getMonthlyRate(), event.getMonthlyRate());
    }

    @Test
    @DisplayName("Should set totalInterestPayment to event.totalInterestPayment")
    void toTotalInterestPayment(@Random OfferStatus offerStatus, @Random LoanOffer loanOffer) {
        OfferAcceptedEvent event = mapper.mapToOfferAcceptedEvent("12345", offerStatus, loanOffer);
        Assert.assertEquals(loanOffer.getTotalPayment().subtract(new BigDecimal(loanOffer.getAmount())).toString(), event.getTotalInterestPayment().toString());
    }

    @Test
    @DisplayName("Should set loanAsked to event.loanAmountAsked")
    void toLoanAmountAsked(@Random OfferStatus offerStatus, @Random LoanOffer loanOffer) {
        OfferAcceptedEvent event = mapper.mapToOfferAcceptedEvent("12345", offerStatus, loanOffer);
        Assert.assertEquals(loanOffer.getAmount(), event.getLoanAmountAsked().intValue());
    }

    @Test
    @DisplayName("Should set loanApplicationId to event.loanApplicationId")
    void toLoanApplicationId(@Random OfferStatus offerStatus, @Random LoanOffer loanOffer) {
        OfferAcceptedEvent event = mapper.mapToOfferAcceptedEvent("12345", offerStatus, loanOffer);
        Assert.assertEquals("12345", event.getLoanApplicationId());
    }

    @Test
    @DisplayName("Should set totalPayment to event.totalPayment")
    void toTotalPayment(@Random OfferStatus offerStatus, @Random LoanOffer loanOffer) {
        OfferAcceptedEvent event = mapper.mapToOfferAcceptedEvent("12345", offerStatus, loanOffer);
        Assert.assertEquals(loanOffer.getTotalPayment(), event.getTotalPayment());
    }
}
