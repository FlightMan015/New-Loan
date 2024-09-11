package de.joonko.loan.offer.domain;

import de.joonko.loan.offer.api.CustomDACData;
import de.joonko.loan.offer.api.LoanDemandRequest;
import de.joonko.loan.offer.api.PersonalDetails;
import de.joonko.loan.integrations.domain.integrationhandler.userpersonal.mapper.ExpenseMapper;
import de.joonko.loan.integrations.domain.integrationhandler.userpersonal.mapper.IncomeMapper;
import de.joonko.loan.offer.testdata.LoanDemandTestData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static de.joonko.loan.offer.domain.ExpenseModel.expense;
import static de.joonko.loan.offer.domain.IncomeModel.income;
import static org.junit.jupiter.api.Assertions.assertEquals;


@ActiveProfiles("integration")
@ExtendWith({MockitoExtension.class})
@SpringBootTest
public class LoanRecommendationEngineIT extends LoanDemandTestData {

    @Autowired
    private LoanRecommendationEngine loanRecommendationEngine;

    @Autowired
    private IncomeMapper incomeMapper;

    @Autowired
    private ExpenseMapper expenseMapper;

    @Test
    void recommend_method_gives_correct_recommendation_when_disposable_amount_is_smaller() {
        // given
        final var income = incomeMapper.map(income());
        final var expense = expenseMapper.map(expense());
        final var disposableAmount = DisposableAmountCalculator.calculateDisposableAmount(income(), expense(), 2, 1);
        final var loanDemandRequest = LoanDemandRequest.builder()
                .income(income)
                .expenses(expense)
                .disposableIncome(disposableAmount)
                .loanAsked(100000)
                .personalDetails(PersonalDetails.builder().numberOfChildren(2).build())
                .customDACData(CustomDACData.builder().carInformation(true).build())
                .build();

        // when
        final var recommendedLoanDemandRequests = loanRecommendationEngine.recommend(loanDemandRequest);
        // then
        assertEquals(1, recommendedLoanDemandRequests.size());
        assertEquals(32500, recommendedLoanDemandRequests.stream().findFirst().get().getLoanAsked());
    }

    @Test
    void recommend_method_gives_correct_recommendation_when_per_percent_recommendation_is_smaller() {
        // given
        final var income = incomeMapper.map(income());
        final var expense = expenseMapper.map(expense());
        final var disposableAmount = DisposableAmountCalculator.calculateDisposableAmount(income(), expense(), 2, 1);

        final var loanDemandRequest = LoanDemandRequest.builder()
                .loanAsked(50000)
                .income(income)
                .expenses(expense)
                .disposableIncome(disposableAmount)
                .personalDetails(PersonalDetails.builder().numberOfChildren(2).build())
                .customDACData(CustomDACData.builder().carInformation(true).build())
                .build();
        // when
        final var recommendedLoanDemandRequests = loanRecommendationEngine.recommend(loanDemandRequest);
        // then
        assertEquals(1, recommendedLoanDemandRequests.size());
        assertEquals(25000, recommendedLoanDemandRequests.stream().findFirst().get().getLoanAsked());
    }
}
