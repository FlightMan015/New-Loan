package de.joonko.loan.partner.creditPlus.mapper;

import de.joonko.loan.offer.domain.DigitalAccountStatements;
import de.joonko.loan.offer.domain.HousingType;
import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.partner.auxmoney.getoffers.BaseMapperTest;
import de.joonko.loan.partner.creditPlus.CreditPlusDefaults;
import de.joonko.loan.partner.creditPlus.stub.EfinComparerServiceStub;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import io.github.glytching.junit.extension.random.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CreditPlusDebtorFinancialDataMapperTest extends BaseMapperTest {

    @Autowired
    CreditPlusDebtorFinancialDataMapper mapper;

    @Random
    LoanDemand loanDemand;

    EfinComparerServiceStub.FinancialData financialData = new EfinComparerServiceStub.FinancialData();


    @Test
    void toFinancialData() {
        assertNotNull(financialData);
    }

    @Test
    void toIncome_4MonthsAllMatchingCategory() {
        var loanDemand = getLoanDemand(CreditPlusTestDataMapperUtil.getData4MonthsAllMatchingCategory(CreditPlusDefaults.CATEGORY_INCOME));
        financialData = mapper.toFinancialData(loanDemand);

        assertEquals(financialData.getIncome(), BigDecimal.valueOf(1112.50).setScale(2, RoundingMode.HALF_EVEN));
    }

    @Test
    void toIncome_4Months1NonMatchingCategory() {
        var loanDemand = getLoanDemand(CreditPlusTestDataMapperUtil.getData4Months1NonMatchingCategory(CreditPlusDefaults.CATEGORY_INCOME));
        financialData = mapper.toFinancialData(loanDemand);

        assertEquals(financialData.getIncome(), BigDecimal.valueOf(1150.00).setScale(2, RoundingMode.HALF_EVEN));
    }

    @Test
    void toIncome_2MonthsAllMatchingCategory() {
        var loanDemand = getLoanDemand(CreditPlusTestDataMapperUtil.getData2MonthsAllMatchingCategory(CreditPlusDefaults.CATEGORY_INCOME));
        financialData = mapper.toFinancialData(loanDemand);

        assertEquals(financialData.getIncome(), BigDecimal.valueOf(600.51).setScale(2, RoundingMode.HALF_EVEN));
    }

    @Test
    void toIncome_2Months1MatchingCategory() {
        var loanDemand = getLoanDemand(CreditPlusTestDataMapperUtil.getData2Months1MatchingCategory(CreditPlusDefaults.CATEGORY_INCOME));
        financialData = mapper.toFinancialData(loanDemand);

        assertEquals(financialData.getIncome(), BigDecimal.valueOf(800.51).setScale(2, RoundingMode.HALF_EVEN));
    }

    @Test
    void toOtherCreditRates_4MonthsAllMatchingCategory() {
        var loanDemand = getLoanDemand(CreditPlusTestDataMapperUtil.getData4MonthsAllMatchingCategory(CreditPlusDefaults.CATEGORY_OTHER_CREDIT_RATES));
        financialData = mapper.toFinancialData(loanDemand);

        assertEquals(financialData.getOtherCreditRates(), BigDecimal.valueOf(1112.50).setScale(2, RoundingMode.HALF_EVEN));
    }

    @Test
    void toOtherCreditRates_4Months1NonMatchingCategory() {
        var loanDemand = getLoanDemand(CreditPlusTestDataMapperUtil.getData4Months1NonMatchingCategory(CreditPlusDefaults.CATEGORY_OTHER_CREDIT_RATES));
        financialData = mapper.toFinancialData(loanDemand);

        assertEquals(financialData.getOtherCreditRates(), BigDecimal.valueOf(1150.00).setScale(2, RoundingMode.HALF_EVEN));
    }

    @Test
    void toOtherCreditRates_2MonthsAllMatchingCategory() {
        var loanDemand = getLoanDemand(CreditPlusTestDataMapperUtil.getData2MonthsAllMatchingCategory(CreditPlusDefaults.CATEGORY_OTHER_CREDIT_RATES));
        financialData = mapper.toFinancialData(loanDemand);

        assertEquals(financialData.getOtherCreditRates(), BigDecimal.valueOf(600.51).setScale(2, RoundingMode.HALF_EVEN));
    }

    @Test
    void toOtherCreditRates_2Months1MatchingCategory() {
        var loanDemand = getLoanDemand(CreditPlusTestDataMapperUtil.getData2Months1MatchingCategory(CreditPlusDefaults.CATEGORY_OTHER_CREDIT_RATES));
        financialData = mapper.toFinancialData(loanDemand);

        assertEquals(financialData.getOtherCreditRates(), BigDecimal.valueOf(800.51).setScale(2, RoundingMode.HALF_EVEN));
    }

    @Test
    void toOtherIncome_4MonthsAllMatchingCategory() {
        var loanDemand = getLoanDemand(CreditPlusTestDataMapperUtil.getData4MonthsAllMatchingCategory(CreditPlusDefaults.CATEGORY_OTHER_INCOME));
        financialData = mapper.toFinancialData(loanDemand);

        assertEquals(financialData.getOtherIncome(), BigDecimal.valueOf(1112.50).setScale(2, RoundingMode.HALF_EVEN));
    }

    @Test
    void toOtherIncome_4Months1NonMatchingCategory() {
        var loanDemand = getLoanDemand(CreditPlusTestDataMapperUtil.getData4Months1NonMatchingCategory(CreditPlusDefaults.CATEGORY_OTHER_INCOME));
        financialData = mapper.toFinancialData(loanDemand);

        assertEquals(financialData.getOtherIncome(), BigDecimal.valueOf(1150.00).setScale(2, RoundingMode.HALF_EVEN));
    }

    @Test
    void toOtherIncome_2MonthsAllMatchingCategory() {
        var loanDemand = getLoanDemand(CreditPlusTestDataMapperUtil.getData2MonthsAllMatchingCategory(CreditPlusDefaults.CATEGORY_OTHER_INCOME));
        financialData = mapper.toFinancialData(loanDemand);

        assertEquals(financialData.getOtherIncome(), BigDecimal.valueOf(600.51).setScale(2, RoundingMode.HALF_EVEN));
    }

    @Test
    void toOtherIncome_2Months1MatchingCategory() {
        var loanDemand = getLoanDemand(CreditPlusTestDataMapperUtil.getData2Months1MatchingCategory(CreditPlusDefaults.CATEGORY_OTHER_INCOME));
        financialData = mapper.toFinancialData(loanDemand);

        assertEquals(financialData.getOtherIncome(), BigDecimal.valueOf(800.51).setScale(2, RoundingMode.HALF_EVEN));
    }

    @Test
    void toRentalIncome_4MonthsAllMatchingCategory() {
        var loanDemand = getLoanDemand(CreditPlusTestDataMapperUtil.getData4MonthsAllMatchingCategory(CreditPlusDefaults.CATEGORY_RENTAl_INCOME));
        financialData = mapper.toFinancialData(loanDemand);

        assertEquals(financialData.getRentalIncome(), BigDecimal.valueOf(1112.50).setScale(2, RoundingMode.HALF_EVEN));
    }

    @Test
    void toRentalIncome_4Months1NonMatchingCategory() {
        var loanDemand = getLoanDemand(CreditPlusTestDataMapperUtil.getData4Months1NonMatchingCategory(CreditPlusDefaults.CATEGORY_RENTAl_INCOME));
        financialData = mapper.toFinancialData(loanDemand);

        assertEquals(financialData.getRentalIncome(), BigDecimal.valueOf(1150.00).setScale(2, RoundingMode.HALF_EVEN));
    }

    @Test
    void toRentalIncome_2MonthsAllMatchingCategory() {
        var loanDemand = getLoanDemand(CreditPlusTestDataMapperUtil.getData2MonthsAllMatchingCategory(CreditPlusDefaults.CATEGORY_RENTAl_INCOME));
        financialData = mapper.toFinancialData(loanDemand);

        assertEquals(financialData.getRentalIncome(), BigDecimal.valueOf(600.51).setScale(2, RoundingMode.HALF_EVEN));
    }

    @Test
    void toRentalIncome_2Months1MatchingCategory() {
        var loanDemand = getLoanDemand(CreditPlusTestDataMapperUtil.getData2Months1MatchingCategory(CreditPlusDefaults.CATEGORY_RENTAl_INCOME));
        financialData = mapper.toFinancialData(loanDemand);

        assertEquals(financialData.getRentalIncome(), BigDecimal.valueOf(800.51).setScale(2, RoundingMode.HALF_EVEN));
    }

    @Test
    void toOtherIncometype() {
        financialData = mapper.toFinancialData(loanDemand);
        assertEquals(financialData.getOtherIncomeType(), Integer.valueOf(CreditPlusDefaults.OTHER_INCOME_TYPE));
    }

    @Test
    void toRent_4MonthsAllMatchingCategory() {
        var loanDemand = getLoanDemand(CreditPlusTestDataMapperUtil.getData4MonthsAllMatchingCategory(CreditPlusDefaults.CATEGORY_ID_RENT));
        financialData = mapper.toFinancialData(loanDemand);

        assertEquals(financialData.getRent(), BigDecimal.valueOf(1112.50).setScale(2, RoundingMode.HALF_EVEN));
    }

    @Test
    void toRent_4Months1NonMatchingCategory() {
        var loanDemand = getLoanDemand(CreditPlusTestDataMapperUtil.getData4Months1NonMatchingCategory(CreditPlusDefaults.CATEGORY_ID_RENT));
        financialData = mapper.toFinancialData(loanDemand);

        assertEquals(financialData.getRent(), BigDecimal.valueOf(1150.00).setScale(2, RoundingMode.HALF_EVEN));
    }

    @Test
    void toRent_2MonthsAllMatchingCategory() {
        var loanDemand = getLoanDemand(CreditPlusTestDataMapperUtil.getData2MonthsAllMatchingCategory(CreditPlusDefaults.CATEGORY_ID_RENT));
        financialData = mapper.toFinancialData(loanDemand);

        assertEquals(financialData.getRent(), BigDecimal.valueOf(600.51).setScale(2, RoundingMode.HALF_EVEN));
    }

    @Test
    void toRent_2Months1MatchingCategory() {
        var loanDemand = getLoanDemand(CreditPlusTestDataMapperUtil.getData2Months1MatchingCategory(CreditPlusDefaults.CATEGORY_ID_RENT));
        financialData = mapper.toFinancialData(loanDemand);

        assertEquals(financialData.getRent(), BigDecimal.valueOf(800.51).setScale(2, RoundingMode.HALF_EVEN));
    }

    @Test
    void toPension_4MonthsAllMatchingCategory() {
        var loanDemand = getLoanDemand(CreditPlusTestDataMapperUtil.getData4MonthsAllMatchingCategory(CreditPlusDefaults.CATEGORY_PENSION));
        financialData = mapper.toFinancialData(loanDemand);

        assertEquals(financialData.getPension(), BigDecimal.valueOf(1112.50).setScale(2, RoundingMode.HALF_EVEN));
    }

    @Test
    void toPension_4Months1NonMatchingCategory() {
        var loanDemand = getLoanDemand(CreditPlusTestDataMapperUtil.getData4Months1NonMatchingCategory(CreditPlusDefaults.CATEGORY_PENSION));
        financialData = mapper.toFinancialData(loanDemand);

        assertEquals(financialData.getPension(), BigDecimal.valueOf(1150.00).setScale(2, RoundingMode.HALF_EVEN));
    }

    @Test
    void toPension_2MonthsAllMatchingCategory() {
        var loanDemand = getLoanDemand(CreditPlusTestDataMapperUtil.getData2MonthsAllMatchingCategory(CreditPlusDefaults.CATEGORY_PENSION));
        financialData = mapper.toFinancialData(loanDemand);

        assertEquals(financialData.getPension(), BigDecimal.valueOf(600.51).setScale(2, RoundingMode.HALF_EVEN));
    }

    @Test
    void toPension_2Months1MatchingCategory() {
        var loanDemand = getLoanDemand(CreditPlusTestDataMapperUtil.getData2Months1MatchingCategory(CreditPlusDefaults.CATEGORY_PENSION));
        financialData = mapper.toFinancialData(loanDemand);

        assertEquals(financialData.getPension(), BigDecimal.valueOf(800.51).setScale(2, RoundingMode.HALF_EVEN));
    }

    //

    @Test
    void toHouseCosts_4MonthsAllMatchingCategory() {
        var loanDemand = getLoanDemand(CreditPlusTestDataMapperUtil.getData4MonthsAllMatchingCategory(CreditPlusDefaults.CATEGORY_HOUSE_COSTS));
        financialData = mapper.toFinancialData(loanDemand);

        assertEquals(financialData.getHouseCosts(), BigDecimal.valueOf(1112.50).setScale(2, RoundingMode.HALF_EVEN));
    }

    @Test
    void toHouseCosts_4Months1NonMatchingCategory() {
        var loanDemand = getLoanDemand(CreditPlusTestDataMapperUtil.getData4Months1NonMatchingCategory(CreditPlusDefaults.CATEGORY_HOUSE_COSTS));
        financialData = mapper.toFinancialData(loanDemand);

        assertEquals(financialData.getHouseCosts(), BigDecimal.valueOf(1150.00).setScale(2, RoundingMode.HALF_EVEN));
    }

    @Test
    void toHouseCosts_2MonthsAllMatchingCategory() {
        var loanDemand = getLoanDemand(CreditPlusTestDataMapperUtil.getData2MonthsAllMatchingCategory(CreditPlusDefaults.CATEGORY_HOUSE_COSTS));
        financialData = mapper.toFinancialData(loanDemand);

        assertEquals(financialData.getHouseCosts(), BigDecimal.valueOf(600.51).setScale(2, RoundingMode.HALF_EVEN));
    }

    @Test
    void toHouseCosts_2Months1MatchingCategory() {
        var loanDemand = getLoanDemand(CreditPlusTestDataMapperUtil.getData2Months1MatchingCategory(CreditPlusDefaults.CATEGORY_HOUSE_COSTS));
        financialData = mapper.toFinancialData(loanDemand);

        assertEquals(financialData.getHouseCosts(), BigDecimal.valueOf(800.51).setScale(2, RoundingMode.HALF_EVEN));
    }

    //

    @Test
    void toAliment_4MonthsAllMatchingCategory() {
        var loanDemand = getLoanDemand(CreditPlusTestDataMapperUtil.getData4MonthsAllMatchingCategory(CreditPlusDefaults.CATEGORY_ALIMENT));
        financialData = mapper.toFinancialData(loanDemand);

        assertEquals(financialData.getAliment(), BigDecimal.valueOf(1112.50).setScale(2, RoundingMode.HALF_EVEN));
    }

    @Test
    void toAliment_4Months1NonMatchingCategory() {
        var loanDemand = getLoanDemand(CreditPlusTestDataMapperUtil.getData4Months1NonMatchingCategory(CreditPlusDefaults.CATEGORY_ALIMENT));
        financialData = mapper.toFinancialData(loanDemand);

        assertEquals(financialData.getAliment(), BigDecimal.valueOf(1150.00).setScale(2, RoundingMode.HALF_EVEN));
    }

    @Test
    void toAliment_2MonthsAllMatchingCategory() {
        var loanDemand = getLoanDemand(CreditPlusTestDataMapperUtil.getData2MonthsAllMatchingCategory(CreditPlusDefaults.CATEGORY_ALIMENT));
        financialData = mapper.toFinancialData(loanDemand);

        assertEquals(financialData.getAliment(), BigDecimal.valueOf(600.51).setScale(2, RoundingMode.HALF_EVEN));
    }

    @Test
    void toAliment_2Months1MatchingCategory() {
        var loanDemand = getLoanDemand(CreditPlusTestDataMapperUtil.getData2Months1MatchingCategory(CreditPlusDefaults.CATEGORY_ALIMENT));
        financialData = mapper.toFinancialData(loanDemand);

        assertEquals(financialData.getAliment(), BigDecimal.valueOf(800.51).setScale(2, RoundingMode.HALF_EVEN));
    }

    @Test
    void toPrivateHealthInsurance_4MonthsAllMatchingCategory() {
        var loanDemand = getLoanDemand(CreditPlusTestDataMapperUtil.getData4MonthsAllMatchingCategory(CreditPlusDefaults.CATEGORY_PRIVATE_HEALTH_INSURANCE));
        financialData = mapper.toFinancialData(loanDemand);

        assertEquals(financialData.getPrivateHealthInsurance(), BigDecimal.valueOf(1112.50).setScale(2, RoundingMode.HALF_EVEN));
    }

    @Test
    void toPrivateHealthInsurance_4Months1NonMatchingCategory() {
        var loanDemand = getLoanDemand(CreditPlusTestDataMapperUtil.getData4Months1NonMatchingCategory(CreditPlusDefaults.CATEGORY_PRIVATE_HEALTH_INSURANCE));
        financialData = mapper.toFinancialData(loanDemand);

        assertEquals(financialData.getPrivateHealthInsurance(), BigDecimal.valueOf(1150.00).setScale(2, RoundingMode.HALF_EVEN));
    }

    @Test
    void toPrivateHealthInsurance_2MonthsAllMatchingCategory() {
        var loanDemand = getLoanDemand(CreditPlusTestDataMapperUtil.getData2MonthsAllMatchingCategory(CreditPlusDefaults.CATEGORY_PRIVATE_HEALTH_INSURANCE));
        financialData = mapper.toFinancialData(loanDemand);

        assertEquals(financialData.getPrivateHealthInsurance(), BigDecimal.valueOf(600.51).setScale(2, RoundingMode.HALF_EVEN));
    }

    @Test
    void toPrivateHealthInsurance_2Months1MatchingCategory() {
        var loanDemand = getLoanDemand(CreditPlusTestDataMapperUtil.getData2Months1MatchingCategory(CreditPlusDefaults.CATEGORY_PRIVATE_HEALTH_INSURANCE));
        financialData = mapper.toFinancialData(loanDemand);
        assertEquals(financialData.getPrivateHealthInsurance(), BigDecimal.valueOf(800.51).setScale(2, RoundingMode.HALF_EVEN));
    }

    @Test
    void toResidenceType1_WhenOwner() {
        loanDemand.getPersonalDetails().setHousingType(HousingType.OWNER);
        financialData = mapper.toFinancialData(loanDemand);
        assertEquals(financialData.getResidenceType(), Integer.valueOf(CreditPlusDefaults.OWNER));
    }

    @Test
    void toResidenceType2_WhenRenter() {
        loanDemand.getPersonalDetails().setHousingType(HousingType.RENT);
        financialData = mapper.toFinancialData(loanDemand);
        assertEquals(financialData.getResidenceType(), Integer.valueOf(CreditPlusDefaults.RENTER));
    }

    @Test
    void toVehicleExistsIfExpenseMoreThan50Euros() {
        var loanDemand = getLoanDemand(CreditPlusTestDataMapperUtil.getData2Months1MatchingCategory(CreditPlusDefaults.CATEGORY_VEHICLE));
        financialData = mapper.toFinancialData(loanDemand);
        assertEquals(financialData.getVehicle(), Boolean.TRUE);
    }

    @Test
    void toVehicleDoesNotExistsIfExpenseLessThan50Euros() {
        var loanDemand = getLoanDemand(CreditPlusTestDataMapperUtil.getDataNoMatchingCategory(CreditPlusDefaults.CATEGORY_VEHICLE));
        financialData = mapper.toFinancialData(loanDemand);
        assertEquals(financialData.getVehicle(), Boolean.FALSE);
    }

    private LoanDemand getLoanDemand(DigitalAccountStatements digitalAccountStatements) {
        return new LoanDemand(
                null, null, null, null, null, null, null, null, null,
                digitalAccountStatements,
                null,
                null, null,
                null, null, List.of(), null);
    }
}