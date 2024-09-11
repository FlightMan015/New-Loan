package de.joonko.loan.partner.santander.mapper;

import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.partner.auxmoney.getoffers.BaseMapperTest;
import io.github.glytching.junit.extension.random.Random;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static de.joonko.loan.matcher.MatcherBox.isLoanDemandCorrectlyMatched;
import static org.hamcrest.MatcherAssert.assertThat;


class SantanderGetOfferRequestMapperTest extends BaseMapperTest {

    @Autowired
    SantanderGetOfferRequestMapper santanderGetOfferRequestMapper;

    @Test
    void loanDemand_maps_correctly_to_santander_request(@Random LoanDemand loanDemand) {
        // when
        final var mappedRequest = santanderGetOfferRequestMapper.toSantanderGetOfferRequest(loanDemand);

        // then
        assertThat(mappedRequest, isLoanDemandCorrectlyMatched(loanDemand));
    }
}