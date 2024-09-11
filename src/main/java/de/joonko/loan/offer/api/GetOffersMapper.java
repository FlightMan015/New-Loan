package de.joonko.loan.offer.api;

import de.joonko.loan.common.CollectionUtil;
import de.joonko.loan.db.vo.LoanOfferStore;
import de.joonko.loan.offer.domain.CreditDetails;
import de.joonko.loan.offer.domain.CustomDACData;
import de.joonko.loan.offer.domain.EmploymentDetails;
import de.joonko.loan.offer.domain.EmploymentType;
import de.joonko.loan.offer.domain.Nationality;
import de.joonko.loan.offer.domain.PersonalDetails;
import de.joonko.loan.offer.domain.PreviousAddress;
import de.joonko.loan.offer.domain.*;
import de.joonko.loan.user.api.model.Consent;
import de.joonko.loan.user.service.mapper.ConsentMapper;
import de.joonko.loan.user.service.persistence.domain.ConsentData;

import org.mapstruct.Mapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static de.joonko.loan.offer.domain.DomainDefault.DEFAULT_LOAN_CATEGORY;
import static de.joonko.loan.offer.domain.DomainDefault.LOAN_DURATION;
import static java.util.Optional.ofNullable;

@Mapper(componentModel = "spring")
public interface GetOffersMapper {

    LoanOffer toResponse(de.joonko.loan.offer.domain.LoanOffer loanOffer);

    default LoanOfferStore toStore(de.joonko.loan.offer.domain.LoanOffer loanOffer, String userUUID, String applicationId, String parentApplicationId) {
        return LoanOfferStore.builder()
                .offer(toResponse(loanOffer))
                .userUUID(userUUID)
                .applicationId(applicationId)
                .parentApplicationId(parentApplicationId)
                .loanProviderReferenceNumber(loanOffer.getLoanProviderOfferId())
                .build();
    }

    default LoanDemand fromRequest(LoanDemandRequest request, String loanApplicationId, String userUUID) {
        return new LoanDemand(
                loanApplicationId,
                request.getLoanAsked(),
                request.getFundingPurpose(),
                LOAN_DURATION,
                DEFAULT_LOAN_CATEGORY,
                getPersonalDetails(request),
                getCreditDetails(request.getCreditDetails()),
                getEmploymentDetails(request),
                getContactData(request.getContactData()),
                DigitalAccountStatements.fakeValue(request.getAccountDetails()),
                request.getFtsTransactionId(),
                request.getDacId(),
                request.getRequestIp(),
                request.getRequestCountryCode(),
                getCustomDACData(request.getCustomDACData()),
                request.getConsents(),
                userUUID);

    }

    private CreditDetails getCreditDetails(final de.joonko.loan.offer.api.CreditDetails creditDetails) {
        if (creditDetails == null) {
            return null;
        }

        return CreditDetails.builder()
                .probabilityOfDefault(Objects.nonNull(creditDetails.getProbabilityOfDefault()) ? BigDecimal.valueOf(creditDetails.getProbabilityOfDefault()) : null)
                .estimatedSchufaClass(creditDetails.getEstimatedSchufaClass())
                .bonimaScore(creditDetails.getBonimaScore())
                .creditCardLimitDeclared(Objects.nonNull(creditDetails.getCreditCardLimitDeclared()) ? BigDecimal.valueOf(creditDetails.getCreditCardLimitDeclared()) : null)
                .isCurrentDelayInInstallmentsDeclared(creditDetails.getIsCurrentDelayInInstallmentsDeclared())
                .build();
    }

    private List<ConsentData> getConsents(final List<Consent> consents, final String clientIP) {
        return CollectionUtil.<Consent, ConsentData>mapList(consent -> ConsentMapper.map(consent, clientIP)).apply(consents);
    }

    private de.joonko.loan.offer.domain.ContactData getContactData(ContactData contactData) {
        return new de.joonko.loan.offer.domain.ContactData(contactData.getCity(), contactData.getStreetName(), contactData.getHouseNumber(), new ZipCode(contactData.getPostCode()), LocalDate.of(contactData.getLivingSince()
                .getYear(), contactData.getLivingSince()
                .getMonth(), 1), getPreviousAddress(contactData.getPreviousAddress()), new Email(contactData.getEmail()), contactData.getMobile());
    }


