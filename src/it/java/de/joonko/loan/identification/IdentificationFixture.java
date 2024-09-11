package de.joonko.loan.identification;

import de.joonko.loan.identification.model.CreateIdentRequest;
import de.joonko.loan.identification.model.InitiateIdentificationRequest;
import de.joonko.loan.offer.api.Gender;
import de.joonko.loan.offer.api.Nationality;
import de.joonko.loan.user.service.UserPersonalInformationStore;

import java.time.LocalDate;
import java.util.UUID;

public class IdentificationFixture {

    public static CreateIdentRequest getCreateIdentRequest(String loanProvider) {
        return CreateIdentRequest.builder()
                .loanOfferId(UUID.randomUUID().toString())
                .applicationId(UUID.randomUUID().toString())
                .birthday("1987-05-23")
                .birthplace("London")
                .city("London")
                .country("GB")
                .email("someOne@joonko.io")
                .firstName("Olivia")
                .lastName("Jones")
                .gender("MALE")
                .mobilePhone("4901789012345")
                .nationality("GB")
                .street("Baker Street")
                .zipCode("W1U")
                .loanProvider(loanProvider)
                .language("EN")
                .build();
    }

    public static InitiateIdentificationRequest getInitiateIdentificationRequest() {
        return InitiateIdentificationRequest.builder()
                .applicationId(UUID.randomUUID().toString())
                .loanOfferId(UUID.randomUUID().toString())
                .build();
    }

    public static UserPersonalInformationStore getUserPersonalInformationStore(String userUuid) {
        UserPersonalInformationStore userPersonalInformationStore = new UserPersonalInformationStore();
        userPersonalInformationStore.setFirstName("Olivia");
        userPersonalInformationStore.setLastName("Olivia");
        userPersonalInformationStore.setGender(Gender.MALE);
        userPersonalInformationStore.setPlaceOfBirth("London");
        userPersonalInformationStore.setAddressStreet("Baker Street");
        userPersonalInformationStore.setAddressHouseNumber("123");
        userPersonalInformationStore.setAddressCity("London");
        userPersonalInformationStore.setAddressZipCode("W1U");
        userPersonalInformationStore.setNationality(Nationality.GB);
        userPersonalInformationStore.setMobilePhone("01789012345");
        userPersonalInformationStore.setEmail("someOne@joonko.io");
        userPersonalInformationStore.setBirthDate(LocalDate.of(1987, 5, 23));
        userPersonalInformationStore.setUserUUID(userUuid);

        return userPersonalInformationStore;
    }
}
