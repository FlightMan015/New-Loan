package de.joonko.loan.offer.api;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class LoanDemandFixtures {

    public static LoanDemandRequest getLoanDemandRequest() {
        AccountDetails accountDetails = getAccountDetails();

        return LoanDemandRequest.builder()
                .loanAsked(1000)
                .ftsTransactionId("12ab34cd56ef-78gh90ij12kl34mn")
                .dacId("09zx87fg65sa-12po34lk56gh78tr")
                .personalDetails(getPersonalDetails())
                .employmentDetails(getEmploymentDetails())
                .accountDetails(accountDetails)
                .income(getIncome())
                .expenses(getExpenses())
                .contactData(LoanDemandFixtures.getContactData())
                .customDACData(LoanDemandFixtures.getCustomDACData())
                .applicationId("test-application")
                .userUUID("test-user")
                .requestIp("127.0.0.1")
                .requestCountryCode("DE")
                .build();
    }

    public static Expenses getExpenses() {
        return Expenses.builder()
                .acknowledgedMortgages(0.0)
                .acknowledgedRent(900.0)
                .build();
    }

    public static Income getIncome() {
        return Income.builder()
                .build();
    }

    public static EmploymentDetails getEmploymentDetails() {
        return EmploymentDetails.builder()
                .city("Berlin")
                .employerName("Joonko AG")
                .employmentSince(new ShortDate(5, 2012))
                .postCode("10587")
                .employmentType(de.joonko.loan.offer.api.EmploymentType.REGULAR_EMPLOYED)
                .streetName("HardenbergStr.")
                .build();
    }

    public static PersonalDetails getPersonalDetails() {
        return PersonalDetails.builder()
                .gender(Gender.FEMALE)
                .firstName("Foo")
                .lastName("Bar")
                .familyStatus(FamilyStatus.MARRIED)
                .birthDate(LocalDate.of(1980, 3, 1))
                .nationality(Nationality.DE)
                .numberOfChildren(2)
                .numberOfCreditCard(1)
                .housingType(HousingType.OWNER)
                .placeOfBirth("SOMEWHERE")
                .countryOfBirth("DE")
                .build();
    }

    public static AccountDetails getAccountDetails() {
        return AccountDetails.builder()
                .balance(100.00)
                .balanceDate(LocalDate.now())
                .bic("Some bic")
                .currency("EUR")
                .iban("SomeIBN")
                .limit(10.0)
                .nameOnAccount("Some Name")
                .transactions(List.of(DacTransaction.builder()
                        .amount(50.0)
                        .bic("Some Bic")
                        .bookingDate(LocalDate.now())
                        .iban("Some Ibn")
                        .isPreBooked(false)
                        .purpose("Test")
                        .build()))
                .days(90)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static ContactData getContactData() {
        return ContactData.builder()
                .city("City")
                .houseNumber("12")
                .postCode("12345")
                .streetName("Street Name")
                .email("someone@joonko.io")
                .mobile("491748273421011")
                .livingSince(ShortDate.builder()
                        .month(01)
                        .year(2010)
                        .build())
                .previousAddress(getPreviousAddress())
                .build();
    }

    public static PreviousAddress getPreviousAddress() {
        return PreviousAddress.builder()
                .city("Berlin")
                .streetName("Some Street")
                .postCode("12345")
                .country(Nationality.AD)
                .livingSince(ShortDate.builder()
                        .month(01)
                        .year(2008)
                        .build())
                .build();
    }

    public static CustomDACData getCustomDACData() {
        return CustomDACData.builder()
                .carInformation(true)
                .has3IncomeTags(true)
                .netIncomeHasGovSupport(false)
                .hasSalary(true)
                .hasConsorsPreventionTags(false)
                .hasSwkPreventionTags(false)
                .totalIncomeInLast90Days(10000.0)
                .gamblingAmountInLast90Days(0.0)
                .cashWithdrawalsInLast90Days(0.0)
                .wasDelayInInstallments40DaysDiff(false)
                .wasDelayInInstallments62DaysDiff(false)
                .isCurrentDelayInInstallments(false)
                .hasSalaryEachMonthLast3M(true)
                .build();
    }
}
