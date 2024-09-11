package de.joonko.loan.integrations.domain.integrationhandler.loandemand;

import de.joonko.loan.integrations.domain.integrationhandler.fts.domain.UserTransactionalDataRepository;
import de.joonko.loan.integrations.domain.integrationhandler.fts.domain.UserTransactionalDataStore;
import de.joonko.loan.integrations.domain.integrationhandler.fts.domain.UserTransactionalDataStoreMapper;
import de.joonko.loan.integrations.domain.integrationhandler.userpersonal.mapper.ExpenseMapper;
import de.joonko.loan.integrations.domain.integrationhandler.userpersonal.mapper.IncomeMapper;
import de.joonko.loan.integrations.domain.integrationhandler.userpersonal.mapper.UserPersonalInformationMapper;
import de.joonko.loan.integrations.model.OfferRequest;
import de.joonko.loan.offer.api.Expenses;
import de.joonko.loan.offer.api.Income;
import de.joonko.loan.offer.domain.DisposableAmountCalculator;
import de.joonko.loan.offer.domain.ExpenseModel;
import de.joonko.loan.offer.domain.IncomeModel;
import de.joonko.loan.user.service.UserAdditionalInformationRepository;
import de.joonko.loan.user.service.UserAdditionalInformationStore;
import de.joonko.loan.user.service.UserPersonalInformationRepository;
import de.joonko.loan.user.service.UserPersonalInformationStore;
import de.joonko.loan.webhooks.postbank.model.CreditResult;

import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import reactor.test.StepVerifier;

import java.util.Optional;

import static java.util.Optional.ofNullable;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({RandomBeansExtension.class, MockitoExtension.class})
class LoanDemandRequestBuilderTest {

    private LoanDemandRequestBuilder builder;

    private UserTransactionalDataRepository userTransactionalDataRepository;
    private UserAdditionalInformationRepository userAdditionalInformationRepository;
    private UserPersonalInformationRepository userPersonalInformationRepository;

    private UserTransactionalDataStoreMapper userTransactionalDataStoreMapper;
    private UserPersonalInformationMapper userPersonalInformationMapper;

    private IncomeMapper incomeMapper;
    private ExpenseMapper expenseMapper;

    @BeforeEach
    void setUp() {
        userTransactionalDataRepository = mock(UserTransactionalDataRepository.class);
        userAdditionalInformationRepository = mock(UserAdditionalInformationRepository.class);
        userPersonalInformationRepository = mock(UserPersonalInformationRepository.class);

        userTransactionalDataStoreMapper = mock(UserTransactionalDataStoreMapper.class);
        userPersonalInformationMapper = mock(UserPersonalInformationMapper.class);
        incomeMapper = mock(IncomeMapper.class);
        expenseMapper = mock(ExpenseMapper.class);

        builder = new LoanDemandRequestBuilder(userTransactionalDataRepository, userAdditionalInformationRepository, userPersonalInformationRepository,
                userTransactionalDataStoreMapper, userPersonalInformationMapper, incomeMapper, expenseMapper);
    }

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    void build_loan_demand_request_when_missing_personal_data(@Random OfferRequest offerRequest) {
        // given
        when(userPersonalInformationRepository.findById(offerRequest.getUserUUID())).thenReturn(Optional.empty());
        when(userTransactionalDataRepository.findById(offerRequest.getUserUUID())).thenReturn(Optional.empty());
        when(userAdditionalInformationRepository.findById(offerRequest.getUserUUID())).thenReturn(Optional.empty());

        // when
        var loanDemandRequest = builder.build(offerRequest);

        // then
        StepVerifier.create(loanDemandRequest)
                .verifyErrorMessage("Unable to find personal information for userId: " + offerRequest.getUserUUID());
    }

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    void build_loan_demand_request_when_missing_transactional_data(@Random OfferRequest offerRequest) {
        // given
        when(userPersonalInformationRepository.findById(offerRequest.getUserUUID())).thenReturn(Optional.of(new UserPersonalInformationStore()));
        when(userTransactionalDataRepository.findById(offerRequest.getUserUUID())).thenReturn(Optional.empty());
        when(userAdditionalInformationRepository.findById(offerRequest.getUserUUID())).thenReturn(Optional.empty());

        // when
        var loanDemandRequest = builder.build(offerRequest);

        // then
        StepVerifier.create(loanDemandRequest)
                .verifyErrorMessage("Unable to find transactional data for userId: " + offerRequest.getUserUUID());
    }

    @Test
    void build_loan_demand_request_when_missing_additional_data(@Random OfferRequest offerRequest) {
        // given
        when(userPersonalInformationRepository.findById(offerRequest.getUserUUID())).thenReturn(Optional.of(new UserPersonalInformationStore()));
        when(userTransactionalDataRepository.findById(offerRequest.getUserUUID())).thenReturn(Optional.of(new UserTransactionalDataStore()));
        when(userAdditionalInformationRepository.findById(offerRequest.getUserUUID())).thenReturn(Optional.empty());

        // when
        var loanDemandRequest = builder.build(offerRequest);

        // then
        StepVerifier.create(loanDemandRequest)
                .verifyErrorMessage("Unable to find additional information for userId: " + offerRequest.getUserUUID());
    }

