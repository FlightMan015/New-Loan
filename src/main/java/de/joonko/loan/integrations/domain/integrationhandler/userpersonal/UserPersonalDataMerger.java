package de.joonko.loan.integrations.domain.integrationhandler.userpersonal;

import de.joonko.loan.offer.api.*;
import de.joonko.loan.integrations.model.DistributionChannel;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

@Service
public class UserPersonalDataMerger {

    public UserPersonalData merge(List<UserPersonalData> userPersonalList) {
        var personalDetailsList = userPersonalList.stream()
                .map(UserPersonalData::getPersonalDetails)
                .filter(Objects::nonNull)
                .collect(toList());

        var personalDetails = mergePersonalDetails(personalDetailsList);

        var employmentDetailsList = userPersonalList.stream()
                .map(UserPersonalData::getEmploymentDetails)
                .filter(Objects::nonNull)
                .collect(toList());

        var employmentDetails = mergeEmploymentDetails(employmentDetailsList);

        var contactDataList = userPersonalList.stream()
                .map(UserPersonalData::getContactData)
                .filter(Objects::nonNull)
                .collect(toList());

        var creditDetailsList = userPersonalList.stream()
                .map(UserPersonalData::getCreditDetails)
                .filter(Objects::nonNull)
                .collect(toList());

        var creditDetails = mergeCreditDetails(creditDetailsList);

        var contactData = mergeContactData(contactDataList);

        var incomeList = userPersonalList.stream()
                .map(UserPersonalData::getIncome)
                .filter(Objects::nonNull)
                .collect(toList());

        var income = mergeIncome(incomeList);

        var expensesList = userPersonalList.stream()
                .map(UserPersonalData::getExpenses)
                .filter(Objects::nonNull)
                .collect(toList());

        var expenses = mergeExpenses(expensesList);

        String userId = userPersonalList.stream()
                .map(UserPersonalData::getUserUuid)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        Long bonifyUserId = userPersonalList.stream()
                .map(UserPersonalData::getBonifyUserId)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        DistributionChannel distributionChannel = userPersonalList.stream()
                .map(UserPersonalData::getDistributionChannel)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        Boolean verifiedViaBank = userPersonalList.stream()
                .map(UserPersonalData::getVerifiedViaBankAccount)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(false);

        final var tenantId = userPersonalList.stream()
                .map(UserPersonalData::getTenantId)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        return UserPersonalData.builder()
                .userUuid(userId)
                .bonifyUserId(bonifyUserId)
                .verifiedViaBankAccount(verifiedViaBank)
                .distributionChannel(distributionChannel)
                .personalDetails(personalDetails)
                .employmentDetails(employmentDetails)
                .contactData(contactData)
                .income(income)
                .expenses(expenses)
                .creditDetails(creditDetails)
                .tenantId(tenantId)
                .build();
    }

    private CreditDetails mergeCreditDetails(List<CreditDetails> creditDetailsList) {
        if (creditDetailsList.isEmpty()) {
            return null;
        }

        Integer bonimaScore = creditDetailsList.stream()
                .map(CreditDetails::getBonimaScore)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        String estimatedSchufaClass = creditDetailsList.stream()
                .map(CreditDetails::getEstimatedSchufaClass)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        Double probabilityOfDefault = creditDetailsList.stream()
                .map(CreditDetails::getProbabilityOfDefault)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        return CreditDetails.builder()
                .bonimaScore(bonimaScore)
                .estimatedSchufaClass(estimatedSchufaClass)
                .probabilityOfDefault(probabilityOfDefault)
                .build();
    }

