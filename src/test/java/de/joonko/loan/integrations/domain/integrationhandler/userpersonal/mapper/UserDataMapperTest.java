package de.joonko.loan.integrations.domain.integrationhandler.userpersonal.mapper;

import de.joonko.loan.integrations.domain.integrationhandler.userpersonal.UserPersonalData;
import de.joonko.loan.partner.auxmoney.getoffers.BaseMapperTest;
import de.joonko.loan.userdata.api.mapper.UserDataMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import io.github.glytching.junit.extension.random.Random;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UserDataMapperTest extends BaseMapperTest {

    @Autowired
    private UserDataMapper userDataMapper;

    private static final String USER_ID = "2f20a660-f0f2-4ca5-9fe6-b24b52cd1070";

    @Test
    void toUserData(@Random UserPersonalData userPersonalData) {
        final var userData = userDataMapper.fromUserPersonalData(userPersonalData);

        assertAll(
                () -> assertEquals(userData.getUserPersonal().getFirstName(), userPersonalData.getPersonalDetails().getFirstName()),
                () -> assertEquals(userData.getUserPersonal().getLastName(), userPersonalData.getPersonalDetails().getLastName()),
                () -> assertEquals(userData.getUserPersonal().getBirthDate(), userPersonalData.getPersonalDetails().getBirthDate()),
                () -> assertEquals(userData.getUserPersonal().getNationality(), userPersonalData.getPersonalDetails().getNationality()),
                () -> assertEquals(userData.getUserPersonal().getCountryOfBirth(), userPersonalData.getPersonalDetails().getCountryOfBirth()),
                () -> assertEquals(userData.getUserPersonal().getPlaceOfBirth(), userPersonalData.getPersonalDetails().getPlaceOfBirth()),
                () -> assertEquals(userData.getUserPersonal().getFamilyStatus(), userPersonalData.getPersonalDetails().getFamilyStatus()),
                () -> assertEquals(userData.getUserPersonal().getGender(), userPersonalData.getPersonalDetails().getGender()),

                () -> assertEquals(userData.getUserContact().getEmail(), userPersonalData.getContactData().getEmail()),
                () -> assertEquals(userData.getUserContact().getMobile(), userPersonalData.getContactData().getMobile()),
                () -> assertEquals(userData.getUserContact().getCity(), userPersonalData.getContactData().getCity()),
                () -> assertEquals(userData.getUserContact().getStreetName(), userPersonalData.getContactData().getStreetName()),
                () -> assertEquals(userData.getUserContact().getPostCode(), userPersonalData.getContactData().getPostCode()),
                () -> assertEquals(userData.getUserContact().getLivingSince(), userPersonalData.getContactData().getLivingSince()),
                () -> assertEquals(userData.getUserContact().getPreviousAddress().getCity(), userPersonalData.getContactData().getPreviousAddress().getCity()),
                () -> assertEquals(userData.getUserContact().getPreviousAddress().getCountry(), userPersonalData.getContactData().getPreviousAddress().getCountry()),
                () -> assertEquals(userData.getUserContact().getPreviousAddress().getStreetName(), userPersonalData.getContactData().getPreviousAddress().getStreetName()),
                () -> assertEquals(userData.getUserContact().getPreviousAddress().getPostCode(), userPersonalData.getContactData().getPreviousAddress().getPostCode()),
                () -> assertEquals(userData.getUserContact().getPreviousAddress().getHouseNumber(), userPersonalData.getContactData().getPreviousAddress().getHouseNumber()),
                () -> assertEquals(userData.getUserContact().getPreviousAddress().getLivingSince(), userPersonalData.getContactData().getPreviousAddress().getLivingSince()),

                () -> assertEquals(userData.getUserEmployment().getEmployerName(), userPersonalData.getEmploymentDetails().getEmployerName()),
                () -> assertEquals(userData.getUserEmployment().getEmploymentType(), userPersonalData.getEmploymentDetails().getEmploymentType()),
                () -> assertEquals(userData.getUserEmployment().getEmploymentSince(), userPersonalData.getEmploymentDetails().getEmploymentSince()),
                () -> assertEquals(userData.getUserEmployment().getProfessionEndDate(), userPersonalData.getEmploymentDetails().getProfessionEndDate()),
                () -> assertEquals(userData.getUserEmployment().getCity(), userPersonalData.getEmploymentDetails().getCity()),
                () -> assertEquals(userData.getUserEmployment().getStreetName(), userPersonalData.getEmploymentDetails().getStreetName()),
                () -> assertEquals(userData.getUserEmployment().getHouseNumber(), userPersonalData.getEmploymentDetails().getHouseNumber()),
                () -> assertEquals(userData.getUserEmployment().getPostCode(), userPersonalData.getEmploymentDetails().getPostCode()),

                () -> assertEquals(userData.getUserHousing().getHousingType(), userPersonalData.getPersonalDetails().getHousingType()),
                () -> assertEquals(userData.getUserHousing().getNumberOfChildren(), userPersonalData.getPersonalDetails().getNumberOfChildren()),
                () -> assertEquals(userData.getUserHousing().getNumberOfDependants(), userPersonalData.getPersonalDetails().getNumberOfDependants())
        );
    }

}
