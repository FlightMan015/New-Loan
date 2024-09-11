package de.joonko.loan.partner.creditPlus.mapper;

import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.offer.domain.LoanDuration;
import de.joonko.loan.partner.auxmoney.getoffers.BaseMapperTest;
import de.joonko.loan.partner.creditPlus.CreditPlusDefaults;
import de.joonko.loan.partner.creditPlus.stub.EfinComparerServiceStub;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.github.glytching.junit.extension.random.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CreditPlusMinimalDataMapperTest extends BaseMapperTest {

    @Autowired
    CreditPlusMinimalDataMapper mapper;

    @Random
    LoanDemand loanDemand;

    EfinComparerServiceStub.Minimal minimalData = new EfinComparerServiceStub.Minimal();

    @Test
    void toAccountOwner() {
        minimalData = mapper.toMinimalData(loanDemand);
        assertEquals(minimalData.getAccountOwner(), loanDemand.getDigitalAccountStatements().getOwner());
    }

    @Test
    void toEmployerName() {
        minimalData = mapper.toMinimalData(loanDemand);
        assertEquals(minimalData.getEmployerName(), loanDemand.getEmploymentDetails().getEmployerName());
    }

    @Test
    void toIncome_4MonthsAllMatchingCategory() {
        minimalData = mapper.toMinimalData(new LoanDemand(
                null, null, null, LoanDuration.TWENTY_FOUR, null, null, null, null, null,
                CreditPlusTestDataMapperUtil.getData4MonthsAllMatchingCategory(Stream.concat(CreditPlusDefaults.CATEGORY_INCOME.stream(), CreditPlusDefaults.CATEGORY_OTHER_INCOME.stream())
                        .collect(Collectors.toList())),
                null,
                null, null,
                null, null, List.of(), null));

        assertEquals(minimalData.getIncome(), BigDecimal.valueOf(1112.50).setScale(2, RoundingMode.HALF_EVEN));
    }

    @Test
    void toIncome_4Months1NonMatchingCategory() {
        minimalData = mapper.toMinimalData(new LoanDemand(
                null, null, null, LoanDuration.TWENTY_FOUR, null, null, null, null, null,
                CreditPlusTestDataMapperUtil.getData4Months1NonMatchingCategory(Stream.concat(CreditPlusDefaults.CATEGORY_INCOME.stream(), CreditPlusDefaults.CATEGORY_OTHER_INCOME.stream())
                        .collect(Collectors.toList())),
                null,
                null, null,
                null, null, List.of(), null));

        assertEquals(minimalData.getIncome(), BigDecimal.valueOf(1150.00).setScale(2, RoundingMode.HALF_EVEN));
    }

    @Test
    void toIncome_2MonthsAllMatchingCategory() {
        minimalData = mapper.toMinimalData(new LoanDemand(
                null, null, null, LoanDuration.TWENTY_FOUR, null, null, null, null, null,
                CreditPlusTestDataMapperUtil.getData2MonthsAllMatchingCategory(Stream.concat(CreditPlusDefaults.CATEGORY_INCOME.stream(), CreditPlusDefaults.CATEGORY_OTHER_INCOME.stream())
                        .collect(Collectors.toList())),
                null,
                null, null,
                null, null, List.of(), null));

        assertEquals(minimalData.getIncome(), BigDecimal.valueOf(600.51));
    }

    @Test
    void toIncome_2Months1MatchingCategory() {
        minimalData = mapper.toMinimalData(new LoanDemand(
                null, null, null, LoanDuration.TWENTY_FOUR, null, null, null, null, null,
                CreditPlusTestDataMapperUtil.getData2Months1MatchingCategory(Stream.concat(CreditPlusDefaults.CATEGORY_INCOME.stream(), CreditPlusDefaults.CATEGORY_OTHER_INCOME.stream())
                        .collect(Collectors.toList())),
                null,
                null, null,
                null, null, List.of(), null));

        assertEquals(minimalData.getIncome(), BigDecimal.valueOf(800.51));
    }

    @Test
    void toIban() {
        minimalData = mapper.toMinimalData(loanDemand);
        assertEquals(minimalData.getIban(), loanDemand.getDigitalAccountStatements().getIban());
    }

    @Test
    void toNumberOfChild() {
        minimalData = mapper.toMinimalData(loanDemand);
        assertEquals(minimalData.getNumberOfChildren(), loanDemand.getPersonalDetails().getNumberOfChildren());
    }

    @Test
    void toPension_4MonthsAllMatchingCategory() {
        minimalData = mapper.toMinimalData(new LoanDemand(
                null, null, null, LoanDuration.TWENTY_FOUR, null, null, null, null, null,
                CreditPlusTestDataMapperUtil.getData4MonthsAllMatchingCategory(CreditPlusDefaults.CATEGORY_PENSION),
                null,
                null, null,
                null, null, List.of(), null));

        assertEquals(minimalData.getPension(), BigDecimal.valueOf(1112.50).setScale(2, RoundingMode.HALF_EVEN));
    }

    @Test
    void toPension_4Months1NonMatchingCategory() {
        minimalData = mapper.toMinimalData(new LoanDemand(
                null, null, null, LoanDuration.TWENTY_FOUR, null, null, null, null, null,
                CreditPlusTestDataMapperUtil.getData4Months1NonMatchingCategory(CreditPlusDefaults.CATEGORY_PENSION),
                null,
                null, null,
                null, null, List.of(), null));

        assertEquals(minimalData.getPension(), BigDecimal.valueOf(1150.00).setScale(2, RoundingMode.HALF_EVEN));
    }

    @Test
    void toPension_2MonthsAllMatchingCategory() {
        minimalData = mapper.toMinimalData(new LoanDemand(
                null, null, null, LoanDuration.TWENTY_FOUR, null, null, null, null, null,
                CreditPlusTestDataMapperUtil.getData2MonthsAllMatchingCategory(CreditPlusDefaults.CATEGORY_PENSION),
                null,
                null, null,
                null, null, List.of(), null));

        assertEquals(minimalData.getPension(), BigDecimal.valueOf(600.51));
    }

    @Test
    void toPension_2Months1MatchingCategory() {
        minimalData = mapper.toMinimalData(new LoanDemand(
                null, null, null, LoanDuration.TWENTY_FOUR, null, null, null, null, null,
                CreditPlusTestDataMapperUtil.getData2Months1MatchingCategory(CreditPlusDefaults.CATEGORY_PENSION),
                null,
                null, null,
                null, null, List.of(), null));

        assertEquals(minimalData.getPension(), BigDecimal.valueOf(800.51));
    }

    @Test
    void toRent_4MonthsAllMatchingCategory() {
        minimalData = mapper.toMinimalData(new LoanDemand(
                null, null, null, LoanDuration.TWENTY_FOUR, null, null, null, null, null,
                CreditPlusTestDataMapperUtil.getData4MonthsAllMatchingCategory(CreditPlusDefaults.CATEGORY_ID_RENT),
                null,
                null, null,
                null, null, List.of(), null));

        assertEquals(minimalData.getRent(), BigDecimal.valueOf(1112.50).setScale(2, RoundingMode.HALF_EVEN));
    }

    @Test
    void toRent_4Months1NonMatchingCategory() {
        minimalData = mapper.toMinimalData(new LoanDemand(
                null, null, null, LoanDuration.TWENTY_FOUR, null, null, null, null, null,
                CreditPlusTestDataMapperUtil.getData4Months1NonMatchingCategory(CreditPlusDefaults.CATEGORY_ID_RENT),
                null,
                null, null,
                null, null, List.of(), null));

        assertEquals(minimalData.getRent(), BigDecimal.valueOf(1150.00).setScale(2, RoundingMode.HALF_EVEN));
    }

    @Test
    void toRent_2MonthsAllMatchingCategory() {
        minimalData = mapper.toMinimalData(new LoanDemand(
                null, null, null, LoanDuration.TWENTY_FOUR, null, null, null, null, null,
                CreditPlusTestDataMapperUtil.getData2MonthsAllMatchingCategory(CreditPlusDefaults.CATEGORY_ID_RENT),
                null,
                null, null,
                null, null, List.of(), null));

        assertEquals(minimalData.getRent(), BigDecimal.valueOf(600.51));
    }

    @Test
    void toRent_2Months1MatchingCategory() {
        minimalData = mapper.toMinimalData(new LoanDemand(
                null, null, null, LoanDuration.TWENTY_FOUR, null, null, null, null, null,
                CreditPlusTestDataMapperUtil.getData2Months1MatchingCategory(CreditPlusDefaults.CATEGORY_ID_RENT),
                null,
                null, null,
                null, null, List.of(), null));

        assertEquals(minimalData.getRent(), BigDecimal.valueOf(800.51));
    }

    @Test
    void toScore() {
        minimalData = mapper.toMinimalData(loanDemand);
        assertEquals(minimalData.getScore(), Integer.valueOf(CreditPlusDefaults.SCORE));
    }

    @Test
    void toVehicleExistsIfExpenseMoreThan50Euros() {
        minimalData = mapper.toMinimalData(new LoanDemand(
                null, null, null, LoanDuration.TWENTY_FOUR, null, null, null, null, null,
                CreditPlusTestDataMapperUtil.getData2Months1MatchingCategory(CreditPlusDefaults.CATEGORY_VEHICLE),
                null,
                null, null,
                null, null, List.of(), null));
        assertEquals(minimalData.getVehicleExists(), Boolean.TRUE);
    }

    @Test
    void toVehicleDoesNotExistsIfExpenseLessThan50Euros() {
        minimalData = mapper.toMinimalData(new LoanDemand(
                null, null, null, LoanDuration.TWENTY_FOUR, null, null, null, null, null,
                CreditPlusTestDataMapperUtil.getDataNoMatchingCategory(CreditPlusDefaults.CATEGORY_VEHICLE),
                null,
                null, null,
                null, null, List.of(), null));
        assertEquals(minimalData.getVehicleExists(), Boolean.FALSE);
    }

}