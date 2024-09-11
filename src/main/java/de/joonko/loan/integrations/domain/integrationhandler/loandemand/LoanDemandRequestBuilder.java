package de.joonko.loan.integrations.domain.integrationhandler.loandemand;

import de.joonko.loan.integrations.domain.integrationhandler.fts.domain.UserTransactionalDataRepository;
import de.joonko.loan.integrations.domain.integrationhandler.fts.domain.UserTransactionalDataStore;
import de.joonko.loan.integrations.domain.integrationhandler.fts.domain.UserTransactionalDataStoreMapper;
import de.joonko.loan.integrations.domain.integrationhandler.userpersonal.mapper.ExpenseMapper;
import de.joonko.loan.integrations.domain.integrationhandler.userpersonal.mapper.IncomeMapper;
import de.joonko.loan.integrations.domain.integrationhandler.userpersonal.mapper.UserPersonalInformationMapper;
import de.joonko.loan.integrations.model.OfferRequest;
import de.joonko.loan.offer.api.AccountDetails;
import de.joonko.loan.offer.api.ContactData;
import de.joonko.loan.offer.api.EmploymentDetails;
import de.joonko.loan.offer.api.Expenses;
import de.joonko.loan.offer.api.Income;
import de.joonko.loan.offer.api.LoanDemandRequest;
import de.joonko.loan.offer.api.PersonalDetails;
import de.joonko.loan.offer.domain.DisposableAmountCalculator;
import de.joonko.loan.user.service.UserAdditionalInformationRepository;
import de.joonko.loan.user.service.UserAdditionalInformationStore;
import de.joonko.loan.user.service.UserPersonalInformationRepository;
import de.joonko.loan.user.service.UserPersonalInformationStore;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import static java.util.Optional.ofNullable;

@Slf4j
@RequiredArgsConstructor
@Component
public class LoanDemandRequestBuilder {

    private final UserTransactionalDataRepository userTransactionalDataRepository;
    private final UserAdditionalInformationRepository userAdditionalInformationRepository;
    private final UserPersonalInformationRepository userPersonalInformationRepository;

    private final UserTransactionalDataStoreMapper userTransactionalDataStoreMapper;
    private final UserPersonalInformationMapper userPersonalInformationMapper;

    private final IncomeMapper incomeMapper;
    private final ExpenseMapper expenseMapper;

    @Value("${TWEAKED_IBAN_IN_LOAN_DEMAND_REQUEST:#{NULL}}")
    private String tweakedIBAN;

    public Mono<LoanDemandRequest> build(OfferRequest offerRequest) {
        return Mono.zip(getUserPersonalInformation(offerRequest.getUserUUID()), getUserTransactionalData(offerRequest.getUserUUID()), getUserAdditionalInformation(offerRequest.getUserUUID()))
                .map(tuple -> buildLoanDemandRequest(tuple.getT1(), tuple.getT2(), tuple.getT3()))
                .map(loanDemandRequest -> {
                    loanDemandRequest.setLoanAsked(offerRequest.getRequestedAmount());
                    loanDemandRequest.setUserUUID(offerRequest.getUserUUID());
                    loanDemandRequest.setFundingPurpose(offerRequest.getRequestedPurpose());
                    loanDemandRequest.setRequestIp(offerRequest.getClientIp());
                    loanDemandRequest.setRequestCountryCode(offerRequest.getCountryCode());
                    loanDemandRequest.setIsRequestedBonifyLoans(offerRequest.isRequestedBonifyLoans());
                    return loanDemandRequest;
                })
                .subscribeOn(Schedulers.elastic());
    }

    private Mono<UserPersonalInformationStore> getUserPersonalInformation(String userUuid) {
        return Mono.fromCallable(() -> userPersonalInformationRepository.findById(userUuid))
                .map(opt -> opt.orElseThrow(() -> new IllegalStateException(String.format("Unable to find personal information for userId: %s", userUuid))))
                .doOnError(ex -> log.error(ex.getMessage()));
    }

    private Mono<UserTransactionalDataStore> getUserTransactionalData(String userUuid) {
        return Mono.fromCallable(() -> userTransactionalDataRepository.findById(userUuid))
                .map(opt -> opt.orElseThrow(() -> new IllegalStateException(String.format("Unable to find transactional data for userId: %s", userUuid))))
                .doOnError(ex -> log.error(ex.getMessage()));
    }

