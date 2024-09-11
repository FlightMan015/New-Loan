package de.joonko.loan.partner.auxmoney.mapper;

import de.joonko.loan.offer.domain.LoanOffer;
import de.joonko.loan.partner.auxmoney.getoffers.BaseMapperTest;
import de.joonko.loan.partner.auxmoney.model.AuxmoneySingleCallResponse;
import io.github.glytching.junit.extension.random.Random;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


class AuxmoneyOffersMapperTest extends BaseMapperTest {

    @Autowired
    private AuxmoneyGetOffersResponseMapper auxmoneyGetOffersResponseMapper;

    @Random
    private AuxmoneySingleCallResponse auxmoneySingleCallResponse;

    @Test
    void fromAuxmoneyOffers() {

        List<LoanOffer> offers = auxmoneyGetOffersResponseMapper.fromAuxmoneyResponse(List.of(auxmoneySingleCallResponse));

    }
}
