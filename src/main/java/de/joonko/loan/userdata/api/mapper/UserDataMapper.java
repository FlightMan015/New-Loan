package de.joonko.loan.userdata.api.mapper;

import de.joonko.loan.integrations.domain.integrationhandler.fts.domain.UserTransactionalDataStore;
import de.joonko.loan.integrations.domain.integrationhandler.userpersonal.UserPersonalData;
import de.joonko.loan.offer.api.*;
import de.joonko.loan.userdata.api.model.UserDataRequest;
import de.joonko.loan.userdata.api.model.UserDataResponse;
import de.joonko.loan.userdata.domain.model.*;
import de.joonko.loan.util.StringUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import static java.util.Optional.ofNullable;

@Mapper(componentModel = "spring")
public interface UserDataMapper {

    UserDataResponse toApiModel(UserData domainModel);

    UserData toDomainModel(UserDataRequest apiModel);

    @Mapping(target = "userUuid", source = "userUuid")
    @Mapping(target = "contactData", source = "userData.userContact")
    @Mapping(target = "employmentDetails", source = "userData.userEmployment")
    @Mapping(target = "personalDetails", source = "userData", qualifiedByName = "mapToPersonalDetails")
    @Mapping(target = "income", source = "userData.userCredit.userIncomes")
    @Mapping(target = "expenses", source = "userData", qualifiedByName = "mapExpenses")
    @Mapping(target = "creditDetails", source = "userData", qualifiedByName = "mapCreditDetails")
    UserPersonalData toUserPersonalData(UserData userData, String userUuid);

    @Mapping(target = "userPersonal", source = "personalDetails")
    @Mapping(target = "userContact", source = "contactData")
    @Mapping(target = "userEmployment", source = "employmentDetails")
    @Mapping(target = "userHousing.housingType", source = "personalDetails.housingType")
    @Mapping(target = "userHousing.numberOfDependants", source = "personalDetails.numberOfDependants")
    @Mapping(target = "userHousing.numberOfChildren", source = "personalDetails.numberOfChildren")
    @Mapping(target = "userHousing.acknowledgedMortgages", source = "expenses.acknowledgedMortgages")
    @Mapping(target = "userHousing.acknowledgedRent", source = "expenses.acknowledgedRent")
    @Mapping(target = "userHousing.rent", source = "expenses.rent")
    @Mapping(target = "userHousing.mortgages", source = "expenses.mortgages")
    @Mapping(target = "userCredit.userIncomes", source = "income")
    @Mapping(target = "userCredit.userExpenses", source = "userPersonalData", qualifiedByName = "mapUserExpenses")
    UserData fromUserPersonalData(UserPersonalData userPersonalData);

    @Named("mapUserExpenses")
    default UserExpenses mapUserExpenses(UserPersonalData userPersonalData) {
        if (userPersonalData == null) {
            return null;
        }
        var expenses = userPersonalData.getExpenses();
        var creditDetails = userPersonalData.getCreditDetails();

        if (expenses == null && creditDetails == null) {
            return null;
        }

        var userExpenses = new UserExpenses();
        userExpenses.setMonthlyLoanInstallmentsDeclared(ofNullable(expenses).map(Expenses::getMonthlyLoanInstallmentsDeclared).orElse(null));
        userExpenses.setIsCurrentDelayInInstallmentsDeclared(ofNullable(creditDetails).map(CreditDetails::getIsCurrentDelayInInstallmentsDeclared).orElse(null));

        userExpenses.setMonthlyLifeCost(ofNullable(expenses).map(Expenses::getMonthlyLifeCost).orElse(null));
        userExpenses.setCreditCardLimitDeclared(ofNullable(creditDetails).map(CreditDetails::getCreditCardLimitDeclared).orElse(null));

        return userExpenses;
    }

    default UserData fromValidUserPersonalData(UserPersonalData userPersonalData) {
        final var userData = fromUserPersonalData(userPersonalData);

        userData.getUserPersonal().setValid(true);
        userData.getUserContact().setValid(true);
        userData.getUserEmployment().setValid(true);
        userData.getUserHousing().setValid(true);
        userData.getUserCredit().setValid(true);

        return userData;
    }

