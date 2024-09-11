package de.joonko.loan.data.support.mapper;

import de.joonko.loan.avro.dto.loan_demand.LoanDemandMessage;
import de.joonko.loan.offer.api.LoanDemandRequest;
import de.joonko.loan.user.service.persistence.domain.ConsentData;
import de.joonko.loan.user.service.persistence.domain.ConsentState;
import de.joonko.loan.user.service.persistence.domain.ConsentType;
import io.github.glytching.junit.extension.random.Random;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ContextConfiguration(classes = {
        LoanDemandMapperImpl.class})
class LoanDemandMapperTest extends MapstructBaseTest {

    @Autowired
    private LoanDemandMapper loanDemandMapper;

    @Test
    void should_map_to_loan_demand_message(@Random LoanDemandRequest loanDemandRequest) {
        LoanDemandMessage loanDemandMessage = loanDemandMapper.mapLoanDemand(123L, loanDemandRequest);
        assertAll(
                () -> assertEquals(loanDemandMessage.getEmploymentDetails().getCity(), loanDemandRequest.getEmploymentDetails().getCity()),
                () -> assertEquals(loanDemandMessage.getUserUUID(), loanDemandRequest.getUserUUID()),
                () -> assertNotNull(loanDemandMessage.getTimestamp()),
                () -> assertEquals(loanDemandMessage.getUserId(), 123L),
                () -> assertEquals(loanDemandMessage.getAskedForBonifyLoans(), loanDemandRequest.getIsRequestedBonifyLoans()),
                () -> assertEquals(loanDemandMessage.getApplicationId(), loanDemandRequest.getApplicationId()),
                () -> assertEquals(loanDemandMessage.getParentApplicationId(), loanDemandRequest.getParentApplicationId()),
                () -> assertEquals(loanDemandMessage.getPersonalDetails().getBirthDate(), loanDemandRequest.getPersonalDetails().getBirthDate().toEpochSecond(LocalTime.NOON, ZoneOffset.UTC)),
                () -> assertEquals(loanDemandMessage.getIncome().getAcknowledgedNetIncome(), loanDemandRequest.getIncome().getAcknowledgedNetIncome().floatValue(), 0.0)
        );

    }

    @Test
    void should_map_prechecks_loan_demand_message(@Random LoanDemandRequest loanDemandRequest) {
        LoanDemandMessage loanDemandMessage = loanDemandMapper.mapLoanDemand(123L, loanDemandRequest);
        assertAll(
                () -> assertEquals(loanDemandRequest.getPreChecks().size(), loanDemandMessage.getPreChecks().size()),
                () -> loanDemandRequest.getPreChecks().forEach(precheck ->
                        assertTrue(loanDemandMessage.getPreChecks().stream().anyMatch(mappedPrecheck ->
                                mappedPrecheck.getProvider().equals(precheck.getProvider()) &&
                                        mappedPrecheck.getPreCheck().equals(precheck.getPreCheck().name()) &&
                                        mappedPrecheck.getValue().equals(precheck.getValue())
                        ))
                ),
                () -> assertEquals(loanDemandRequest.getCustomDACData().getCountEncashmentTag(), loanDemandMessage.getCustomDACData().getCountEncashmentTag()),
                () -> assertEquals(loanDemandRequest.getCustomDACData().getCountSeizureTag(), loanDemandMessage.getCustomDACData().getCountSeizureTag()),
                () -> assertEquals(loanDemandRequest.getCustomDACData().getCountPAccountTag(), loanDemandMessage.getCustomDACData().getCountPAccountTag()),
                () -> assertEquals(loanDemandRequest.getCustomDACData().getCountChargebackTag(), loanDemandMessage.getCustomDACData().getCountChargebackTag())
        );

    }