    private Expenses mergeExpenses(List<Expenses> expenses) {
        if (expenses.isEmpty()) {
            return null;
        }
        Double mortgages = expenses.stream()
                .filter(Objects::nonNull)
                .map(Expenses::getMortgages)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        Double insuranceAndSavings = expenses.stream()
                .filter(Objects::nonNull)
                .map(Expenses::getInsuranceAndSavings)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        Double loanInstalments = expenses.stream()
                .filter(Objects::nonNull)
                .map(Expenses::getLoanInstalments)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        Double rent = expenses.stream()
                .filter(Objects::nonNull)
                .map(Expenses::getRent)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        Double alimony = expenses.stream()
                .filter(Objects::nonNull)
                .map(Expenses::getAlimony)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        Double privateHealthInsurance = expenses.stream()
                .filter(Objects::nonNull)
                .map(Expenses::getPrivateHealthInsurance)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        Double loanInstallmentsSwk = expenses.stream()
                .filter(Objects::nonNull)
                .map(Expenses::getLoanInstallmentsSwk)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        Double vehicleInsurance = expenses.stream()
                .filter(Objects::nonNull)
                .map(Expenses::getVehicleInsurance)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        Double acknowledgedMortgages = expenses.stream()
                .filter(Objects::nonNull)
                .map(Expenses::getAcknowledgedMortgages)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        Double acknowledgedRent = expenses.stream()
                .filter(Objects::nonNull)
                .map(Expenses::getAcknowledgedRent)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        return Expenses.builder()
                .mortgages(mortgages)
                .insuranceAndSavings(insuranceAndSavings)
                .loanInstalments(loanInstalments)
                .rent(rent)
                .alimony(alimony)
                .privateHealthInsurance(privateHealthInsurance)
                .loanInstallmentsSwk(loanInstallmentsSwk)
                .vehicleInsurance(vehicleInsurance)
                .acknowledgedMortgages(acknowledgedMortgages)
                .acknowledgedRent(acknowledgedRent)
                .build();
    }

    private Income mergeIncome(List<Income> incomes) {
        if (incomes.isEmpty()) {
            return null;
        }
        Double netIncome = incomes.stream()
                .filter(Objects::nonNull)
                .map(Income::getNetIncome)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        Double pensionBenefits = incomes.stream()
                .filter(Objects::nonNull)
                .map(Income::getPensionBenefits)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        Double childBenefits = incomes.stream()
                .filter(Objects::nonNull)
                .map(Income::getChildBenefits)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        Double otherRevenue = incomes.stream()
                .filter(Objects::nonNull)
                .map(Income::getOtherRevenue)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        Double rentalIncome = incomes.stream()
                .filter(Objects::nonNull)
                .map(Income::getRentalIncome)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        Double alimonyPayments = incomes.stream()
                .filter(Objects::nonNull)
                .map(Income::getAlimonyPayments)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        Double acknowledgedNetIncome = incomes.stream()
                .filter(Objects::nonNull)
                .map(Income::getAcknowledgedNetIncome)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        return Income.builder()
                .netIncome(netIncome)
                .pensionBenefits(pensionBenefits)
                .rentalIncome(rentalIncome)
                .alimonyPayments(alimonyPayments)
                .childBenefits(childBenefits)
                .acknowledgedNetIncome(acknowledgedNetIncome)
                .otherRevenue(otherRevenue)
                .build();
    }

    private ContactData mergeContactData(List<ContactData> contactDatas) {
        if (contactDatas.isEmpty()) {
            return null;
        }

        String streetName = contactDatas.stream()
                .filter(Objects::nonNull)
                .map(ContactData::getStreetName)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        String houseNumber = contactDatas.stream()
                .filter(Objects::nonNull)
                .map(ContactData::getHouseNumber)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        String postCode = contactDatas.stream()
                .filter(Objects::nonNull)
                .map(ContactData::getPostCode)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        String city = contactDatas.stream()
                .filter(Objects::nonNull)
                .map(ContactData::getCity)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        String email = contactDatas.stream()
                .filter(Objects::nonNull)
                .map(ContactData::getEmail)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        ShortDate livingSince = contactDatas.stream()
                .filter(Objects::nonNull)
                .map(ContactData::getLivingSince)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        PreviousAddress previousAddress = contactDatas.stream()
                .filter(Objects::nonNull)
                .map(ContactData::getPreviousAddress)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        String mobile = contactDatas.stream()
                .filter(Objects::nonNull)
                .map(ContactData::getMobile)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        return ContactData.builder()
                .city(city)
                .streetName(streetName)
                .postCode(postCode)
                .houseNumber(houseNumber)
                .email(email)
                .mobile(mobile)
                .livingSince(livingSince)
                .previousAddress(previousAddress)
                .build();
    }

