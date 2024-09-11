package de.joonko.loan.integrations.domain.integrationhandler.userpersonal.provider;

import com.google.common.primitives.Longs;

import de.joonko.loan.common.Regex;
import de.joonko.loan.integrations.domain.integrationhandler.userpersonal.UserPersonalData;
import de.joonko.loan.integrations.model.DistributionChannel;
import de.joonko.loan.integrations.segment.CustomerData;
import de.joonko.loan.offer.api.CreditDetails;
import de.joonko.loan.offer.api.EmploymentType;
import de.joonko.loan.offer.api.FamilyStatus;
import de.joonko.loan.offer.api.Gender;
import de.joonko.loan.offer.api.HousingType;
import de.joonko.loan.offer.api.Nationality;
import de.joonko.loan.offer.api.ShortDate;
import de.joonko.loan.user.service.UserAdditionalInformationStore;
import de.joonko.loan.user.service.UserPersonalInformationStore;
import de.joonko.loan.user.service.persistence.domain.ConsentData;
import de.joonko.loan.userdata.domain.model.UserData;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

import io.fusionauth.domain.User;

import static java.util.Optional.ofNullable;

@Mapper(componentModel = "spring")
public abstract class UserPersonalDataMapper {

    @Autowired
    DistributionChannelManager distributionChannelManager;

    @Mapping(target = "userUuid", expression = "java(user.id.toString())")
    @Mapping(target = "bonifyUserId", source = "data", qualifiedByName = "extractBonifyUserId")
    @Mapping(target = "contactData.email", expression = "java(user.lookupEmail())")
    @Mapping(target = "verifiedViaBankAccount", expression = "java((Boolean) user.data.get(\"userVerifiedViaBankAccount\"))")
    @Mapping(target = "distributionChannel", source = "tenantId", qualifiedByName = "extractDistributionChannel")
    @Mapping(target = "personalDetails.firstName", source = "firstName")
    @Mapping(target = "personalDetails.lastName", source = "lastName")
    @Mapping(target = "contactData.city", expression = "java((String) user.data.get(\"addressCity\"))")
    @Mapping(target = "contactData.houseNumber", expression = "java((String) user.data.get(\"addressHouseNumber\"))")
    @Mapping(target = "contactData.streetName", expression = "java((String) user.data.get(\"addressStreet\"))")
    @Mapping(target = "contactData.postCode", expression = "java((String) user.data.get(\"addressZipCode\"))")
    @Mapping(target = "personalDetails.nationality", source = "data", qualifiedByName = "extractNationality")
    @Mapping(target = "personalDetails.placeOfBirth", expression = "java((String) user.data.get(\"placeOfBirth\"))")
    @Mapping(target = "personalDetails.gender", source = "data", qualifiedByName = "extractGender")
    @Mapping(target = "personalDetails.familyStatus", source = "data", qualifiedByName = "extractFamilyStatus")
    @Mapping(target = "contactData.mobile", source = "mobilePhone", qualifiedByName = "extractMobilePhone")
    @Mapping(target = "tenantId", expression = "java(user.tenantId.toString())")
    public abstract UserPersonalData fromUserData(User user);

