package de.joonko.loan.partner.solaris.mapper;

import de.joonko.loan.acceptoffer.domain.OfferRequest;
import de.joonko.loan.partner.auxmoney.getoffers.BaseMapperTest;
import de.joonko.loan.partner.solaris.model.SolarisAcceptOfferRequest;
import io.github.glytching.junit.extension.random.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SolarisAcceptOfferRequestMapperTest extends BaseMapperTest {

    @Autowired
    private SolarisAcceptOfferRequestMapper solarisAcceptOfferRequestMapper;

    @Test
    @DisplayName("Should map duration")
    void accountSnaphot(@Random OfferRequest offerRequest) {

        SolarisAcceptOfferRequest solarisAcceptOfferRequest = solarisAcceptOfferRequestMapper.toSolarisRequest(offerRequest);

        assertEquals(offerRequest.getDuration(), solarisAcceptOfferRequest.getDuration());
    }

    @Test
    @DisplayName("Should map loanAsked")
    void loanAsked(@Random OfferRequest offerRequest) {

        SolarisAcceptOfferRequest solarisAcceptOfferRequest = solarisAcceptOfferRequestMapper.toSolarisRequest(offerRequest);

        assertEquals(offerRequest.getLoanAsked(), solarisAcceptOfferRequest.getLoanAsked());
    }
}
