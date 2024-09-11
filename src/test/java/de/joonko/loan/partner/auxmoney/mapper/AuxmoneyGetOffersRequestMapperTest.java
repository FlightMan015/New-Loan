package de.joonko.loan.partner.auxmoney.mapper;


import com.fasterxml.jackson.core.JsonProcessingException;

import de.joonko.loan.offer.domain.LoanCategory;
import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.offer.domain.LoanDuration;
import de.joonko.loan.partner.auxmoney.getoffers.BaseMapperTest;
import de.joonko.loan.partner.auxmoney.model.AuxmoneyGetOffersRequest;
import de.joonko.loan.partner.auxmoney.model.Expenses;
import de.joonko.loan.partner.auxmoney.model.Income;
import de.joonko.loan.partner.auxmoney.model.PaymentCollectionDay;
import de.joonko.loan.util.JsonUtil;

import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;

import io.github.glytching.junit.extension.random.Random;

import java.util.List;

class AuxmoneyGetOffersRequestMapperTest extends BaseMapperTest {

    @Random
    private LoanDemand loanDemand;

    @Random
    private de.joonko.loan.offer.domain.Income income;

    @Autowired
    private AuxmoneyGetOffersRequestMapper requestMapper;

    @Autowired
    private AuxmoneyIncomeMapper incomeMapper;

    @Test
    @DisplayName("Should Convert Application Id To External ContactId")
    void shouldConvertApplicationIdToExternalContactId() {
        AuxmoneyGetOffersRequest auxmoneyGetOffersRequest = requestMapper.toAuxmoneyRequest(loanDemand);
        Assert.assertEquals(loanDemand.getLoanApplicationId()
                .toString(), auxmoneyGetOffersRequest.getExternalId());

    }

    @Test
    @DisplayName("Should Convert loan Asked")
    void convertLoanAsked() {
        AuxmoneyGetOffersRequest auxmoneyGetOffersRequest = requestMapper.toAuxmoneyRequest(loanDemand);
        Assert.assertEquals(loanDemand.getLoanAsked()
                .intValue(), auxmoneyGetOffersRequest.getLoanAsked());
    }

    @Test
    @DisplayName("Should Convert Income ")
    void shouldConvertIncome() {
        AuxmoneyGetOffersRequest auxmoneyGetOffersRequest = requestMapper.toAuxmoneyRequest(loanDemand);
        Income incomeRequest = auxmoneyGetOffersRequest.getIncome();
        de.joonko.loan.offer.domain.Income income = loanDemand.getPersonalDetails()
                .getFinance()
                .getIncome();

        Assert.assertEquals(income.getChildBenefitsInEuroCent(), incomeRequest.getBenefit());

        Assert.assertEquals(income.getNetIncomeInEuroCent(), incomeRequest.getNet());
        int total = income.getPensionBenefitsInEuroCent() + income.getOtherRevenueInEuroCent() + income.getRentalIncomeInEuroCent() + income.getAlimonyPaymentsInEuroCent() + income.getNetIncomeInEuroCent() + income.getChildBenefitsInEuroCent();
        Assert.assertEquals(total, incomeRequest.getTotal());
        int other = income.getPensionBenefitsInEuroCent() + income.getOtherRevenueInEuroCent() + income.getRentalIncomeInEuroCent() + income.getAlimonyPaymentsInEuroCent();
        Assert.assertEquals(other, incomeRequest.getOther());
    }

    @Test
    @DisplayName("Should Convert expense  ")
    void expense() {
        AuxmoneyGetOffersRequest auxmoneyGetOffersRequest = requestMapper.toAuxmoneyRequest(loanDemand);
        Expenses auxmoneyExpenses = auxmoneyGetOffersRequest.getExpenses();
        Assert.assertNotNull(auxmoneyExpenses);
        de.joonko.loan.offer.domain.Expenses expenses = loanDemand.getPersonalDetails()
                .getFinance()
                .getExpenses();
        Assert.assertEquals(expenses.getRentInEuroCent() + expenses.getMortgagesInEuroCent(), auxmoneyExpenses.getRentAndMortgage());


    }

