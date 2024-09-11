package de.joonko.loan.integrations.domain.integrationhandler.userpersonal.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.joonko.loan.common.Regex;
import de.joonko.loan.db.vo.LoanOfferStore;
import de.joonko.loan.identification.model.CreateIdentRequest;
import de.joonko.loan.integrations.domain.integrationhandler.userpersonal.UserPersonalData;
import de.joonko.loan.offer.api.*;
import de.joonko.loan.offer.api.model.UserPersonalDetails;
import de.joonko.loan.user.service.UserAdditionalInformationStore;
import de.joonko.loan.user.service.UserPersonalInformationStore;
import de.joonko.loan.integrations.domain.integrationhandler.fts.domain.UserTransactionalDataStore;
import de.joonko.loan.user.service.persistence.domain.ConsentState;
import io.fusionauth.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.apache.commons.collections.IteratorUtils.toList;

@Mapper(componentModel = "spring", uses = {CustomDacPersonalDetailsMapper.class})
public interface UserPersonalInformationMapper {

    UserPersonalData mapFromUserInput(String userUuid, UserPersonalDetails userPersonalDetails);


    @Mapping(target = "userUUID", expression = "java(user.id.toString())")
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "addressCity", expression = "java((String) user.data.get(\"addressCity\"))")
    @Mapping(target = "addressHouseNumber", expression = "java((String) user.data.get(\"addressHouseNumber\"))")
    @Mapping(target = "addressStreet", expression = "java((String) user.data.get(\"addressStreet\"))")
    @Mapping(target = "addressZipCode", expression = "java((String) user.data.get(\"addressZipCode\"))")
    @Mapping(target = "email", expression = "java(user.lookupEmail())")
    @Mapping(target = "nationality", source = "user.data", qualifiedByName = "extractNationality")
    @Mapping(target = "placeOfBirth", expression = "java((String) user.data.get(\"placeOfBirth\"))")
    @Mapping(target = "countryOfBirth", expression = "java((String) user.data.get(\"countryOfBirth\"))")
    @Mapping(target = "gender", source = "user.data", qualifiedByName = "extractGender")
    @Mapping(target = "familyStatus", source = "user.data", qualifiedByName = "extractFamilyStatus")
    @Mapping(target = "mobilePhone", source = "user.mobilePhone", qualifiedByName = "extractMobilePhone")
    UserPersonalInformationStore map(User user);

    PersonalDetails fromUserPersonalDetailsStore(UserPersonalInformationStore userPersonalInformationStore);

    @Mapping(target = "firstName", source = "customDacPersonalDetails.firstName")
    @Mapping(target = "lastName", source = "customDacPersonalDetails.lastName")
    @Mapping(target = "numberOfChildren", source = "customDacPersonalDetails.numberOfChildren")
    @Mapping(target = "numberOfCreditCard", source = "customDacPersonalDetails.numberOfCreditCard")
    PersonalDetails fromUserTransactionalDataStore(CustomDacPersonalDetails customDacPersonalDetails, PersonalDetails personalDetails);

    @Mapping(target = "firstName", source = "userPersonalDetails.personalDetails.firstName")
    @Mapping(target = "lastName", source = "userPersonalDetails.personalDetails.lastName")
    @Mapping(target = "gender", source = "userPersonalDetails.personalDetails.gender")
    @Mapping(target = "birthDate", source = "userPersonalDetails.personalDetails.birthDate")
    @Mapping(target = "email", source = "userPersonalDetails.contactData.email")
    @Mapping(target = "addressCity", source = "userPersonalDetails.contactData.city")
    @Mapping(target = "addressHouseNumber", source = "userPersonalDetails.contactData.houseNumber")
    @Mapping(target = "addressStreet", source = "userPersonalDetails.contactData.streetName")
    @Mapping(target = "addressZipCode", source = "userPersonalDetails.contactData.postCode")
    @Mapping(target = "nationality", source = "userPersonalDetails.personalDetails.nationality")
    @Mapping(target = "placeOfBirth", source = "userPersonalDetails.personalDetails.placeOfBirth")
    @Mapping(target = "countryOfBirth", source = "userPersonalDetails.personalDetails.countryOfBirth")
    @Mapping(target = "familyStatus", source = "userPersonalDetails.personalDetails.familyStatus")
    @Mapping(target = "numberOfChildren", source = "userPersonalDetails.personalDetails.numberOfChildren")
    @Mapping(target = "mobilePhone", expression = "java(correctMobilePhone(userPersonalDetails, userPersonalInformationStore))")
    UserPersonalInformationStore map(UserPersonalDetails userPersonalDetails, UserPersonalInformationStore userPersonalInformationStore);

    @Mapping(target = "firstName", expression = "java(correctFirstName(userTransactionalDataStore, userAdditionalInformationStore))")
    @Mapping(target = "lastName", expression = "java(correctLastName(userTransactionalDataStore, userAdditionalInformationStore))")
    @Mapping(target = "numberOfCreditCard", source = "userTransactionalDataStore.customDacPersonalDetails.numberOfCreditCard")
    @Mapping(target = "gender", source = "userPersonalInformationStore.gender")
    @Mapping(target = "birthDate", source = "userPersonalInformationStore.birthDate")

    @Mapping(target = "familyStatus", source = "userPersonalInformationStore.familyStatus")
    @Mapping(target = "numberOfChildren", expression = "java(userPersonalInformationStore.getNumberOfChildren() != null ? userPersonalInformationStore.getNumberOfChildren() : 0)")
    @Mapping(target = "numberOfDependants", expression = "java(userPersonalInformationStore.getNumberOfDependants() != null ? userPersonalInformationStore.getNumberOfDependants() : 0)")
    @Mapping(target = "nationality", source = "userPersonalInformationStore.nationality")
    @Mapping(target = "placeOfBirth", source = "userPersonalInformationStore.placeOfBirth")
    @Mapping(target = "countryOfBirth", source = "userPersonalInformationStore.countryOfBirth")
    @Mapping(target = "housingType", source = "userAdditionalInformationStore.personalDetails.housingType")
    @Mapping(target = "taxId", source = "userAdditionalInformationStore.personalDetails.taxId")
    PersonalDetails merge(UserPersonalInformationStore userPersonalInformationStore, UserAdditionalInformationStore userAdditionalInformationStore, UserTransactionalDataStore userTransactionalDataStore);


    @Mapping(target = "houseNumber", source = "addressHouseNumber")
    @Mapping(target = "postCode", source = "addressZipCode")
    @Mapping(target = "city", source = "addressCity")
    @Mapping(target = "streetName", source = "addressStreet")
    @Mapping(target = "mobile", source = "mobilePhone")
    ContactData map(UserPersonalInformationStore userPersonalInformationStore);

    /*

   @NotBlank(message = "Birthday must not be null")
    private String birthday;

     */
    @Mapping(target = "firstName", source = "userPersonalInformationStore.firstName")
    @Mapping(target = "lastName", source = "userPersonalInformationStore.lastName")
    @Mapping(target = "gender", source = "userPersonalInformationStore.gender")
    @Mapping(target = "birthplace", source = "userPersonalInformationStore.placeOfBirth")
    @Mapping(target = "street", source = "userPersonalInformationStore.addressStreet")
    @Mapping(target = "houseNumber", source = "userPersonalInformationStore.addressHouseNumber")
    @Mapping(target = "city", source = "userPersonalInformationStore.addressCity")
    @Mapping(target = "zipCode", source = "userPersonalInformationStore.addressZipCode")
    @Mapping(target = "nationality", source = "userPersonalInformationStore.nationality")
    @Mapping(target = "country", expression = "java(\"DE\")")
    @Mapping(target = "mobilePhone", expression = "java(getPhoneNumberFromUserInformations(userPersonalInformationStore, userAdditionalInformationStore))")
    @Mapping(target = "email", expression = "java(getEmailFromUserInformations(userPersonalInformationStore, userAdditionalInformationStore))")
    @Mapping(target = "language", expression = "java(\"de\")")
    @Mapping(target = "loanProvider", source = "loanOfferStore.offer.loanProvider.name")
    @Mapping(target = "applicationId", source = "loanOfferStore.applicationId")
    @Mapping(target = "loanOfferId", source = "loanOfferStore.loanOfferId")
    @Mapping(target = "advertisingConsent", source = "userAdditionalInformationStore", qualifiedByName = "getAdvertisingConsent")
    @Mapping(target = "duration", source = "loanOfferStore.offer.durationInMonth")
    @Mapping(target = "birthday", source = "userPersonalInformationStore.birthDate", qualifiedByName = "birthDateToString")
    CreateIdentRequest from(UserPersonalInformationStore userPersonalInformationStore, LoanOfferStore loanOfferStore, Optional<UserAdditionalInformationStore> userAdditionalInformationStore);

    @Named("getPhoneNumberFromUserInformations")
    default String getPhoneNumberFromUserInformations(UserPersonalInformationStore userPersonalInformationStore, Optional<UserAdditionalInformationStore> userAdditionalInformationStore) {
        if (userPersonalInformationStore != null && userPersonalInformationStore.getMobilePhone() != null) {
            return userPersonalInformationStore.getMobilePhone();
        }

        return userAdditionalInformationStore
                .flatMap(userStore -> Optional.ofNullable(userStore.getContactData()))
                .map(ContactData::getMobile)
                .orElse("");
    }

    @Named("getEmailFromUserInformations")
    default String getEmailFromUserInformations(UserPersonalInformationStore userPersonalInformationStore, Optional<UserAdditionalInformationStore> userAdditionalInformationStore) {
        if (userPersonalInformationStore != null && userPersonalInformationStore.getEmail() != null) {
            return userPersonalInformationStore.getEmail();
        }

        return userAdditionalInformationStore
                .flatMap(userStore -> Optional.ofNullable(userStore.getContactData()))
                .map(ContactData::getEmail)
                .orElse("");
    }

    default ContactData customMerge(ContactData contactData, ContactData extra) {
        try {
            if (contactData == null && extra != null) {
                return extra;
            }
            if (null != contactData && null != extra) {
                ObjectMapper objectMapper = new ObjectMapper();

                JsonNode base = objectMapper.valueToTree(contactData);
                JsonNode addFrom = objectMapper.valueToTree(extra);
                List<String> fileNames = toList(base.fieldNames());
                fileNames.forEach(fieldName -> {
                    JsonNode fieldNode = base.get(fieldName);
                    if (null == fieldNode || fieldNode.isNull()) {
                        ((ObjectNode) base).set(fieldName, addFrom.get(fieldName));
                    }
                });

                return objectMapper.treeToValue(base, ContactData.class);

            }
        } catch (JsonProcessingException e) {
            // ignore
        }

        return contactData;
    }

    @Named("extractHousingType")
    default HousingType extractHousingType(UserTransactionalDataStore userTransactionalDataStore) {
        return Optional.ofNullable(userTransactionalDataStore.getExpenses())
                .map(Expenses::getRent)
                .filter(rent -> rent > 0)
                .map(x -> HousingType.RENT)
                .orElse(null);
    }

    @Named("extractMobilePhone")
    default String extractMobilePhone(String mobilePhone) {
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

    @Named("extractFamilyStatus")
    default FamilyStatus extractFamilyStatus(Map<String, Object> data) {
        return Optional.ofNullable(data).map(d -> d.get("familyStatus"))
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .map(FamilyStatus::fromString)
                .orElse(null);
    }

    @Named("extractGender")
    default Gender extractGender(Map<String, Object> data) {
        return Optional.ofNullable(data).map(d -> d.get("gender"))
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .map(Gender::fromString)
                .orElse(null);
    }

    @Named("extractNationality")
    default Nationality extractNationality(Map<String, Object> data) {
        return Optional.ofNullable(data).map(d -> d.get("nationality"))
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .map(Nationality::fromString)
                .orElse(null);
    }

    @Named("birthDateToString")
    default String birthDateToString(LocalDate localDate) {
        return Optional.ofNullable(localDate)
                .map(l -> l.format(DateTimeFormatter.ISO_LOCAL_DATE))
                .orElse("");
    }

    @Named("getAdvertisingConsent")
    default boolean getAdvertisingConsent(Optional<UserAdditionalInformationStore> userAdditionalInformationStore) {
        return userAdditionalInformationStore
                .map(UserAdditionalInformationStore::getConsentData)
                .map(l -> l.stream().takeWhile(d -> d.getConsentState() == ConsentState.ACCEPTED).collect(Collectors.toList()))
                .map(fl -> !fl.isEmpty())
                .orElse(false);
    }

    @Named("correctMobilePhone")
    default String correctMobilePhone(UserPersonalDetails userPersonalDetails, UserPersonalInformationStore userPersonalInformationStore) {
        if (userPersonalInformationStore != null && userPersonalInformationStore.getMobilePhone() != null) {
            return userPersonalInformationStore.getMobilePhone();
        }

        if (userPersonalDetails != null && userPersonalDetails.getContactData() != null) {
            return userPersonalDetails.getContactData().getMobile();
        }

        return null;
    }

    @Named("correctFirstName")
    default String correctFirstName(UserTransactionalDataStore userTransactionalDataStore, UserAdditionalInformationStore userAdditionalInformationStore) {
        if (userAdditionalInformationStore != null) {
            if (userAdditionalInformationStore.getPersonalDetails() != null) {
                if (userAdditionalInformationStore.getPersonalDetails().getFirstName() != null) {
                    return userAdditionalInformationStore.getPersonalDetails().getFirstName();
                }
            }
        }
        if (userTransactionalDataStore != null) {
            if (userTransactionalDataStore.getCustomDacPersonalDetails() != null) {
                if (userTransactionalDataStore.getCustomDacPersonalDetails() != null) {
                    return userTransactionalDataStore.getCustomDacPersonalDetails().getFirstName();
                }
            }
        }
        return null;
    }


    @Named("correctLastName")
    default String correctLastName(UserTransactionalDataStore userTransactionalDataStore, UserAdditionalInformationStore userAdditionalInformationStore) {
        if (userAdditionalInformationStore != null) {
            if (userAdditionalInformationStore.getPersonalDetails() != null) {
                if (userAdditionalInformationStore.getPersonalDetails().getLastName() != null) {
                    return userAdditionalInformationStore.getPersonalDetails().getLastName();
                }
            }
        }
        if (userTransactionalDataStore != null) {
            if (userTransactionalDataStore.getCustomDacPersonalDetails() != null) {
                if (userTransactionalDataStore.getCustomDacPersonalDetails() != null) {
                    return userTransactionalDataStore.getCustomDacPersonalDetails().getLastName();
                }
            }
        }
        return null;
    }
}