    private Mono<UserAdditionalInformationStore> getUserAdditionalInformation(String userUuid) {
        return Mono.fromCallable(() -> userAdditionalInformationRepository.findById(userUuid))
                .map(opt -> opt.orElseThrow(() -> new IllegalStateException(String.format("Unable to find additional information for userId: %s", userUuid))))
                .doOnError(ex -> log.error(ex.getMessage()));
    }

    private LoanDemandRequest buildLoanDemandRequest(UserPersonalInformationStore userPersonalInformation, UserTransactionalDataStore userTransactionalData, UserAdditionalInformationStore userAdditionalInformation) {
        PersonalDetails personalDetails = userPersonalInformationMapper.merge(userPersonalInformation, userAdditionalInformation, userTransactionalData);
        ContactData contactData = userPersonalInformationMapper.customMerge(userAdditionalInformation.getContactData(), userPersonalInformationMapper.map(userPersonalInformation));
        EmploymentDetails employmentDetails = userTransactionalDataStoreMapper.customMapping(userAdditionalInformation.getEmploymentDetails(), userTransactionalData.getCustomDacPersonalDetails(), userTransactionalData.getCustomDACData());
        AccountDetails accountDetails = getCorrectAccountDetails(userTransactionalData.getAccountDetails());

        final Expenses expenses = constructExpensesUsingUserInputs(userTransactionalData.getExpenses(), userAdditionalInformation.getExpenses());
        final Income income = constructIncomeUsingUserInputs(userTransactionalData.getIncome(), userAdditionalInformation.getIncome());

        return LoanDemandRequest.builder()
                .personalDetails(personalDetails)
                .accountDetails(accountDetails)
                .creditDetails(userAdditionalInformation.getCreditDetails())
                .contactData(contactData)
                .employmentDetails(employmentDetails)
                .customDACData(userTransactionalData.getCustomDACData())
                .income(income)
                .expenses(expenses)
                .consents(userAdditionalInformation.getConsentData())
                .dacId(userTransactionalData.getDacId())
                .ftsTransactionId(userTransactionalData.getFtsTransactionId())
                .disposableIncome(calculateDisposableAmount(income, expenses, personalDetails.getNumberOfChildren(), Boolean.TRUE.equals(userTransactionalData.getCustomDACData().getCarInformation()) ? 1 : 0))
                .build()
                .acknowledgeRentAndMortgages()
                .acknowledgeNetIncome();
    }

    private Income constructIncomeUsingUserInputs(final Income incomeFromTransactions, final Income incomeFromUser) {
        return incomeFromTransactions.toBuilder()
                .incomeDeclared(ofNullable(incomeFromUser).map(Income::getIncomeDeclared).orElse(null))
                .build();
    }

    private Expenses constructExpensesUsingUserInputs(final Expenses expensesFromTransactions, final Expenses expensesFromUser) {
        return expensesFromTransactions.toBuilder()
                .rent(ofNullable(expensesFromUser).map(Expenses::getRent).orElse(expensesFromTransactions.getRent()))
                .acknowledgedRent(ofNullable(expensesFromUser).map(Expenses::getAcknowledgedRent).orElse(0d))
                .monthlyLoanInstallmentsDeclared(ofNullable(expensesFromUser).map(Expenses::getMonthlyLoanInstallmentsDeclared).orElse(null))
                .monthlyLifeCost(ofNullable(expensesFromUser).map(Expenses::getMonthlyLifeCost).orElse(null))
                .build();
    }

    private BigDecimal calculateDisposableAmount(final Income income, final Expenses expenses, int numberOfChildren, int numberOfCars) {
        final var mappedIncome = incomeMapper.map(income);
        final var mappedExpense = expenseMapper.map(expenses);
        return DisposableAmountCalculator.calculateDisposableAmount(mappedIncome, mappedExpense, numberOfChildren, numberOfCars);
    }

    private AccountDetails getCorrectAccountDetails(AccountDetails accountDetails) {
        log.info("tweakedIBAN {}", tweakedIBAN);
        if (accountDetails != null && tweakedIBAN != null) {
            accountDetails.setIban(tweakedIBAN);
        }
        return accountDetails;
    }
}
