package de.joonko.loan.data.support.mapper;

import de.joonko.loan.data.support.model.DataLoanOffer;
import de.joonko.loan.offer.api.LoanOffer;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import io.github.glytching.junit.extension.random.Random;

@ContextConfiguration(classes = {
        DataOfferMapperImpl.class})
class DataOfferMapperTest extends MapstructBaseTest {

    @Autowired
    private DataOfferMapper dataOfferMapper;

    @Test
    void should_map_to_data_loan_offer(@Random LoanOffer loanOffer) {
        DataLoanOffer dataLoanOffer = dataOfferMapper.mapLoanOffer("12345", loanOffer);
        Assert.assertEquals(dataLoanOffer.getAmount(), loanOffer.getAmount());
        Assert.assertEquals(dataLoanOffer.getDurationInMonth(), loanOffer.getDurationInMonth());
        Assert.assertEquals(dataLoanOffer.getEffectiveInterestRate(), loanOffer.getEffectiveInterestRate());
        Assert.assertEquals(dataLoanOffer.getLoanOfferId(), "12345");
        Assert.assertEquals(dataLoanOffer.getMonthlyRate(), loanOffer.getMonthlyRate());
        Assert.assertEquals(dataLoanOffer.getNominalInterestRate(), loanOffer.getNominalInterestRate());
        Assert.assertEquals(dataLoanOffer.getTotalPayment(), loanOffer.getTotalPayment());
    }


}