package de.joonko.loan.partner.aion.mapper;

import de.joonko.loan.common.utils.PhoneNumberUtil;
import de.joonko.loan.offer.domain.EmploymentType;
import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.offer.domain.LoanDuration;
import de.joonko.loan.partner.aion.model.*;
import de.joonko.loan.util.BigDecimalUtil;

import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.math.BigDecimal;
import java.util.List;

import static java.util.Optional.ofNullable;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(RandomBeansExtension.class)
class AionLoanProviderApiMapperTest {

    private final AionLoanProviderApiMapper mapper = new AionLoanProviderApiMapper();

    @Test
    void toLoanProviderRequest_mapsAllFieldsAsExpected(@Random LoanDemand loanDemand, @Random LoanDuration loanDuration) {
        // when

        final var mapped = mapper.toLoanProviderRequest(loanDemand, loanDuration);
        final var creditApplicationVariable = mapped.getVariables().stream()
                .filter(variable -> variable.getTransmissionDataType().equals(TransmissionDataType.CREDIT_APPLICATION))
                .findFirst();
        final var personalDataVariable = mapped.getVariables().stream()
                .filter(variable -> variable.getTransmissionDataType().equals(TransmissionDataType.PERSONAL_DATA))
                .findFirst();
        final var consentDataVariable = mapped.getVariables().stream()
                .filter(variable -> variable.getTransmissionDataType().equals(TransmissionDataType.CONSENTS))
                .findFirst();
        final var psd2rawVariable = mapped.getVariables().stream()
                .filter(variable -> variable.getTransmissionDataType().equals(TransmissionDataType.PSD2_RAW))
                .findFirst();

        // then
        assertAll(
                () -> assertEquals(4, mapped.getVariables().size()),
                () -> assertThat(creditApplicationVariable).isNotEmpty(),
                () -> assertThat(personalDataVariable).isNotEmpty(),
                () -> assertThat(consentDataVariable).isNotEmpty(),
                () -> assertThat(psd2rawVariable).isNotEmpty(),
                () -> assertEquals(CreditApplicationTransmissionData.class, creditApplicationVariable.get().getTransmissionData().getClass()),
                () -> assertEquals(loanDemand.getLoanAsked(), ((CreditApplicationTransmissionData) creditApplicationVariable.get().getTransmissionData()).getRequestedAmount()),

                () -> assertEquals(loanDemand.getPersonalDetails().getFinance().getIncome().getSumOfAllIncomes(), ((CreditApplicationTransmissionData) creditApplicationVariable.get().getTransmissionData()).getEstimatedIncome()),
                () -> assertEquals(loanDemand.getPersonalDetails().getFinance().getIncome().getNetIncome(), ((CreditApplicationTransmissionData) creditApplicationVariable.get().getTransmissionData()).getEstimatedNetIncome()),
                () -> assertEquals(loanDemand.getPersonalDetails().getFinance().getIncome().getPensionBenefits(), ((CreditApplicationTransmissionData) creditApplicationVariable.get().getTransmissionData()).getEstimatedPensionBenefits()),
                () -> assertEquals(loanDemand.getPersonalDetails().getFinance().getIncome().getAlimonyPayments(), ((CreditApplicationTransmissionData) creditApplicationVariable.get().getTransmissionData()).getEstimatedAlimonyPayments()),
                () -> assertEquals(loanDemand.getPersonalDetails().getFinance().getIncome().getChildBenefits(), ((CreditApplicationTransmissionData) creditApplicationVariable.get().getTransmissionData()).getEstimatedChildBenefits()),
                () -> assertEquals(loanDemand.getPersonalDetails().getFinance().getIncome().getRentalIncome(), ((CreditApplicationTransmissionData) creditApplicationVariable.get().getTransmissionData()).getEstimatedRentalIncome()),
                () -> assertEquals(loanDemand.getPersonalDetails().getFinance().getIncome().getOtherRevenue(), ((CreditApplicationTransmissionData) creditApplicationVariable.get().getTransmissionData()).getEstimatedOtherRevenue()),
                () -> assertEquals(loanDemand.getPersonalDetails().getFinance().getIncome().getAcknowledgedNetIncome(), ((CreditApplicationTransmissionData) creditApplicationVariable.get().getTransmissionData()).getEstimatedAcknowledgedNetIncome()),
                () -> assertEquals(loanDemand.getPersonalDetails().getFinance().getIncome().getIncomeDeclared(), ((CreditApplicationTransmissionData) creditApplicationVariable.get().getTransmissionData()).getDeclaredSalary()),

                () -> assertEquals(loanDemand.getPersonalDetails().getFinance().getExpenses().getMonthlyLifeCost(), ((CreditApplicationTransmissionData) creditApplicationVariable.get().getTransmissionData()).getDeclaredCostOfLife()),
                () -> assertEquals(loanDemand.getPersonalDetails().getFinance().getExpenses().getMonthlyLoanInstallmentsDeclared(), ((CreditApplicationTransmissionData) creditApplicationVariable.get().getTransmissionData()).getDeclaredInstallmentsAndCreditObligations()),
                () -> assertEquals(loanDemand.getPersonalDetails().getFinance().getDisposableAmount(), ((CreditApplicationTransmissionData) creditApplicationVariable.get().getTransmissionData()).getEstimatedDisposableIncome()),
                () -> assertEquals(loanDemand.getPersonalDetails().getFinance().getExpenses().getAcknowledgedRent(), ((CreditApplicationTransmissionData) creditApplicationVariable.get().getTransmissionData()).getEstimatedRent()),


                () -> assertEquals(loanDemand.getCreditDetails().getCreditCardLimitDeclared(), ((CreditApplicationTransmissionData) creditApplicationVariable.get().getTransmissionData()).getDeclaredCreditCardsLimit()),
                () -> assertEquals(loanDemand.getFundingPurpose(), ((CreditApplicationTransmissionData) creditApplicationVariable.get().getTransmissionData()).getPurposeOfLoan()),
                () -> assertEquals(loanDemand.getCreditDetails().getBonimaScore().toString(), ((CreditApplicationTransmissionData) creditApplicationVariable.get().getTransmissionData()).getBonimaScore()),
                () -> assertNull(((CreditApplicationTransmissionData) creditApplicationVariable.get().getTransmissionData()).getSchufaClass()),
                () -> assertEquals(loanDemand.getCreditDetails().getProbabilityOfDefault(), ((CreditApplicationTransmissionData) creditApplicationVariable.get().getTransmissionData()).getProbabilityOfDefault()),
                () -> assertEquals(loanDemand.getCreditDetails().getIsCurrentDelayInInstallmentsDeclared(), ((CreditApplicationTransmissionData) creditApplicationVariable.get().getTransmissionData()).getHasDeclaredCurrentDelaysInInstallments()),

                () -> assertEquals(ofNullable(loanDemand.getCustomDACData().getWasDelayInInstallments40DaysDiff()).orElse(false), ((CreditApplicationTransmissionData) creditApplicationVariable.get().getTransmissionData()).getHadDelayInInstallmentsInLast12M40DaysDiff()),
                () -> assertEquals(ofNullable(loanDemand.getCustomDACData().getWasDelayInInstallments62DaysDiff()).orElse(false), ((CreditApplicationTransmissionData) creditApplicationVariable.get().getTransmissionData()).getHadDelayInInstallmentsInLast12M62DaysDiff()),
                () -> assertEquals(ofNullable(loanDemand.getCustomDACData().getIsCurrentDelayInInstallments()).orElse(false), ((CreditApplicationTransmissionData) creditApplicationVariable.get().getTransmissionData()).getHasDelayInInstallments()),

                () -> assertEquals(loanDemand.getEmploymentDetails().getEmploymentSince(), ((CreditApplicationTransmissionData) creditApplicationVariable.get().getTransmissionData()).getEmploymentDate()),
                () -> assertEquals(loanDemand.getEmploymentDetails().getEmployerName(), ((CreditApplicationTransmissionData) creditApplicationVariable.get().getTransmissionData()).getEmployerName()),
                () -> assertEquals(loanDemand.getEmploymentDetails().getEmploymentType() == EmploymentType.REGULAR_EMPLOYED ? Occupation.EMPLOYEE : Occupation.UNEMPLOYED, ((CreditApplicationTransmissionData) creditApplicationVariable.get().getTransmissionData()).getOccupation()),

                () -> assertEquals(loanDemand.getPersonalDetails().getFinance().getExpenses().getAcknowledgedMortgages()
                        .add(loanDemand.getPersonalDetails().getFinance().getExpenses().getLoanInstalments()), ((CreditApplicationTransmissionData) creditApplicationVariable.get().getTransmissionData()).getEstimatedInstallmentsAndCreditObligations()),
                () -> assertEquals(loanDemand.getPersonalDetails().getPlaceOfBirth(), ((CreditApplicationTransmissionData) creditApplicationVariable.get().getTransmissionData()).getPlaceOfBirth()),
                () -> assertEquals(loanDemand.getPersonalDetails().getBirthDate(), ((CreditApplicationTransmissionData) creditApplicationVariable.get().getTransmissionData()).getBirthDate()),
                () -> assertEquals(loanDemand.getPersonalDetails().getNationality().getCountryCode().getAlpha2(), ((CreditApplicationTransmissionData) creditApplicationVariable.get().getTransmissionData()).getNationality()),
                () -> assertEquals(SourceOfFunds.WORK_SALARY, ((CreditApplicationTransmissionData) creditApplicationVariable.get().getTransmissionData()).getSourceOfFunds()),
                () -> assertEquals(loanDemand.getPersonalDetails().getTaxId(), ((CreditApplicationTransmissionData) creditApplicationVariable.get().getTransmissionData()).getTin()),
                () -> assertEquals(loanDemand.getPersonalDetails().getNumberOfDependants(), ((CreditApplicationTransmissionData) creditApplicationVariable.get().getTransmissionData()).getNumberOfDependants()),
                () -> assertEquals(loanDemand.getPersonalDetails().getHousingType().name(), ((CreditApplicationTransmissionData) creditApplicationVariable.get().getTransmissionData()).getFlatStatus()),
                () -> assertEquals(loanDemand.getPersonalDetails().getCountryOfBirth(), ((CreditApplicationTransmissionData) creditApplicationVariable.get().getTransmissionData()).getCountryOfBirth()),
                () -> assertEquals(loanDemand.getPersonalDetails().getGender().name(), ((CreditApplicationTransmissionData) creditApplicationVariable.get().getTransmissionData()).getGender().name()),

                () -> assertEquals(BigDecimalUtil.average(List.of(loanDemand.getCustomDACData().getSumIncomes1MAgo(), loanDemand.getCustomDACData().getSumIncomes2MAgo(), loanDemand.getCustomDACData().getSumIncomes3MAgo())).orElse(BigDecimal.ZERO), ((CreditApplicationTransmissionData) creditApplicationVariable.get().getTransmissionData()).getEstimatedAverageIncomeInLast3M()),
                () -> assertEquals(loanDemand.getCustomDACData().getSumIncomes1MAgo(), ((CreditApplicationTransmissionData) creditApplicationVariable.get().getTransmissionData()).getLastSalary()),
                () -> assertEquals(loanDemand.getCustomDACData().getSumIncomes2MAgo(), ((CreditApplicationTransmissionData) creditApplicationVariable.get().getTransmissionData()).getSalary2MAgo()),
                () -> assertEquals(loanDemand.getCustomDACData().getSumIncomes3MAgo(), ((CreditApplicationTransmissionData) creditApplicationVariable.get().getTransmissionData()).getSalary3MAgo()),
                () -> assertEquals(loanDemand.getCustomDACData().getHasSalaryEachMonthLast3M(), ((CreditApplicationTransmissionData) creditApplicationVariable.get().getTransmissionData()).getHas3MSalary()),

                () -> assertEquals(PersonalDataTransmissionData.class, personalDataVariable.get().getTransmissionData().getClass()),
                () -> assertEquals(loanDemand.getPersonalDetails().getFirstName(), ((PersonalDataTransmissionData) personalDataVariable.get().getTransmissionData()).getFirstName()),
                () -> assertEquals(loanDemand.getPersonalDetails().getLastName(), ((PersonalDataTransmissionData) personalDataVariable.get().getTransmissionData()).getLastName()),
                () -> assertEquals(loanDemand.getContactData().getEmail().getEmailString(), ((PersonalDataTransmissionData) personalDataVariable.get().getTransmissionData()).getEmail()),
                () -> assertEquals(PhoneNumberUtil.extractPrefixFromPhoneNumberWithout0(loanDemand.getContactData().getMobile()), ((PersonalDataTransmissionData) personalDataVariable.get().getTransmissionData()).getMobilePhoneNumber()),
                () -> assertEquals("49", ((PersonalDataTransmissionData) personalDataVariable.get().getTransmissionData()).getPhonePrefix()),

                () -> assertEquals(ConsentDataTransmissionData.class, consentDataVariable.get().getTransmissionData().getClass()),

                () -> assertNull(psd2rawVariable.get().getTransmissionData())
        );
    }

    @Test
    void fromLoanProviderResponse_returnsEmptyList(@Random CreditApplicationResponse response) {
        final var mapped = mapper.fromLoanProviderResponse(response);

        assertEquals(0, mapped.size());
    }

}