package de.joonko.loan.partner.auxmoney.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.joonko.loan.offer.domain.Expenses;
import de.joonko.loan.partner.auxmoney.getoffers.BaseMapperTest;
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


class AuxmoneyExpenseMapperTest extends BaseMapperTest {

    @Random
    private Expenses expenses;

    private de.joonko.loan.partner.auxmoney.model.Expenses auxmoneyExpenses;

    @Autowired
    private AuxmoneyExpenseMapper mapper;

    @Test
    @DisplayName("should convert joonko Rent and Mortgages To Auxmoney rent_and_mortgage")
    void toAuxmoneyExpensesAuxmoneyRentAndMortgage() {
        this.auxmoneyExpenses = mapper.toAuxmoneyExpenses(expenses);
        Assert.assertEquals(expenses.getRentInEuroCent() + expenses.getMortgagesInEuroCent(), auxmoneyExpenses.getRentAndMortgage());
    }

    @Test
    @DisplayName("should convert support_expenses to zero ")
    void toAuxmoneyExpensesAuxmoneySupportExpenses() {
        this.auxmoneyExpenses = mapper.toAuxmoneyExpenses(expenses);
        Assert.assertEquals(0, auxmoneyExpenses.getSupportExpenses());
    }

    @Test
    @DisplayName("should convert joonko insurance_and_savings  to Auxmoney insurance_and_savings ")
    void toAuxmoneyInsuranceAndSavings() {
        this.auxmoneyExpenses = mapper.toAuxmoneyExpenses(expenses);
        Assert.assertEquals(expenses.getInsuranceAndSavingsInEuroCent() + expenses.getVehicleInsuranceInEuroCent(), auxmoneyExpenses.getInsuranceAndSavings());
    }

    @Test
    @DisplayName("should convert memberships to zero ")
    void toAuxmoneyMemberships() {
        this.auxmoneyExpenses = mapper.toAuxmoneyExpenses(expenses);
        Assert.assertEquals(0, auxmoneyExpenses.getMemberships());
    }

    @Test
    @DisplayName("should convert joonko loanInstalments  to Auxmoney debt_expenses ")
    void toAuxmoneyDebtExpenses() {
        this.auxmoneyExpenses = mapper.toAuxmoneyExpenses(expenses);
        Assert.assertEquals(expenses.getLoanInstalmentsInEuroCent(), auxmoneyExpenses.getDebtExpenses());
    }

    @Test
    @DisplayName("should convert living_expenses to zero")
    void toAuxmoneyLivingExpenses() {
        this.auxmoneyExpenses = mapper.toAuxmoneyExpenses(expenses);
        Assert.assertEquals(0, auxmoneyExpenses.getLivingExpenses());
    }

    @Test
    @DisplayName("should convert joonko Alimony,PrivateHealthInsurance  to Auxmoney other ")
    void toAuxmoneyOtherExpenses() {
        this.auxmoneyExpenses = mapper.toAuxmoneyExpenses(expenses);
        Assert.assertEquals(expenses.getAlimonyInEuroCent() + expenses.getPrivateHealthInsuranceInEuroCent(), auxmoneyExpenses.getOther());
    }

    @Test
    @DisplayName("should convert joonko Rent,Morrgages,Alimony,PrivateHealthInsurance,InsuranceAndSavings,LoanInstalments  to Auxmoney Total ")
    void toAuxmoneyTotalExpenses() {
        this.auxmoneyExpenses = mapper.toAuxmoneyExpenses(expenses);
        Assert.assertEquals(expenses.getRentInEuroCent() + expenses.getMortgagesInEuroCent() + expenses.getAlimonyInEuroCent() + expenses.getPrivateHealthInsuranceInEuroCent() + expenses.getInsuranceAndSavingsInEuroCent() + expenses.getLoanInstalmentsInEuroCent(), auxmoneyExpenses.getTotalExpenses());
    }

    @Test
    @DisplayName("should convert data in correct format")
    void checkJsonFormat() throws JsonProcessingException, JSONException {
        Expenses expenses = Expenses.builder()
                .alimony(BigDecimal.valueOf(100))
                .loanInstalments(BigDecimal.valueOf(100.0))
                .mortgages(BigDecimal.valueOf(100.0))
                .privateHealthInsurance(BigDecimal.valueOf(100.0))
                .rent(BigDecimal.valueOf(100.0))
                .insuranceAndSavings(BigDecimal.valueOf(148.60))
                .build();
        auxmoneyExpenses = mapper.toAuxmoneyExpenses(expenses);
        String expectedJson = "{ \n" +
                "   \"rent_and_mortgage\":20000,\n" +
                "   \"support_expenses\":0,\n" +
                "   \"insurance_and_savings\":14860,\n" +
                "   \"memberships\":0,\n" +
                "   \"debt_expenses\":10000,\n" +
                "   \"living_expenses\":0,\n" +
                "   \"other\":20000,\n" +
                "   \"total_expenses\":64860\n" +
                "}";

        String auxmoneyRequestAsJson = JsonUtil.getObjectAsJsonString(auxmoneyExpenses);
        JSONAssert.assertEquals(auxmoneyRequestAsJson, expectedJson, JSONCompareMode.LENIENT);

    }
}