    @Named("mapCreditDetails")
    default CreditDetails mapCreditDetails(UserData userData) {
        if (userData == null || userData.getUserCredit() == null) {
            return null;
        }

        var userExpenses = userData.getUserCredit().getUserExpenses();

        return CreditDetails.builder()
                .isCurrentDelayInInstallmentsDeclared(userExpenses != null ? userExpenses.getIsCurrentDelayInInstallmentsDeclared() : null)
                .creditCardLimitDeclared(userExpenses != null ? userExpenses.getCreditCardLimitDeclared() : null)
                .build();
    }

    @Named("mapToPersonalDetails")
    default PersonalDetails mapToPersonalDetails(UserData userData) {
        if (userData == null) {
            return null;
        }

        var userPersonal = userData.getUserPersonal();
        var userHousing = userData.getUserHousing();
        var userCredit = userData.getUserCredit();
        var userEmployment = userData.getUserEmployment();
        var userExpenses = userCredit != null ? userCredit.getUserExpenses() : null;

        if (userPersonal == null && userHousing == null && userCredit == null && userEmployment == null && userExpenses == null) {
            return null;
        }

        return PersonalDetails.builder()
                .gender(userPersonal != null ? userPersonal.getGender() : null)
                .firstName(userPersonal != null ? userPersonal.getFirstName() : null)
                .lastName(userPersonal != null ? userPersonal.getLastName() : null)
                .familyStatus(userPersonal != null ? userPersonal.getFamilyStatus() : null)
                .birthDate(userPersonal != null ? userPersonal.getBirthDate() : null)
                .nationality(userPersonal != null ? userPersonal.getNationality() : null)
                .placeOfBirth(userPersonal != null ? userPersonal.getPlaceOfBirth() : null)
                .countryOfBirth(userPersonal != null ? userPersonal.getCountryOfBirth() : null)
                .numberOfChildren(userHousing != null ? userHousing.getNumberOfChildren() : null)
                .numberOfDependants(userHousing != null ? userHousing.getNumberOfDependants() : null)
                .housingType(userHousing != null ? userHousing.getHousingType() : null)
                .taxId(userEmployment != null ? userEmployment.getTaxId() : null)
                .build();
    }

    @Named("mapExpenses")
    default Expenses mapExpenses(UserData userData) {
        if (userData == null) {
            return null;
        }

        var userHousing = userData.getUserHousing();
        var userExpenses = userData.getUserCredit() != null ? userData.getUserCredit().getUserExpenses() : null;

        if (userHousing == null && userExpenses == null) {
            return null;
        }

        return Expenses.builder()
                .monthlyLifeCost(userExpenses != null ? userExpenses.getMonthlyLifeCost() : null)
                .monthlyLoanInstallmentsDeclared(userExpenses != null ? userExpenses.getMonthlyLoanInstallmentsDeclared() : null)
                .acknowledgedMortgages(userHousing != null ? userHousing.getAcknowledgedMortgages() : null)
                .acknowledgedRent(userHousing != null ? userHousing.getAcknowledgedRent() : null)
                .build();
    }

    default UserData merge(UserData userData, UserTransactionalDataStore transactionalData) {
        if (transactionalData.getUserUUID() == null) {
            return userData;
        }

        userData.setUserAccount(mapUserAccount(transactionalData.getAccountDetails()));

        final var incomesFromFts = transactionalData.getIncome();
        final var expensesFromFts = transactionalData.getExpenses();
        final var customDacData = transactionalData.getCustomDACData();
        final var customDacPersonalData = transactionalData.getCustomDacPersonalDetails();

        final var exisingHousing = userData.getUserHousing();
        userData.setUserHousing(merge(exisingHousing, expensesFromFts));

        if (incomesFromFts == null && expensesFromFts == null && customDacPersonalData == null) {
            return userData;
        }

        if (userData.getUserCredit() == null) {
            userData.setUserCredit(new UserCredit());
        }

        final var userCredit = userData.getUserCredit();

        final var existingIncomes = userCredit.getUserIncomes();
        final var existingExpenses = userCredit.getUserExpenses();

        userCredit.setUserIncomes(merge(existingIncomes, incomesFromFts));
        userCredit.setUserExpenses(merge(existingExpenses, expensesFromFts, customDacData, customDacPersonalData));

        return userData;
    }

