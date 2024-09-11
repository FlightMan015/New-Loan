package de.joonko.loan.partner.aion.mapper;

import de.joonko.loan.common.utils.PhoneNumberUtil;
import de.joonko.loan.offer.domain.Gender;
import de.joonko.loan.offer.domain.*;
import de.joonko.loan.partner.aion.model.*;
import de.joonko.loan.user.service.persistence.domain.ConsentState;
import de.joonko.loan.util.BigDecimalUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static de.joonko.loan.util.DateUtil.formatForAionFromInstant;
import static java.util.Optional.ofNullable;

@Component
@Slf4j
@RequiredArgsConstructor
public class AionLoanProviderApiMapper implements LoanProviderApiMapper<CreditApplicationRequest, CreditApplicationResponse> {

    @Override
    public CreditApplicationRequest toLoanProviderRequest(LoanDemand loanDemand, LoanDuration loanDuration) {
        return CreditApplicationRequest.builder()
                .variables(List.of(
                        CreditApplicationRequest.Variable.builder()
                                .transmissionDataType(TransmissionDataType.CREDIT_APPLICATION)
                                .transmissionData(buildCreditApplicationTransmissionData(loanDemand))
                                .build(),

                        CreditApplicationRequest.Variable.builder()
                                .transmissionDataType(TransmissionDataType.PERSONAL_DATA)
                                .transmissionData(buildPersonalDataTransmissionData(loanDemand))
                                .build(),

                        CreditApplicationRequest.Variable.builder()
                                .transmissionDataType(TransmissionDataType.CONSENTS)
                                .transmissionData(buildConsentsTransmissionData(loanDemand))
                                .build(),

                        CreditApplicationRequest.Variable.builder()
                                .transmissionDataType(TransmissionDataType.PSD2_RAW)
                                .build()
                ))
                .build();
    }

    @Override
    public List<LoanOffer> fromLoanProviderResponse(final CreditApplicationResponse response) {
        return List.of();
    }

    private PersonalDataTransmissionData buildPersonalDataTransmissionData(final LoanDemand loanDemand) {
        return PersonalDataTransmissionData.builder()
                .firstName(loanDemand.getPersonalDetails().getFirstName())
                .lastName(loanDemand.getPersonalDetails().getLastName())
                .email(loanDemand.getContactData().getEmail().getEmailString())
                .mobilePhoneNumber(PhoneNumberUtil.extractPrefixFromPhoneNumberWithout0(loanDemand.getContactData().getMobile()))
                .build();
    }

    private ConsentDataTransmissionData buildConsentsTransmissionData(final LoanDemand loanDemand) {
        return ConsentDataTransmissionData.builder()
                .aionPrivacyPolicyConsent(ConsentValue.builder() // TODO: TBD by AION
                        .isAccepted(true)
                        .timestamp(formatForAionFromInstant(Instant.now()))
                        .build())
                .dataTransferConsent(ConsentValue.builder()
                        .isAccepted(true)
                        .timestamp(formatForAionFromInstant(Instant.now()))
                        .build())
                .marketingConsent(loanDemand.getConsents()
                        .stream()
                        .findFirst() // As currently either all consents are accepted or rejected
                        .map(consent -> ConsentValue.builder()
                                .isAccepted(ConsentState.ACCEPTED.equals(consent.getConsentState()))
                                .timestamp(formatForAionFromInstant(consent.getLastUpdatedTimestamp()))
                                .build())
                        .orElse(ConsentValue.builder()
                                .isAccepted(false)
                                .timestamp(formatForAionFromInstant(Instant.now()))
                                .build()))
                .build();
    }