    private PreviousAddress getPreviousAddress(de.joonko.loan.offer.api.PreviousAddress previousAddress) {

        if (null != previousAddress) {
            return PreviousAddress.builder()
                    .street(previousAddress.getStreetName())
                    .postCode(previousAddress.getPostCode())
                    .city(previousAddress.getCity())
                    .country(Nationality.valueOf(previousAddress.getCountry()
                            .name()))
                    .houseNumber(previousAddress.getHouseNumber())
                    .livingSince(LocalDate.of(previousAddress.getLivingSince().getYear(), previousAddress.getLivingSince().getMonth(), 1))
                    .build();
        }
        return null;
    }

    private EmploymentDetails getEmploymentDetails(LoanDemandRequest request) {
        if (de.joonko.loan.offer.api.EmploymentType.REGULAR_EMPLOYED.equals(request.getEmploymentDetails()
                .getEmploymentType())) {
            return EmploymentDetails.builder()
                    .city(request.getEmploymentDetails()
                            .getCity())
                    .employerName(request.getEmploymentDetails()
                            .getEmployerName())
                    .employmentSince(LocalDate.of(request.getEmploymentDetails()
                                    .getEmploymentSince()
                                    .getYear(),
                            request.getEmploymentDetails()
                                    .getEmploymentSince()
                                    .getMonth(), 1))
                    .employmentType(EmploymentType.valueOf(request.getEmploymentDetails()
                            .getEmploymentType()
                            .name()))
                    .streetName(request.getEmploymentDetails()
                            .getStreetName())
                    .zipCode(new ZipCode(request.getEmploymentDetails()
                            .getPostCode()))
                    .professionEndDate(getProfessionEndDateValue(request.getEmploymentDetails()))
                    .houseNumber(request.getEmploymentDetails().getHouseNumber())
                    .build();
        } else {
            return EmploymentDetails.builder()
                    .employmentType(EmploymentType.valueOf(request.getEmploymentDetails()
                            .getEmploymentType()
                            .name()))
                    .build();
        }

    }

    private de.joonko.loan.offer.domain.PersonalDetails getPersonalDetails(LoanDemandRequest loanDemandRequest) {
        final var income = mapIncomeApiModelToDomainModel(loanDemandRequest.getIncome());
        final var expenses = mapExpenseApiModelToDomainModel(loanDemandRequest.getExpenses());
        return PersonalDetails.builder()
                .birthDate(loanDemandRequest.getPersonalDetails()
                        .getBirthDate())
                .finance(new Finance(income, expenses, loanDemandRequest.getDisposableIncome()))
                .familyStatus(de.joonko.loan.offer.domain.FamilyStatus.valueOf(loanDemandRequest.getPersonalDetails()
                        .getFamilyStatus()
                        .name()))
                .firstName(loanDemandRequest.getPersonalDetails()
                        .getFirstName())
                .gender(de.joonko.loan.offer.domain.Gender.valueOf(loanDemandRequest.getPersonalDetails()
                        .getGender()
                        .name()))
                .housingType(de.joonko.loan.offer.domain.HousingType.valueOf(loanDemandRequest.getPersonalDetails()
                        .getHousingType()
                        .name()))
                .lastName(loanDemandRequest.getPersonalDetails()
                        .getLastName())
                .mainEarner(DomainDefault.MAIN_EARNER)
                .nationality(Nationality.valueOf(loanDemandRequest.getPersonalDetails()
                        .getNationality()
                        .name()))
                .numberOfChildren(loanDemandRequest.getPersonalDetails()
                        .getNumberOfChildren())
                .numberOfDependants(loanDemandRequest.getPersonalDetails()
                        .getNumberOfDependants())
                .numberOfCreditCard(loanDemandRequest.getPersonalDetails()
                        .getNumberOfCreditCard())
                .placeOfBirth(loanDemandRequest.getPersonalDetails().getPlaceOfBirth())
                .countryOfBirth(loanDemandRequest.getPersonalDetails().getCountryOfBirth())
                .taxId(loanDemandRequest.getPersonalDetails().getTaxId())
                .build();
    }