    default UserHousing merge(UserHousing existingHousing, Expenses expensesFromFts) {
        if (existingHousing == null && expensesFromFts == null) {
            return null;
        }

        final var mergedUserHousing = new UserHousing();
        mergedUserHousing.setHousingType(ofNullable(existingHousing).map(UserHousing::getHousingType).orElse(null));
        mergedUserHousing.setNumberOfDependants(ofNullable(existingHousing).map(UserHousing::getNumberOfDependants).orElse(null));
        mergedUserHousing.setNumberOfChildren(ofNullable(existingHousing).map(UserHousing::getNumberOfChildren).orElse(null));
        mergedUserHousing.setMortgages(ofNullable(expensesFromFts).map(Expenses::getMortgages).orElse(null));
        mergedUserHousing.setAcknowledgedMortgages(ofNullable(existingHousing).map(UserHousing::getAcknowledgedMortgages).orElse(null));
        mergedUserHousing.setRent(ofNullable(expensesFromFts).map(Expenses::getRent).orElse(null));
        mergedUserHousing.setAcknowledgedRent(ofNullable(existingHousing).map(UserHousing::getAcknowledgedRent).orElse(null));

        return mergedUserHousing;
    }

    default UserAccount mapUserAccount(AccountDetails accountDetails) {
        return ofNullable(accountDetails)
                .map(account -> {
                    UserAccount userAccount = new UserAccount();
                    userAccount.setNameOnAccount(accountDetails.getNameOnAccount());
                    userAccount.setIban(StringUtil.maskIban(accountDetails.getIban(), "*"));
                    userAccount.setBic(StringUtil.maskLastNCharacters(accountDetails.getBic(), "*", 4));
                    userAccount.setBankName(accountDetails.getBankName());
                    return userAccount;
                }).orElse(null);
    }

    default UserIncomes merge(UserIncomes existingIncomes, Income ftsIncome) {
        if (existingIncomes == null && ftsIncome == null) {
            return null;
        }
        final var mergedUserIncomes = new UserIncomes();
        mergedUserIncomes.setNetIncome(ofNullable(ftsIncome).map(Income::getNetIncome).orElse(null));
        mergedUserIncomes.setAcknowledgedNetIncome(ofNullable(existingIncomes).map(UserIncomes::getAcknowledgedNetIncome).orElse(null));
        mergedUserIncomes.setIncomeDeclared(ofNullable(existingIncomes).map(UserIncomes::getIncomeDeclared).orElse(null));

        return mergedUserIncomes;
    }

    default UserExpenses merge(UserExpenses existingExpenses, Expenses ftsExpenses, CustomDACData customDACData, CustomDacPersonalDetails customDacPersonalData) {
        if (existingExpenses == null && ftsExpenses == null && customDACData == null && customDacPersonalData == null) {
            return null;
        }

        final var mergedUserExpenses = new UserExpenses();
        mergedUserExpenses.setLoanInstalments(ofNullable(ftsExpenses).map(Expenses::getLoanInstalments).orElse(null));
        mergedUserExpenses.setMonthlyLoanInstallmentsDeclared(ofNullable(existingExpenses).map(UserExpenses::getMonthlyLoanInstallmentsDeclared).orElse(null));

        mergedUserExpenses.setIsCurrentDelayInInstallments(ofNullable(customDACData).map(CustomDACData::getIsCurrentDelayInInstallments).orElse(null));
        mergedUserExpenses.setIsCurrentDelayInInstallmentsDeclared(ofNullable(existingExpenses).map(UserExpenses::getIsCurrentDelayInInstallmentsDeclared).orElse(null));

        mergedUserExpenses.setMonthlyLifeCost(ofNullable(existingExpenses).map(UserExpenses::getMonthlyLifeCost).orElse(null));
        mergedUserExpenses.setCreditCardLimitDeclared(ofNullable(existingExpenses).map(UserExpenses::getCreditCardLimitDeclared).orElse(null));
        mergedUserExpenses.setNumberOfCreditCard(ofNullable(customDacPersonalData).map(CustomDacPersonalDetails::getNumberOfCreditCard).orElse(null));

        return mergedUserExpenses;
    }
}
