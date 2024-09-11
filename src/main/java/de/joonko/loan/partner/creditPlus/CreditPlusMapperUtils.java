package de.joonko.loan.partner.creditPlus;

import de.joonko.loan.offer.domain.DigitalAccountStatements;
import de.joonko.loan.offer.domain.Transaction;
import de.joonko.loan.partner.creditPlus.stub.EfinComparerServiceStub;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
public class CreditPlusMapperUtils {

    public static BigDecimal getCalculatedAmount(DigitalAccountStatements digitalAccountStatements, List<String> categoryIdList) {
        Map<String, BigDecimal> monthToSumMap = mapMonthToSum(digitalAccountStatements, categoryIdList);
        if (monthToSumMap.size() == 0) {
            return BigDecimal.ZERO;
        } else if (monthToSumMap.size() == 1) {
            return monthToSumMap.get(monthToSumMap.keySet().toArray()[0]);
        } else if (monthToSumMap.size() == 2) {
            String[] mapElements = monthToSumMap.keySet().toArray(String[]::new);
            return monthToSumMap.get(mapElements[0]).compareTo(monthToSumMap.get(mapElements[1])) > 0 ? monthToSumMap.get(mapElements[1]) : monthToSumMap.get(mapElements[0]);
        } else {
            return monthToSumMap.values().stream().reduce(BigDecimal.valueOf(0), BigDecimal::add).divide(BigDecimal.valueOf(monthToSumMap.size()), 2, RoundingMode.HALF_UP);
        }
    }

    public static Map<String, BigDecimal> mapMonthToSum(DigitalAccountStatements digitalAccountStatements, List<String> categoryIdList) {
        Map<String, List<Transaction>> transactionByMonth = digitalAccountStatements.groupTransactionByMonth();

        Map<String, BigDecimal> monthToSumMap = new HashMap();

        transactionByMonth.forEach((month, transactions) -> {
            AtomicReference<BigDecimal> rentSum = new AtomicReference<>(BigDecimal.valueOf(0));
            AtomicBoolean categoryMatched = new AtomicBoolean(false);
            transactions.forEach(transaction -> {
                if (categoryIdList.contains(transaction.getCategoryId())) {
                    categoryMatched.set(true);
                    rentSum.updateAndGet(value -> BigDecimal.valueOf(transaction.getAmount()).add(value));
                }
            });
            if (categoryMatched.get()) {
                monthToSumMap.put(month, rentSum.get());
            }

        });
        return monthToSumMap;
    }

    public static BigDecimal calculateChildBenefit(DigitalAccountStatements digitalAccountStatements) {
        Map<String, BigDecimal> monthToSum = mapMonthToSum(digitalAccountStatements, CreditPlusDefaults.CATEGORY_CHILD_BENEFIT);
        Date date = new Date();
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        String fullMonth = String.valueOf(localDate.getYear() + String.valueOf(localDate.getMonthValue() - 1));
        if (monthToSum.containsKey(fullMonth)) {
            return monthToSum.get(fullMonth);
        }
        return BigDecimal.ZERO;
    }

    public static EfinComparerServiceStub.Revenue[] mapToRevenues(DigitalAccountStatements digitalAccountStatements) {
        Map<String, String> ftsToCreditPlusCategoryMap = new HashMap();

        try {
            ftsToCreditPlusCategoryMap = getCreditPlusCategoryMap();
        } catch (IOException e) {
            log.info("Error occured during mapping FTS category to CreditPlus categories ", e);
        }

        List<EfinComparerServiceStub.Revenue> revenues = new ArrayList();
        Map<String, String> finalFtsToCreditPlusCategoryMap = ftsToCreditPlusCategoryMap;
        AtomicInteger index = new AtomicInteger(1);

        digitalAccountStatements.getTransactions().forEach(transaction -> {
            EfinComparerServiceStub.Revenue revenue = new EfinComparerServiceStub.Revenue();
            revenue.setAccountNumber(transaction.getIban());
            revenue.setAmount(BigDecimal.valueOf(transaction.getAmount()));
            if (index.get() == digitalAccountStatements.getTransactions().size()) { // to be set just for last transaction
                revenue.setBalance(BigDecimal.valueOf(digitalAccountStatements.getBalance()));
            }
            revenue.setBankCode(transaction.getBic());
            Calendar calendar = Calendar.getInstance();
            calendar.clear();
            calendar.set(transaction.getBookingDate().getYear(), transaction.getBookingDate().getMonthValue(), transaction.getBookingDate().getDayOfMonth());
            revenue.setBookingDate(calendar);
            revenue.setCategory(finalFtsToCreditPlusCategoryMap.get(transaction.getCategoryId()));
            revenue.setIban(transaction.getIban());
            revenue.setNameOfContraAccount(transaction.getCounterHolder());
            revenue.setReasonForTheSepaTransfer(transaction.getPurpose());
            revenues.add(revenue);
            index.getAndIncrement();
        });

        EfinComparerServiceStub.Revenue[] revenueArray = new EfinComparerServiceStub.Revenue[revenues.size()];
        return revenues.toArray(revenueArray);
    }

    private static Map<String, String> getCreditPlusCategoryMap() throws IOException {
        Resource resource = new ClassPathResource("creditplus/categoryMapping.csv");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            Map<String, String> creditPlusCategoryMap = br.lines().map(line -> line.split(","))
                    .collect(Collectors.toMap(line -> line[0].trim(), line -> line[1].trim()));
            br.lines().close();

            return creditPlusCategoryMap;
        }

    }
}