    private de.joonko.loan.offer.domain.Expenses mapExpenseApiModelToDomainModel(Expenses loanDemandRequestExpenses) {
        return de.joonko.loan.offer.domain.Expenses.builder()
                .monthlyLoanInstallmentsDeclared(loanDemandRequestExpenses.getMonthlyLoanInstallmentsDeclared() != null ? BigDecimal.valueOf(loanDemandRequestExpenses.getMonthlyLoanInstallmentsDeclared()) : null)
                .monthlyLifeCost(loanDemandRequestExpenses.getMonthlyLifeCost() != null ? BigDecimal.valueOf(loanDemandRequestExpenses.getMonthlyLifeCost()) : null)
                .loanInstallmentsSwk(BigDecimal.valueOf(loanDemandRequestExpenses.getLoanInstallmentsSwk()))
                .vehicleInsurance(BigDecimal.valueOf(loanDemandRequestExpenses.getVehicleInsurance()))
                .alimony(BigDecimal.valueOf(loanDemandRequestExpenses.getAlimony()))
                .insuranceAndSavings(BigDecimal.valueOf(loanDemandRequestExpenses.getInsuranceAndSavings()))
                .loanInstalments(BigDecimal.valueOf(loanDemandRequestExpenses.getLoanInstalments()))
                .mortgages(BigDecimal.valueOf(loanDemandRequestExpenses.getMortgages()))
                .privateHealthInsurance(BigDecimal.valueOf(loanDemandRequestExpenses.getPrivateHealthInsurance()))
                .rent(BigDecimal.valueOf(loanDemandRequestExpenses.getRent()))
                .acknowledgedMortgages(BigDecimal.valueOf(loanDemandRequestExpenses.getAcknowledgedMortgages()))
                .acknowledgedRent(ofNullable(loanDemandRequestExpenses.getAcknowledgedRent()).map(BigDecimal::valueOf).orElse(null))
                .build();
    }

    private de.joonko.loan.offer.domain.Income mapIncomeApiModelToDomainModel(Income loanDemandRequestIncome) {
        return de.joonko.loan.offer.domain.Income.builder()
                .incomeDeclared(loanDemandRequestIncome.getIncomeDeclared() != null ? BigDecimal.valueOf(loanDemandRequestIncome.getIncomeDeclared()) : null)
                .alimonyPayments(BigDecimal.valueOf(loanDemandRequestIncome.getAlimonyPayments()))
                .childBenefits(BigDecimal.valueOf(loanDemandRequestIncome.getChildBenefits()))
                .netIncome(BigDecimal.valueOf(loanDemandRequestIncome.getNetIncome()))
                .otherRevenue(BigDecimal.valueOf(loanDemandRequestIncome.getOtherRevenue()))
                .pensionBenefits(BigDecimal.valueOf(loanDemandRequestIncome.getPensionBenefits()))
                .rentalIncome(BigDecimal.valueOf(loanDemandRequestIncome.getRentalIncome()))
                .acknowledgedNetIncome(BigDecimal.valueOf(loanDemandRequestIncome.getAcknowledgedNetIncome()))
                .build();
    }

    private LocalDate getProfessionEndDateValue(de.joonko.loan.offer.api.EmploymentDetails employmentDetails) {
        if (employmentDetails.getProfessionEndDate() != null
                && employmentDetails.getProfessionEndDate().getMonth() != null
                && employmentDetails.getProfessionEndDate().getYear() != null) {
            return LocalDate.of(employmentDetails
                            .getProfessionEndDate()
                            .getYear(),
                    employmentDetails
                            .getProfessionEndDate()
                            .getMonth(), 1);
        }
        return null;
    }

    private CustomDACData getCustomDACData(de.joonko.loan.offer.api.CustomDACData customDACData) {
        return CustomDACData.builder()
                .carInformation(customDACData.getCarInformation())
                .has3IncomeTags(customDACData.getHas3IncomeTags())
                .netIncomeHasGovSupport(customDACData.getNetIncomeHasGovSupport())
                .hasSalary(customDACData.getHasSalary())
                .hasConsorsPreventionTags(customDACData.getHasConsorsPreventionTags())
                .hasSwkPreventionTags(customDACData.getHasSwkPreventionTags())
                .totalIncomeInLast90Days(customDACData.getTotalIncomeInLast90Days())
                .cashWithdrawalsInLast90Days(customDACData.getCashWithdrawalsInLast90Days())
                .gamblingAmountInLast90Days(customDACData.getGamblingAmountInLast90Days())
                .countEncashmentTag(customDACData.getCountEncashmentTag())
                .countSeizureTag(customDACData.getCountSeizureTag())
                .countPAccountTag(customDACData.getCountPAccountTag())
                .countChargebackTag(customDACData.getCountChargebackTag())
                .hasSalaryEachMonthLast3M(customDACData.getHasSalaryEachMonthLast3M())
                .wasDelayInInstallments40DaysDiff(customDACData.getWasDelayInInstallments40DaysDiff())
                .wasDelayInInstallments62DaysDiff(customDACData.getWasDelayInInstallments62DaysDiff())
                .isCurrentDelayInInstallments(customDACData.getIsCurrentDelayInInstallments())
                .sumIncomes1MAgo(customDACData.getSumIncomes1MAgo())
                .sumIncomes2MAgo(customDACData.getSumIncomes2MAgo())
                .sumIncomes3MAgo(customDACData.getSumIncomes3MAgo())
                .build();
    }

}