    @Mapping(target = "verifiedViaBankAccount", source = "traits.hasAddedBank")
    @Mapping(target = "distributionChannel", source = "traits.tenantId", qualifiedByName = "mapDistributionChannel")
    @Mapping(target = "personalDetails.gender", source = "traits.gender", qualifiedByName = "mapGender")
    @Mapping(target = "personalDetails.firstName", source = "traits.firstName")
    @Mapping(target = "personalDetails.lastName", source = "traits.lastName")
    @Mapping(target = "personalDetails.familyStatus", source = "traits.maritalStatus", qualifiedByName = "mapFamilyStatus")
    @Mapping(target = "personalDetails.birthDate", source = "traits.dateOfBirth")
    @Mapping(target = "personalDetails.nationality", source = "traits.nationality", qualifiedByName = "mapNationality")
    @Mapping(target = "personalDetails.placeOfBirth", source = "traits.placeOfBirth")
    @Mapping(target = "personalDetails.numberOfChildren", source = "traits.childrenCount")
    @Mapping(target = "personalDetails.housingType", source = "traits.housingSituation", qualifiedByName = "mapHousingType")
    @Mapping(target = "personalDetails.numberOfCreditCard", source = "traits.numberOfCreditCard")
    @Mapping(target = "contactData.streetName", source = "traits.addressStreet")
    @Mapping(target = "contactData.houseNumber", source = "traits.addressHouseNumber")
    @Mapping(target = "contactData.postCode", source = "traits.addressZipCode")
    @Mapping(target = "contactData.city", source = "traits.addressCity")
    @Mapping(target = "contactData.livingSince", source = "traits.livingSince", qualifiedByName = "mapShortDate")
    @Mapping(target = "contactData.email", source = "traits.email")
    @Mapping(target = "contactData.mobile", source = "traits.phone_number", qualifiedByName = "extractMobilePhone")
    @Mapping(target = "employmentDetails.employmentType", source = "traits.employmentType", qualifiedByName = "mapEmploymentType")
    @Mapping(target = "employmentDetails.employerName", source = "traits.nameOfEmployer")
    @Mapping(target = "employmentDetails.employmentSince", source = "traits.workContractStartDate", qualifiedByName = "mapShortDate")
    @Mapping(target = "employmentDetails.streetName", source = "traits.addressStreetOfEmployer")
    @Mapping(target = "employmentDetails.postCode", source = "traits.addressZipCodeOfEmployer")
    @Mapping(target = "employmentDetails.city", source = "traits.addressCityOfEmployer")
    @Mapping(target = "employmentDetails.houseNumber", source = "traits.addressHouseNumberOfEmployer")
    @Mapping(target = "income.netIncome", source = "traits.employeeSalaryAmountLast1M")
    @Mapping(target = "income.pensionBenefits", source = "traits.pensionAmountLast1M")
    @Mapping(target = "income.childBenefits", source = "traits.childBenefitAmountLast1M")
    @Mapping(target = "income.otherRevenue", source = "traits.otherIncomeAmountLast1M")
    @Mapping(target = "income.rentalIncome", source = "traits.rentalIncomeLast1M")
    @Mapping(target = "income.alimonyPayments", source = "traits.alimonyIncomeAmountLast1M")
    @Mapping(target = "expenses.mortgages", source = "traits.monthlyMortgage", qualifiedByName = "absValue")
    @Mapping(target = "expenses.insuranceAndSavings", source = "traits.monthlyInsurance", qualifiedByName = "absValue")
    @Mapping(target = "expenses.loanInstalments", source = "traits.monthlyLoanInstallments", qualifiedByName = "absValue")
    @Mapping(target = "expenses.rent", source = "traits.monthlyRent", qualifiedByName = "absValue")
    @Mapping(target = "expenses.alimony", source = "traits.alimonyAmountLast1M", qualifiedByName = "absValue")
    @Mapping(target = "expenses.privateHealthInsurance", source = "traits.monthlyPrivateHealthInsurance", qualifiedByName = "absValue")
    @Mapping(target = "expenses.vehicleInsurance", source = "traits.carInsuranceAmountLast1M", qualifiedByName = "absValue")
    @Mapping(target = "creditDetails.bonimaScore", source = "traits.bonimaScore")
    @Mapping(target = "creditDetails.estimatedSchufaClass", source = "traits.estimatedSchufaClass")
    @Mapping(target = "creditDetails.probabilityOfDefault", source = "traits.probabilityOfDefault")
    public abstract UserPersonalData fromCustomerData(CustomerData customerData);

    @Mapping(target = "userUUID", source = "userPersonalData.userUuid")
    @Mapping(target = "creditDetails", expression = "java(mapCreditDetails(existingCreditDetails, userPersonalData.getCreditDetails()))")
    @Mapping(target = "consentData", source = "consents")
    public abstract UserAdditionalInformationStore toUserAdditionalInfo(UserPersonalData userPersonalData, List<ConsentData> consents, CreditDetails existingCreditDetails);

    @Mapping(target = "userUUID", source = "userUuid")
    @Mapping(target = "firstName", source = "personalDetails.firstName")
    @Mapping(target = "lastName", source = "personalDetails.lastName")
    @Mapping(target = "addressCity", source = "contactData.city")
    @Mapping(target = "addressHouseNumber", source = "contactData.houseNumber")
    @Mapping(target = "addressStreet", source = "contactData.streetName")
    @Mapping(target = "addressZipCode", source = "contactData.postCode")
    @Mapping(target = "addressLivingSinceDate", source = "contactData.livingSince")
    @Mapping(target = "email", source = "contactData.email")
    @Mapping(target = "nationality", source = "personalDetails.nationality")
    @Mapping(target = "placeOfBirth", source = "personalDetails.placeOfBirth")
    @Mapping(target = "countryOfBirth", source = "personalDetails.countryOfBirth")
    @Mapping(target = "birthDate", source = "personalDetails.birthDate")
    @Mapping(target = "gender", source = "personalDetails.gender")
    @Mapping(target = "familyStatus", source = "personalDetails.familyStatus")
    @Mapping(target = "mobilePhone", source = "contactData.mobile")
    @Mapping(target = "numberOfChildren", source = "personalDetails.numberOfChildren")
    @Mapping(target = "numberOfDependants", source = "personalDetails.numberOfDependants")
    public abstract UserPersonalInformationStore toUserPersonalInfo(UserPersonalData userPersonalData);

    @Mapping(target = "userUuid", source = "userUUID")
    public abstract UserPersonalData fromUserAdditionalInfo(UserAdditionalInformationStore userAdditionalInformationStore);