    private EmploymentDetails mergeEmploymentDetails(List<EmploymentDetails> employmentDetails) {
        if (employmentDetails.isEmpty()) {
            return null;
        }

        EmploymentType employmentType = employmentDetails.stream()
                .filter(Objects::nonNull)
                .map(EmploymentDetails::getEmploymentType)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        String employerName = employmentDetails.stream()
                .filter(Objects::nonNull)
                .map(EmploymentDetails::getEmployerName)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        ShortDate employmentSince = employmentDetails.stream()
                .filter(Objects::nonNull)
                .map(EmploymentDetails::getEmploymentSince)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        String streetName = employmentDetails.stream()
                .filter(Objects::nonNull)
                .map(EmploymentDetails::getStreetName)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        String postCode = employmentDetails.stream()
                .filter(Objects::nonNull)
                .map(EmploymentDetails::getPostCode)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        String city = employmentDetails.stream()
                .filter(Objects::nonNull)
                .map(EmploymentDetails::getCity)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        ShortDate professionEndDate = employmentDetails.stream()
                .filter(Objects::nonNull)
                .map(EmploymentDetails::getProfessionEndDate)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        String houseNumber = employmentDetails.stream()
                .filter(Objects::nonNull)
                .map(EmploymentDetails::getHouseNumber)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        return EmploymentDetails.builder()
                .employmentType(employmentType)
                .employerName(employerName)
                .employmentSince(employmentSince)
                .streetName(streetName)
                .postCode(postCode)
                .city(city)
                .professionEndDate(professionEndDate)
                .houseNumber(houseNumber)
                .build();
    }

    private PersonalDetails mergePersonalDetails(List<PersonalDetails> personalDetails) {
        if (personalDetails.isEmpty()) {
            return null;
        }

        Gender gender = personalDetails.stream()
                .filter(Objects::nonNull)
                .map(PersonalDetails::getGender)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        String firstName = personalDetails.stream()
                .filter(Objects::nonNull)
                .map(PersonalDetails::getFirstName)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        String lastName = personalDetails.stream()
                .filter(Objects::nonNull)
                .map(PersonalDetails::getLastName)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        FamilyStatus familyStatus = personalDetails.stream()
                .filter(Objects::nonNull)
                .map(PersonalDetails::getFamilyStatus)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        LocalDate birthDate = personalDetails.stream()
                .filter(Objects::nonNull)
                .map(PersonalDetails::getBirthDate)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        Nationality nationality = personalDetails.stream()
                .filter(Objects::nonNull)
                .map(PersonalDetails::getNationality)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        String placeOfBirth = personalDetails.stream()
                .filter(Objects::nonNull)
                .map(PersonalDetails::getPlaceOfBirth)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        Integer numberOfChildren = personalDetails.stream()
                .filter(Objects::nonNull)
                .map(PersonalDetails::getNumberOfChildren)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        HousingType housingType = personalDetails.stream()
                .filter(Objects::nonNull)
                .map(PersonalDetails::getHousingType)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        Integer numberOfCreditCard = personalDetails.stream()
                .filter(Objects::nonNull)
                .map(PersonalDetails::getNumberOfCreditCard)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        return PersonalDetails.builder()
                .firstName(firstName)
                .lastName(lastName)
                .familyStatus(familyStatus)
                .numberOfCreditCard(numberOfCreditCard)
                .numberOfChildren(numberOfChildren)
                .housingType(housingType)
                .gender(gender)
                .nationality(nationality)
                .birthDate(birthDate)
                .placeOfBirth(placeOfBirth).build();
    }
}
