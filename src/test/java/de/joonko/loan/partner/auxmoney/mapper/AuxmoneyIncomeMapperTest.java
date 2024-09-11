package de.joonko.loan.partner.auxmoney.mapper;


import com.fasterxml.jackson.core.JsonProcessingException;
import de.joonko.loan.partner.auxmoney.getoffers.BaseMapperTest;
import de.joonko.loan.partner.auxmoney.model.Income;
import de.joonko.loan.util.JsonUtil;
import io.github.glytching.junit.extension.random.Random;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

class AuxmoneyIncomeMapperTest extends BaseMapperTest {

    @Random
    private de.joonko.loan.offer.domain.Income income;


    @Autowired
    private AuxmoneyIncomeMapper incomeMapper;


    @Test
    @DisplayName("Should Convert Joonko pensionBenefits,otherRevenue,rentalIncome ,alimonyPayments To Auxmoney Other Income ")
    void shouldConvertOtherIncome() {
        Income incomeRequest = incomeMapper.toAuxmoneyIncome(income);
        Assert.assertEquals(income.getPensionBenefitsInEuroCent() + income.getOtherRevenueInEuroCent() + income.getRentalIncomeInEuroCent() + income.getAlimonyPaymentsInEuroCent(), incomeRequest.getOther());
    }

    @Test
    @DisplayName("Should Convert Joonko Sum of Income to Auxmoney Other Total")
    void shouldConvertToTotal() {
        Income incomeRequest = incomeMapper.toAuxmoneyIncome(income);
        Assert.assertEquals(income.getPensionBenefitsInEuroCent() + income.getOtherRevenueInEuroCent() + income.getRentalIncomeInEuroCent() + income.getAlimonyPaymentsInEuroCent()
                + income.getNetIncomeInEuroCent() + income.getChildBenefitsInEuroCent(), incomeRequest.getTotal());
    }


    @Test
    @DisplayName("Should Convert Income To Auxmoney Income ")
    void shouldConvertIncome() {
        Income incomeRequest = incomeMapper.toAuxmoneyIncome(income);
        Assert.assertEquals(income.getChildBenefitsInEuroCent(), incomeRequest.getBenefit());
        Assert.assertEquals(income.getNetIncomeInEuroCent(), incomeRequest.getNet());
    }


    @Test
    @DisplayName("Should Generate Json data with correct Format, No Nested Elements ")
    void testFormatOfConvertedValues() throws JsonProcessingException, JSONException {
        String expected = "{\n" +
                "\"net_income\" : 10000 ,\n" +
                "\"child_benefits\" : 10100 ,\n" +
                "\"other\" : 40000 ,\n" +
                "\"total\" : 60100\n" +
                "}";

        de.joonko.loan.offer.domain.Income build = de.joonko.loan.offer.domain.Income.builder()
                .netIncome(BigDecimal.valueOf(100.00))
                .alimonyPayments(BigDecimal.valueOf(100.00))
                .childBenefits(BigDecimal.valueOf(101.00))
                .otherRevenue(BigDecimal.valueOf(100.00))
                .pensionBenefits(BigDecimal.valueOf(100.00))
                .rentalIncome(BigDecimal.valueOf(100.00))
                .build();


        Income income = incomeMapper.toAuxmoneyIncome(build);
        String auxmoneyRequestAsJson = JsonUtil.getObjectAsJsonString(income);
        JSONAssert.assertEquals(auxmoneyRequestAsJson, expected, JSONCompareMode.LENIENT);
    }


}