    @Named("mapCreditDetails")
    CreditDetails mapCreditDetails(CreditDetails existingCredits, CreditDetails newCredits) {
        if (existingCredits == null) {
            return newCredits;
        } else if (newCredits == null) {
            return existingCredits;
        }

        return CreditDetails.builder()
                .bonimaScore(ofNullable(newCredits.getBonimaScore()).orElse(existingCredits.getBonimaScore()))
                .estimatedSchufaClass(ofNullable(newCredits.getEstimatedSchufaClass()).orElse(existingCredits.getEstimatedSchufaClass()))
                .probabilityOfDefault(ofNullable(newCredits.getProbabilityOfDefault()).orElse(existingCredits.getProbabilityOfDefault()))
                .creditCardLimitDeclared(ofNullable(newCredits.getCreditCardLimitDeclared()).orElse(existingCredits.getCreditCardLimitDeclared()))
                .isCurrentDelayInInstallmentsDeclared(ofNullable(newCredits.getIsCurrentDelayInInstallmentsDeclared()).orElse(existingCredits.getIsCurrentDelayInInstallmentsDeclared()))
                .build();
    }

    @Named("mapEmploymentType")
    EmploymentType mapEmploymentType(String employmentType) {
        return ofNullable(employmentType)
                .map(String::toUpperCase)
                .map(type -> {
                    switch (type) {
                        case "REGULAR_EMPLOYED":
                        case "EMPLOYEE/WORKER":
                            return EmploymentType.REGULAR_EMPLOYED;
                        case "OTHER":
                            return EmploymentType.OTHER;
                        default:
                            return null;
                    }
                })
                .orElse(null);
    }

    @Named("absValue")
    Double absValue(Double val) {
        return ofNullable(val)
                .map(Math::abs)
                .orElse(null);
    }

    @Named("mapDistributionChannel")
    DistributionChannel mapDistributionChannel(String tenantId) {
        return Optional.ofNullable(tenantId)
                .map(distributionChannelManager::extractDistributionChannel)
                .orElse(null);
    }

    @Named("extractDistributionChannel")
    DistributionChannel extractDistributionChannel(UUID tenantId) {
        return Optional.ofNullable(tenantId)
                .map(String::valueOf)
                .map(distributionChannelManager::extractDistributionChannel)
                .orElse(null);
    }

    @Named("mapShortDate")
    ShortDate mapShortDate(LocalDate date) {
        return Optional.ofNullable(date)
                .map(d -> ShortDate.builder()
                        .year(d.getYear())
                        .month(d.getMonth().getValue()).build())
                .orElse(null);
    }

    @Named("mapGender")
    Gender mapGender(String gender) {
        return Gender.fromString(gender);
    }

    @Named("mapFamilyStatus")
    FamilyStatus mapFamilyStatus(String familyStatus) {
        return FamilyStatus.fromString(familyStatus);
    }

    @Named("mapNationality")
    Nationality mapNationality(String nationality) {
        return Optional.ofNullable(nationality)
                .map(Nationality::fromString)
                .orElse(null);
    }

    @Named("mapHousingType")
    HousingType mapHousingType(String housingType) {
        return Optional.ofNullable(housingType)
                .map(String::toUpperCase)
                .map(type -> {
                    switch (type) {
                        case "PURCHASED":
                        case "OWNER":
                            return HousingType.OWNER;
                        case "RENT":
                            return HousingType.RENT;
                        default:
                            return null;
                    }
                })
                .orElse(null);
    }

    @Named("extractBonifyUserId")
    Long extractBonifyUserId(Map<String, Object> data) {
        return Optional.ofNullable(data.get("uid")).map(Object::toString)
                .map(Longs::tryParse)
                .orElse(null);
    }

    @Named("extractGender")
    Gender extractGender(Map<String, Object> data) {
        return Optional.ofNullable(data).map(d -> d.get("gender"))
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .map(Gender::fromString)
                .orElse(null);
    }

    @Named("extractNationality")
    Nationality extractNationality(Map<String, Object> data) {
        return Optional.ofNullable(data).map(d -> d.get("nationality"))
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .map(Nationality::fromString)
                .orElse(null);
    }

    @Named("extractFamilyStatus")
    FamilyStatus extractFamilyStatus(Map<String, Object> data) {
        return Optional.ofNullable(data).map(d -> d.get("familyStatus"))
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .map(FamilyStatus::fromString)
                .orElse(null);
    }

    @Named("extractMobilePhone")
    String extractMobilePhone(String mobilePhone) {
        try {
            return Optional.ofNullable(mobilePhone)
                    .map(phone -> {
                        String editedPhone = phone;
                        if (phone.startsWith("+")) {
                            editedPhone = phone.substring(1);
                        } else if (phone.startsWith("00")) {
                            editedPhone = phone.substring(2);
                        }
                        if (Pattern.matches(Regex.MOBILE_NUMBER_REGEX, editedPhone)) {
                            return editedPhone;
                        } else {
                            return null;
                        }
                    }).orElse(null);
        } catch (Exception ex) {
            return null;
        }
    }
}