    @Test
    void should_map_new_fields(@Random LoanDemandRequest loanDemandRequest) {
        LoanDemandMessage loanDemandMessage = loanDemandMapper.mapLoanDemand(123L, loanDemandRequest);
        assertAll(
                () -> assertEquals(loanDemandRequest.getDisposableIncome().floatValue(), loanDemandMessage.getDisposableIncome()),
                () -> assertEquals(loanDemandRequest.getPersonalDetails().getNumberOfDependants(), loanDemandMessage.getPersonalDetails().getNumberOfDependants()),
                () -> assertEquals(loanDemandRequest.getPersonalDetails().getCountryOfBirth(), loanDemandMessage.getPersonalDetails().getCountryOfBirth()),
                () -> assertEquals(loanDemandRequest.getCustomDACData().getSumIncomes1MAgo().floatValue(), loanDemandMessage.getCustomDACData().getSumIncomes1MAgo()),
                () -> assertEquals(loanDemandRequest.getCustomDACData().getSumIncomes2MAgo().floatValue(), loanDemandMessage.getCustomDACData().getSumIncomes2MAgo()),
                () -> assertEquals(loanDemandRequest.getCustomDACData().getSumIncomes3MAgo().floatValue(), loanDemandMessage.getCustomDACData().getSumIncomes3MAgo()),
                () -> assertEquals(loanDemandRequest.getCustomDACData().getHasSalaryEachMonthLast3M(), loanDemandMessage.getCustomDACData().getHasSalaryEachMonthLast3M()),
                () -> assertEquals(loanDemandRequest.getCustomDACData().getIsCurrentDelayInInstallments(), loanDemandMessage.getCustomDACData().getIsCurrentDelayInInstallments()),
                () -> assertEquals(loanDemandRequest.getCustomDACData().getWasDelayInInstallments62DaysDiff(), loanDemandMessage.getCustomDACData().getWasDelayInInstallments62DaysDiff()),
                () -> assertEquals(loanDemandRequest.getCustomDACData().getWasDelayInInstallments40DaysDiff(), loanDemandMessage.getCustomDACData().getWasDelayInInstallments40DaysDiff()),
                () -> assertEquals(loanDemandRequest.getIncome().getIncomeDeclared().floatValue(), loanDemandMessage.getIncome().getIncomeDeclared()),
                () -> assertEquals(loanDemandRequest.getExpenses().getMonthlyLifeCost().floatValue(), loanDemandMessage.getExpenses().getMonthlyLifeCost()),
                () -> assertEquals(loanDemandRequest.getExpenses().getMonthlyLoanInstallmentsDeclared().floatValue(), loanDemandMessage.getExpenses().getMonthlyLoanInstallmentsDeclared()),
                () -> assertEquals(loanDemandRequest.getCreditDetails().getBonimaScore(), loanDemandMessage.getCreditDetails().getBonimaScore()),
                () -> assertEquals(loanDemandRequest.getCreditDetails().getEstimatedSchufaClass(), loanDemandMessage.getCreditDetails().getEstimatedSchufaClass()),
                () -> assertEquals(loanDemandRequest.getCreditDetails().getCreditCardLimitDeclared().floatValue(), loanDemandMessage.getCreditDetails().getCreditCardLimitDeclared()),
                () -> assertEquals(loanDemandRequest.getCreditDetails().getProbabilityOfDefault().floatValue(), loanDemandMessage.getCreditDetails().getProbabilityOfDefault()),
                () -> assertEquals(loanDemandRequest.getCreditDetails().getIsCurrentDelayInInstallmentsDeclared(), loanDemandMessage.getCreditDetails().getIsCurrentDelayInInstallmentsDeclared())
        );

    }

    @Test
    void should_map_consents_correctly_to_loan_demand_message(@Random LoanDemandRequest loanDemandRequest) {
        // given
        loanDemandRequest.setConsents(List.of(ConsentData.builder()
                .consentType(ConsentType.EMAIL)
                .consentState(ConsentState.ACCEPTED)
                .clientIP("111")
                .lastUpdatedTimestamp(Instant.now())
                .build(), ConsentData.builder()
                .consentType(ConsentType.SMS)
                .consentState(ConsentState.REVOKED)
                .clientIP("222")
                .lastUpdatedTimestamp(Instant.now())
                .build(), ConsentData.builder()
                .consentType(ConsentType.LETTER)
                .consentState(ConsentState.DECLINED)
                .clientIP("333")
                .lastUpdatedTimestamp(Instant.now())
                .build(), ConsentData.builder()
                .consentType(ConsentType.PHONE)
                .consentState(ConsentState.NONE)
                .clientIP("444")
                .lastUpdatedTimestamp(Instant.now())
                .build()));

        final var mappedConsents = loanDemandMapper.mapLoanDemand(123L, loanDemandRequest).getConsents();

        assertAll(
                () -> assertEquals(4, mappedConsents.size()),
                () -> loanDemandRequest.getConsents().forEach(consent -> {
                    assertTrue(mappedConsents.stream()
                            .anyMatch(mappedConsent -> mappedConsent.getConsentType().name().equals(consent.getConsentType().name())
                                    && mappedConsent.getConsentState().name().equals(consent.getConsentState().name())
                            ));
                })
        );
    }


}