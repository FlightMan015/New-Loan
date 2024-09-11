package de.joonko.loan.partner.creditPlus.mapper;

import de.joonko.loan.offer.domain.DigitalAccountStatements;
import de.joonko.loan.offer.domain.Transaction;
import de.joonko.loan.partner.creditPlus.CreditPlusDefaults;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CreditPlusTestDataMapperUtil {


    private static Map<String, List<String>> categoryIdListMap = Map.of("rent", CreditPlusDefaults.CATEGORY_ID_RENT,
            "pension", CreditPlusDefaults.CATEGORY_PENSION,
            "income", CreditPlusDefaults.CATEGORY_INCOME,
            "otherIncome", CreditPlusDefaults.CATEGORY_OTHER_INCOME,
            "aliment", CreditPlusDefaults.CATEGORY_ALIMENT,
            "childBenefit", CreditPlusDefaults.CATEGORY_CHILD_BENEFIT,
            "houseCosts", CreditPlusDefaults.CATEGORY_HOUSE_COSTS,
            "otherCreditRates", CreditPlusDefaults.CATEGORY_OTHER_CREDIT_RATES,
            "privateHealthInsurance", CreditPlusDefaults.CATEGORY_PRIVATE_HEALTH_INSURANCE,
            "rentalIncome", CreditPlusDefaults.CATEGORY_RENTAl_INCOME
    );

    public static DigitalAccountStatements getData4MonthsAllMatchingCategory(List<String> categories) {
        return DigitalAccountStatements.builder()
                .transactions(List.of(
                        Transaction.builder().bookingDate(LocalDate.of(2019, 10, 12)).amount(200.0).categoryId(pickRandomCategory(categories)).build(),
                        Transaction.builder().bookingDate(LocalDate.of(2019, 10, 14)).amount(400.0).categoryId(pickRandomCategory(categories)).build(),
                        Transaction.builder().bookingDate(LocalDate.of(2019, 11, 12)).amount(2000.0).categoryId(pickRandomCategory(categories)).build(),
                        Transaction.builder().bookingDate(LocalDate.of(2019, 11, 12)).amount(100.0).categoryId(pickRandomCategory(categories)).build(),
                        Transaction.builder().bookingDate(LocalDate.of(2019, 9, 12)).amount(600.0).categoryId(pickRandomCategory(categories)).build(),
                        Transaction.builder().bookingDate(LocalDate.of(2019, 9, 12)).amount(150.0).categoryId(pickRandomCategory(categories)).build(),
                        Transaction.builder().bookingDate(LocalDate.of(2019, 12, 12)).amount(1000.0).categoryId(pickRandomCategory(categories)).build()
                )).build();
    }

    public static DigitalAccountStatements getData4Months1NonMatchingCategory(List<String> categories) { // 750+2100+610
        return DigitalAccountStatements.builder()
                .transactions(List.of(
                        Transaction.builder().bookingDate(LocalDate.of(2019, 10, 12)).amount(200.0).categoryId(pickRandomCategory(categories)).build(),
                        Transaction.builder().bookingDate(LocalDate.of(2019, 10, 14)).amount(400.0).categoryId(pickRandomCategory(categories)).build(),
                        Transaction.builder().bookingDate(LocalDate.of(2019, 11, 12)).amount(2000.0).categoryId(pickRandomCategory(categories)).build(),
                        Transaction.builder().bookingDate(LocalDate.of(2019, 11, 12)).amount(100.0).categoryId(pickRandomCategory(categories)).build(),
                        Transaction.builder().bookingDate(LocalDate.of(2019, 9, 12)).amount(600.0).categoryId(pickRandomCategory(categories)).build(),
                        Transaction.builder().bookingDate(LocalDate.of(2019, 9, 12)).amount(150.0).categoryId(pickRandomCategory(categories)).build(),
                        Transaction.builder().bookingDate(LocalDate.of(2019, 12, 12)).amount(1000.0).categoryId("random").build()
                )).build();
    }

    public static DigitalAccountStatements getData2MonthsAllMatchingCategory(List<String> categories) {
        return DigitalAccountStatements.builder()
                .transactions(List.of(
                        Transaction.builder().bookingDate(LocalDate.of(2019, 10, 12)).amount(200.51).categoryId(pickRandomCategory(categories)).build(),
                        Transaction.builder().bookingDate(LocalDate.of(2019, 10, 14)).amount(400.0).categoryId(pickRandomCategory(categories)).build(),
                        Transaction.builder().bookingDate(LocalDate.of(2019, 11, 12)).amount(2000.0).categoryId(pickRandomCategory(categories)).build(),
                        Transaction.builder().bookingDate(LocalDate.of(2019, 11, 12)).amount(100.0).categoryId(pickRandomCategory(categories)).build()
                )).build();
    }

    public static DigitalAccountStatements getData2Months1MatchingCategory(List<String> categories) {
        return DigitalAccountStatements.builder()
                .transactions(List.of(
                        Transaction.builder().bookingDate(LocalDate.of(2019, 10, 12)).amount(300.51).categoryId(pickRandomCategory(categories)).build(),
                        Transaction.builder().bookingDate(LocalDate.of(2019, 10, 14)).amount(500.0).categoryId(pickRandomCategory(categories)).build(),
                        Transaction.builder().bookingDate(LocalDate.of(2019, 11, 12)).amount(2000.0).categoryId("random1").build(),
                        Transaction.builder().bookingDate(LocalDate.of(2019, 11, 12)).amount(100.0).categoryId("random2").build()
                )).build();
    }

    public static DigitalAccountStatements getDataNoMatchingCategory(List<String> categories) {
        return DigitalAccountStatements.builder()
                .transactions(List.of(
                        Transaction.builder().bookingDate(LocalDate.of(2019, 10, 12)).amount(300.0).categoryId("dsfdsf").build(),
                        Transaction.builder().bookingDate(LocalDate.of(2019, 10, 14)).amount(500.0).categoryId("erfdgf").build(),
                        Transaction.builder().bookingDate(LocalDate.of(2019, 11, 12)).amount(2000.0).categoryId("random1").build(),
                        Transaction.builder().bookingDate(LocalDate.of(2019, 11, 12)).amount(100.0).categoryId("random2").build()
                )).build();
    }

    private static String pickRandomCategory(List<String> categoryList) {
        return categoryList.get(new Random().nextInt(categoryList.size()));
    }
}