    @Test
    @DisplayName("Should Convert Loan Duration ")
    void convertLoanDuration() {
        AuxmoneyGetOffersRequest auxmoneyGetOffersRequest = requestMapper.toAuxmoneyRequest(loanDemand);
        Assert.assertNotNull(auxmoneyGetOffersRequest.getDuration());
    }

    @Test
    @DisplayName("Should Convert Loan Category ")
    void convertLoanCategory() {
        LoanDemand loanDemand = new LoanDemand(RandomStringUtils.randomAlphabetic(20), 5000, "car", LoanDuration.TWENTY_FOUR, LoanCategory.CAR_LOAN, null, null, null, null, null, null, null, null, null, null, List.of(), null);
        AuxmoneyGetOffersRequest auxmoneyGetOffersRequest = requestMapper.toAuxmoneyRequest(loanDemand);
        Assert.assertEquals(de.joonko.loan.partner.auxmoney.model.LoanCategory.CAR_LOAN.getValue(), auxmoneyGetOffersRequest.getCategory()
                .getValue());
    }

    @Test
    @DisplayName("Should Convert collection day To default value  ")
    void convertCollectionDay() {
        AuxmoneyGetOffersRequest auxmoneyGetOffersRequest = requestMapper.toAuxmoneyRequest(loanDemand);
        Assert.assertEquals(PaymentCollectionDay.FIRST_OF_MONTH, auxmoneyGetOffersRequest.getCollectionDay());
    }

    @Test
    @DisplayName("Should Convert accepted_terms_of_service To default value true ")
    void convertisAcceptedTermsOfService() {
        AuxmoneyGetOffersRequest auxmoneyGetOffersRequest = requestMapper.toAuxmoneyRequest(loanDemand);
        Assert.assertTrue(auxmoneyGetOffersRequest.isAcceptedTermsOfService());
    }

    @Test
    @DisplayName("Should Convert is_accepted_solvency_retrieval\n To default value true ")
    void convertisAcceptedSolvencyRetrieval() {
        AuxmoneyGetOffersRequest auxmoneyGetOffersRequest = requestMapper.toAuxmoneyRequest(loanDemand);
        Assert.assertTrue(auxmoneyGetOffersRequest.isAcceptedSolvencyRetrieval());
    }


    @Test
    @DisplayName("Should Generate Json data with correct Format, No Nested Elements ")
    void testFormatOfConvertedValues() throws JsonProcessingException, JSONException {
        LoanDemand loanDemand = new LoanDemand(RandomStringUtils.randomAlphabetic(20), 5000, "car", LoanDuration.TWENTY_FOUR, LoanCategory.CAR_LOAN, null, null, null, null, null, null, null, null, null, null, List.of(), null);
        String expected = "{\"external_id\":\"" + loanDemand.getLoanApplicationId()
                .toString() + "\",\n" +
                "   \"loan_asked\":5000,\n" +
                "   \"duration\":24,\n" +
                "   \"category\":1,\n" +
                "   \"collection_day\":1,\n" +
                "   \"digital_account_statements\": [],\n" +
                "   \"is_accepted_terms_of_service\":true,\n" +
                "   \"is_accepted_solvency_retrieval\":true}";

        AuxmoneyGetOffersRequest auxmoneyGetOffersRequest = requestMapper.toAuxmoneyRequest(loanDemand);
        String auxmoneyRequestAsJson = JsonUtil.getObjectAsJsonString(auxmoneyGetOffersRequest);
        Assert.assertTrue(auxmoneyGetOffersRequest.isAcceptedSolvencyRetrieval());
        JSONAssert.assertEquals(expected, auxmoneyRequestAsJson, JSONCompareMode.STRICT);
    }


}
