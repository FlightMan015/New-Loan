package de.joonko.loan.partner.consors.mapper;

import de.joonko.loan.acceptoffer.domain.OfferRequest;
import de.joonko.loan.partner.auxmoney.getoffers.BaseMapperTest;
import de.joonko.loan.partner.consors.model.ConsorsAcceptOfferRequest;
import io.github.glytching.junit.extension.random.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConsorsAcceptOfferRequestMapperTest extends BaseMapperTest {

    @Autowired
    private ConsorsAcceptOfferRequestMapper consorsAcceptOfferRequestMapper;

    @Test
    @DisplayName("Should map creditAmount to loanAsked")
    void creditAmount(@Random OfferRequest offerRequest) {
        ConsorsAcceptOfferRequest consorsAcceptOfferRequest = consorsAcceptOfferRequestMapper.toConsorsRequest(offerRequest);
        assertEquals(consorsAcceptOfferRequest.getFinancialCondition().getCreditAmount(), offerRequest.getLoanAsked());
    }

    @Test
    @DisplayName("Should map duration")
    void mapDuration(@Random OfferRequest offerRequest) {
        ConsorsAcceptOfferRequest consorsAcceptOfferRequest = consorsAcceptOfferRequestMapper.toConsorsRequest(offerRequest);
        assertEquals(consorsAcceptOfferRequest.getFinancialCondition().getDuration(), offerRequest.getDuration().getValue());
    }

    @Test
    @DisplayName("Should map paymentDay")
    void paymentDay(@Random OfferRequest offerRequest) {
        ConsorsAcceptOfferRequest consorsAcceptOfferRequest = consorsAcceptOfferRequestMapper.toConsorsRequest(offerRequest);
        assertEquals(consorsAcceptOfferRequest.getPaymentDay(), 1);
    }
}
