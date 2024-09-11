package de.joonko.loan.offer.domain;

import de.joonko.loan.offer.api.AccountDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class DigitalAccountStatements {

    private String owner;

    private LocalDate balanceDate;

    private Double dispoLimit;

    private Double balance;

    private ClassificationProvider dacSource;

    private String iban;

    private String bankAccountName;

    private Currency currency;

    private BankAccountCategory category;

    private String bankAccountType;

    private List<Transaction> transactions;

    private String bic;

    private LocalDate accountSince; // The date since when the owner owns the bank account

    private int days;

    private LocalDateTime createdAt;

    private Boolean isJointlyManaged;

    private String bankName;

    public static DigitalAccountStatements fakeValue(AccountDetails accountDetails) {
        return DigitalAccountStatements.builder()
                .accountSince(DomainDefault.ACCOUNT_SINCE) //TODO this was work around for friends and family
                .balance(accountDetails.getBalance())
                .balanceDate(accountDetails.getBalanceDate())
                .bankAccountName("Personal Loan Account") // TODO Hard Code
                .bankAccountType("Loan account") // TODO Hard Code
                .bic(accountDetails.getBic())
                .category(DomainDefault.DEFAULT_BANK_CATEGORY) // TODO Hard Code
                .currency(Currency.valueOf(accountDetails.getCurrency()))
                .dacSource(DomainDefault.DEFAULT_CLASSIFICATION_PROVIDER)
                .dispoLimit(accountDetails.getLimit())
                .iban(accountDetails.getIban())
                .owner(accountDetails.getNameOnAccount())
                .transactions(accountDetails.getTransactions().stream().map(Transaction::fakeValue).collect(Collectors.toList()))
                .days(accountDetails.getDays())
                .createdAt(accountDetails.getCreatedAt())
                .isJointlyManaged(accountDetails.getIsJointlyManaged())
                .bankName(accountDetails.getBankName())
                .build();
    }

    public Map<String, List<Transaction>> groupTransactionByMonth() {
        Map<String, List<Transaction>> mapOfMonthAndTransactions = new HashMap();
        transactions.stream().forEach(transaction -> {
            String yearMonth = String.valueOf(transaction.getBookingDate().getYear()) + String.valueOf(transaction.getBookingDate().getMonthValue());
            if (mapOfMonthAndTransactions.containsKey(yearMonth)) {
                List<Transaction> transactions = mapOfMonthAndTransactions.get(yearMonth);
                transactions.add(transaction);
                mapOfMonthAndTransactions.put(yearMonth, transactions);
            } else {
                List<Transaction> transactions = new ArrayList();
                transactions.add(transaction);
                mapOfMonthAndTransactions.put(yearMonth, transactions);
            }
        });
        return mapOfMonthAndTransactions;
    }

}