    private CreditApplicationTransmissionData buildCreditApplicationTransmissionData(final LoanDemand loanDemand) {
        return CreditApplicationTransmissionData.builder()
                .civilStatus(mapMaritalStatus(loanDemand.getPersonalDetails().getFamilyStatus()))
                .numberOfDependants(loanDemand.getPersonalDetails().getNumberOfDependants())
                .declaredCostOfLife(loanDemand.getPersonalDetails().getFinance().getExpenses().getMonthlyLifeCost())
                .estimatedRent(loanDemand.getPersonalDetails().getFinance().getExpenses().getAcknowledgedRent())
                .flatStatus(loanDemand.getPersonalDetails().getHousingType().name())
                .estimatedIncome(loanDemand.getPersonalDetails().getFinance().getIncome().getSumOfAllIncomes())
                .declaredSalary(loanDemand.getPersonalDetails().getFinance().getIncome().getIncomeDeclared())
                .estimatedNetIncome(loanDemand.getPersonalDetails().getFinance().getIncome().getNetIncome())
                .estimatedPensionBenefits(loanDemand.getPersonalDetails().getFinance().getIncome().getPensionBenefits())
                .estimatedAlimonyPayments(loanDemand.getPersonalDetails().getFinance().getIncome().getAlimonyPayments())
                .estimatedChildBenefits(loanDemand.getPersonalDetails().getFinance().getIncome().getChildBenefits())
                .estimatedRentalIncome(loanDemand.getPersonalDetails().getFinance().getIncome().getRentalIncome())
                .estimatedDisposableIncome(loanDemand.getPersonalDetails().getFinance().getDisposableAmount())
                .estimatedOtherRevenue(loanDemand.getPersonalDetails().getFinance().getIncome().getOtherRevenue())
                .estimatedAcknowledgedNetIncome(loanDemand.getPersonalDetails().getFinance().getIncome().getAcknowledgedNetIncome())
                .estimatedAlimonyIncome(loanDemand.getPersonalDetails().getFinance().getIncome().getAlimonyPayments())

                .employmentDate(loanDemand.getEmploymentDetails().getEmploymentSince())
                .employerName(loanDemand.getEmploymentDetails().getEmployerName())
                .declaredInstallmentsAndCreditObligations(loanDemand.getPersonalDetails().getFinance().getExpenses().getMonthlyLoanInstallmentsDeclared())
                .estimatedInstallmentsAndCreditObligations(calculateCreditObligations(loanDemand))
                .declaredCreditCardsLimit(ofNullable(loanDemand.getCreditDetails()).map(CreditDetails::getCreditCardLimitDeclared).orElse(null))
                .purposeOfLoan(loanDemand.getFundingPurpose())
                .bonimaScore(ofNullable(loanDemand.getCreditDetails()).flatMap(creditDetails -> ofNullable(creditDetails.getBonimaScore()).map(Object::toString)).orElse(null))
                .countryOfBirth(loanDemand.getPersonalDetails().getCountryOfBirth())
                .placeOfBirth(loanDemand.getPersonalDetails().getPlaceOfBirth())
                .birthDate(loanDemand.getPersonalDetails().getBirthDate())
                .nationality(loanDemand.getPersonalDetails().getNationality().getCountryCode().getAlpha2())
                .gender(mapGender(loanDemand.getPersonalDetails().getGender()))
                .occupation(mapOccupation(loanDemand.getEmploymentDetails().getEmploymentType()))
                .sourceOfFunds(SourceOfFunds.WORK_SALARY)
                .tin(loanDemand.getPersonalDetails().getTaxId())
                .requestedAmount(loanDemand.getLoanAsked())
                .schufaClass(null)
                .hasDelayInInstallments(ofNullable(loanDemand.getCustomDACData().getIsCurrentDelayInInstallments()).orElse(false))
                .hadDelayInInstallmentsInLast12M40DaysDiff(ofNullable(loanDemand.getCustomDACData().getWasDelayInInstallments40DaysDiff()).orElse(false))
                .hadDelayInInstallmentsInLast12M62DaysDiff(ofNullable(loanDemand.getCustomDACData().getWasDelayInInstallments62DaysDiff()).orElse(false))
                .hasDeclaredCurrentDelaysInInstallments(loanDemand.getCreditDetails().getIsCurrentDelayInInstallmentsDeclared())
                .probabilityOfDefault(ofNullable(loanDemand.getCreditDetails()).map(CreditDetails::getProbabilityOfDefault).orElse(null))
                .estimatedAverageIncomeInLast3M(calculateLast3mAverageIncome(loanDemand))
                .has3MSalary(loanDemand.getCustomDACData().getHasSalaryEachMonthLast3M())
                .lastSalary(loanDemand.getCustomDACData().getSumIncomes1MAgo())
                .salary2MAgo(loanDemand.getCustomDACData().getSumIncomes2MAgo())
                .salary3MAgo(loanDemand.getCustomDACData().getSumIncomes3MAgo())

                .requestOriginIp(ofNullable(loanDemand.getRequestIp()).orElse(""))
                .requestOriginCountry(ofNullable(loanDemand.getRequestCountryCode()).orElse(""))
                .residentialAddress(CreditApplicationTransmissionData.ResidentialAddress.builder()
                        .country("DE")
                        .city(loanDemand.getContactData().getCity())
                        .street(loanDemand.getContactData().getStreetName())
                        .buildingNumber(loanDemand.getContactData().getStreetNumber())
                        .postalCode(loanDemand.getContactData().getZipCode().getCode())
                        .build())
                .build();
    }

    private BigDecimal calculateLast3mAverageIncome(final LoanDemand loanDemand) {
        return BigDecimalUtil.average(List.of(
                        ofNullable(loanDemand.getCustomDACData().getSumIncomes1MAgo()).orElse(BigDecimal.ZERO),
                        ofNullable(loanDemand.getCustomDACData().getSumIncomes2MAgo()).orElse(BigDecimal.ZERO),
                        ofNullable(loanDemand.getCustomDACData().getSumIncomes3MAgo()).orElse(BigDecimal.ZERO)))
                .orElse(BigDecimal.ZERO);
    }

    private Occupation mapOccupation(final EmploymentType employmentType) {
        return employmentType == EmploymentType.REGULAR_EMPLOYED ? Occupation.EMPLOYEE : Occupation.UNEMPLOYED;
    }

    private de.joonko.loan.partner.aion.model.Gender mapGender(final de.joonko.loan.offer.domain.Gender gender) {
        if (gender == Gender.MALE) {
            return de.joonko.loan.partner.aion.model.Gender.MALE;
        } else if (gender == Gender.FEMALE) {
            return de.joonko.loan.partner.aion.model.Gender.FEMALE;
        }
        return de.joonko.loan.partner.aion.model.Gender.UNSPECIFIED;
    }

    private BigDecimal calculateCreditObligations(final LoanDemand loanDemand) {
        return loanDemand.getPersonalDetails().getFinance().getExpenses().getAcknowledgedMortgages()
                .add(loanDemand.getPersonalDetails().getFinance().getExpenses().getLoanInstalments());
    }

    private MaritalStatus mapMaritalStatus(final FamilyStatus familyStatus) {
        switch (familyStatus) {
            case SINGLE:
                return MaritalStatus.SINGLE;
            case MARRIED:
                return MaritalStatus.MARRIED;
            case DIVORCED:
                return MaritalStatus.DIVORCED;
            case WIDOWED:
                return MaritalStatus.WIDOWED;
            case LIVING_SEPARATELY:
                return MaritalStatus.SEPARATED;
            case LIVING_IN_LONGTERM_RELATIONSHIP:
                return MaritalStatus.LEGALLY_COHABITATING;
        }
        return null;
    }
}