    @Test
    void build_loanDemandRequest_withCorrectMappings(@Random OfferRequest offerRequest, @Random UserPersonalInformationStore userPersonalInformationStore, @Random UserAdditionalInformationStore userAdditionalInformationStore, @Random UserTransactionalDataStore userTransactionalDataStore) {
        // given
        final var income = IncomeModel.income();
        final var expenses = ExpenseModel.expense();
        final var disposableAmount = DisposableAmountCalculator.calculateDisposableAmount(income, expenses, userAdditionalInformationStore.getPersonalDetails().getNumberOfChildren(), Boolean.TRUE.equals(userTransactionalDataStore.getCustomDACData().getCarInformation()) ? 1 : 0);

        final var argumentCaptor = ArgumentCaptor.forClass(Expenses.class);

        when(userPersonalInformationRepository.findById(offerRequest.getUserUUID())).thenReturn(Optional.of(userPersonalInformationStore));
        when(userTransactionalDataRepository.findById(offerRequest.getUserUUID())).thenReturn(Optional.of(userTransactionalDataStore));
        when(userAdditionalInformationRepository.findById(offerRequest.getUserUUID())).thenReturn(Optional.of(userAdditionalInformationStore));

        when(userPersonalInformationMapper.merge(userPersonalInformationStore, userAdditionalInformationStore, userTransactionalDataStore)).thenReturn(userAdditionalInformationStore.getPersonalDetails());
        when(userPersonalInformationMapper.map(userPersonalInformationStore)).thenReturn(userAdditionalInformationStore.getContactData());
        when(userPersonalInformationMapper.customMerge(userAdditionalInformationStore.getContactData(), userAdditionalInformationStore.getContactData())).thenReturn(userAdditionalInformationStore.getContactData());
        when(userTransactionalDataStoreMapper.customMapping(userAdditionalInformationStore.getEmploymentDetails(), userTransactionalDataStore.getCustomDacPersonalDetails(), userTransactionalDataStore.getCustomDACData())).thenReturn(userAdditionalInformationStore.getEmploymentDetails());
        when(incomeMapper.map(any(Income.class))).thenReturn(income);
        when(expenseMapper.map(any(Expenses.class))).thenReturn(expenses);
        // when
        var response = builder.build(offerRequest);

        // then
        StepVerifier.create(response)
                .consumeNextWith(loanDemandRequest -> {
                    assertAll(
                            () -> assertEquals(offerRequest.getRequestedAmount(), loanDemandRequest.getLoanAsked()),
                            () -> assertEquals(offerRequest.getUserUUID(), loanDemandRequest.getUserUUID()),
                            () -> assertEquals(offerRequest.getRequestedPurpose(), loanDemandRequest.getFundingPurpose()),
                            () -> assertEquals(userAdditionalInformationStore.getPersonalDetails(), loanDemandRequest.getPersonalDetails()),
                            () -> assertEquals(userTransactionalDataStore.getAccountDetails(), loanDemandRequest.getAccountDetails()),
                            () -> assertEquals(userAdditionalInformationStore.getContactData(), loanDemandRequest.getContactData()),
                            () -> assertEquals(userAdditionalInformationStore.getConsentData(), loanDemandRequest.getConsents()),
                            () -> assertEquals(userAdditionalInformationStore.getEmploymentDetails(), loanDemandRequest.getEmploymentDetails()),
                            () -> assertEquals(userTransactionalDataStore.getCustomDACData(), loanDemandRequest.getCustomDACData()),
                            () -> assertEquals(constructIncomeUsingUserInputs(userTransactionalDataStore.getIncome(), userAdditionalInformationStore.getIncome()), loanDemandRequest.getIncome()),
                            () -> assertEquals(constructExpensesUsingUserInputs(userTransactionalDataStore.getExpenses(), userAdditionalInformationStore.getExpenses()), loanDemandRequest.getExpenses()),
                            () -> assertEquals(userTransactionalDataStore.getDacId(), loanDemandRequest.getDacId()),
                            () -> assertEquals(userTransactionalDataStore.getFtsTransactionId(), loanDemandRequest.getFtsTransactionId()),
                            () -> assertEquals(userAdditionalInformationStore.getCreditDetails(), loanDemandRequest.getCreditDetails()),
                            () -> assertEquals(disposableAmount, loanDemandRequest.getDisposableIncome()),
                            () -> verify(expenseMapper).map(argumentCaptor.capture()),
                            () -> assertEquals(userAdditionalInformationStore.getExpenses().getAcknowledgedRent(), argumentCaptor.getValue().getAcknowledgedRent())
                    );
                }).verifyComplete();
    }


    private Income constructIncomeUsingUserInputs(final Income incomeFromTransactions, final Income incomeFromUser) {
        return incomeFromTransactions.toBuilder()
                .incomeDeclared(incomeFromUser.getIncomeDeclared())
                .acknowledgedNetIncome(incomeFromTransactions.getNetIncome())
                .build();
    }

    private Expenses constructExpensesUsingUserInputs(final Expenses expensesFromTransactions, final Expenses expensesFromUser) {
        return expensesFromTransactions.toBuilder()
                .monthlyLoanInstallmentsDeclared(expensesFromUser.getMonthlyLoanInstallmentsDeclared())
                .monthlyLifeCost(expensesFromUser.getMonthlyLifeCost())
                .acknowledgedMortgages(expensesFromTransactions.getMortgages())
                .rent(ofNullable(expensesFromUser.getRent()).orElse(expensesFromTransactions.getRent()))
                .acknowledgedRent(ofNullable(expensesFromUser.getAcknowledgedRent()).orElse(0d))
                .build();
    }

}
