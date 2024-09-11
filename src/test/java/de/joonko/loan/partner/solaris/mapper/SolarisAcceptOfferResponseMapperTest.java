package de.joonko.loan.partner.solaris.mapper;

import de.joonko.loan.acceptoffer.domain.OfferStatus;
import de.joonko.loan.partner.auxmoney.getoffers.BaseMapperTest;
import de.joonko.loan.partner.solaris.model.LoanStatus;
import de.joonko.loan.partner.solaris.model.SolarisAcceptOfferResponse;
import io.github.glytching.junit.extension.random.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SolarisAcceptOfferResponseMapperTest extends BaseMapperTest {

    @Autowired
    SolarisAcceptOfferResponseMapper solarisAcceptOfferResponseMapper;

    @Random
    SolarisAcceptOfferResponse solarisAcceptOfferResponse;

    @Test
    @DisplayName("Should map status")
    void status() {
        solarisAcceptOfferResponse.setStatus(LoanStatus.APPROVED);
        OfferStatus offerStatus =  solarisAcceptOfferResponseMapper.fromSolarisResponse(solarisAcceptOfferResponse);
        assertEquals(solarisAcceptOfferResponse.getStatus().toString(), offerStatus.getStatus().toString());
    }

    @Test
    @DisplayName("Should map url to kycUrl")
    void url() {
        solarisAcceptOfferResponse.setUrl("https://anyurl.com");
        OfferStatus offerStatus = solarisAcceptOfferResponseMapper.fromSolarisResponse(solarisAcceptOfferResponse);
        assertEquals(solarisAcceptOfferResponse.getUrl(), offerStatus.getKycUrl());
    }

    @Test
    @DisplayName("Should map contract to contract")
    void contract() {
        solarisAcceptOfferResponse.setContract("contract-blog".getBytes());
        OfferStatus offerStatus = solarisAcceptOfferResponseMapper.fromSolarisResponse(solarisAcceptOfferResponse);
        assertEquals(Arrays.toString(solarisAcceptOfferResponse.getContract()), Arrays.toString(offerStatus.getContract()));
    }

    @Test
    @DisplayName("Should map preContract to preContract")
    void preContract() {
        solarisAcceptOfferResponse.setPreContract("precontract-blog".getBytes());
        OfferStatus offerStatus = solarisAcceptOfferResponseMapper.fromSolarisResponse(solarisAcceptOfferResponse);
        assertEquals(Arrays.toString(solarisAcceptOfferResponse.getPreContract()), Arrays.toString(offerStatus.getPreContract()));
    }
}
