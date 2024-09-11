package de.joonko.loan.partner.auxmoney.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.joonko.loan.offer.domain.*;
import de.joonko.loan.partner.auxmoney.getoffers.BaseMapperTest;
import de.joonko.loan.partner.auxmoney.model.PersonalData;
import de.joonko.loan.util.JsonUtil;
import io.github.glytching.junit.extension.random.Random;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class AuxmoneyPersonalDataMapperTest extends BaseMapperTest {


    @Autowired
    AuxmoneyPersonalDataMapper mapper;

    @Test
    @DisplayName("Should Convert Address ")
    void shouldConvertAddress(@Random PersonalDetails personalDetails) {
        PersonalData personalData = mapper.toAuxmoneyPersonalData(personalDetails);
        Assert.assertNotNull(personalData.getAddress());

    }

    @Test
    @DisplayName("Should Convert First Name ")
    void shouldConvertFirstName(@Random PersonalDetails personalDetails) {

        PersonalData personalData = mapper.toAuxmoneyPersonalData(personalDetails);
        Assert.assertEquals(personalDetails.getFirstName(), personalData.getForename());

    }

    @Test
    @DisplayName("Should Convert surname")
    void shouldConvertSurname(@Random PersonalDetails personalDetails) {
        PersonalData personalData = mapper.toAuxmoneyPersonalData(personalDetails);
        Assert.assertEquals(personalDetails.getLastName(), personalData.getSurname());
    }

    @Test
    @DisplayName("Should Convert Family")
    void shouldConvertFamily(@Random PersonalDetails personalDetails) {
        PersonalData personalData = mapper.toAuxmoneyPersonalData(personalDetails);
        Assert.assertNotNull(personalData.getFamilyStatus());
    }

    @Test
    @DisplayName("Should Convert Birth Date")
    void shouldConvertBirthDate(@Random PersonalDetails personalDetails) {
        PersonalData personalData = mapper.toAuxmoneyPersonalData(personalDetails);
        Assert.assertEquals(personalDetails.getBirthDate(), personalData.getBirthDate());
    }

    @Nested
    class NationalityTest {
        @Test
        @DisplayName("Should Convert nationality to Germany")
        void shouldConvertNationality(@Random PersonalDetails personalDetails) {
            personalDetails.setNationality(Nationality.DE);
            PersonalData personalData = mapper.toAuxmoneyPersonalData(personalDetails);
            Assert.assertEquals("DE", personalData.getNationality());
        }

        @ParameterizedTest(name = "Should map nationality [{arguments}]")
        @EnumSource(Nationality.class)
        void allFamilyStatus(Nationality nationality, @Random PersonalDetails personalDetails) {
            personalDetails.setNationality(nationality);
            assertNotNull(mapper.toAuxmoneyPersonalData(personalDetails));
        }
    }


    @Test
    @DisplayName("Should Convert has_credit_card  ")
    void shouldConvertHasCreditCard(@Random PersonalDetails personalDetails) {
        PersonalData personalData = mapper.toAuxmoneyPersonalData(personalDetails);
        if (personalDetails.getNumberOfCreditCard() >= 1) {
            Assert.assertEquals(Integer.valueOf(1), personalData.getHasCreditCard());
        } else {
            Assert.assertEquals(Integer.valueOf(0), personalData.getHasCreditCard());
        }

    }

    @Test
    @DisplayName("Should Convert has_ec_card  ")
    void shouldConvertHasEcCard(@Random PersonalDetails personalDetails) {
        PersonalData personalData = mapper.toAuxmoneyPersonalData(personalDetails);
        Assert.assertEquals(Integer.valueOf(1), personalData.getHasEcCard());
    }

    @Test
    @DisplayName("Should Convert has real estate  ")
    void shouldConvertHasRealEstate(@Random PersonalDetails personalDetails) {
        PersonalData personalData = mapper.toAuxmoneyPersonalData(personalDetails);
        if (personalDetails.hasRealEstate()) {
            Assert.assertEquals(Integer.valueOf(1), personalData.getHasRealEstate());
        } else {
            Assert.assertEquals(Integer.valueOf(0), personalData.getHasRealEstate());
        }
    }

    @Test
    @DisplayName("Should Convert housing_type")
    void shouldConvertHasHousingType(@Random PersonalDetails personalDetails) {
        PersonalData personalData = mapper.toAuxmoneyPersonalData(personalDetails);
        Assert.assertNotNull(personalData.getHousingType());
    }

    @Test
    @DisplayName("Should correctly format Data")
    void shouldConvertValuesInCorrectFormat() throws JsonProcessingException, JSONException {
        String expectedJson = "{ \n" +
                "   \"address\":1,\n" +
                "   \"forename\":\"Max\",\n" +
                "   \"surname\":\"model man\",\n" +
                "   \"family_status\":2,\n" +
                "   \"birth_date\":\"1969-01-01\",\n" +
                "   \"nationality\":\"DE\",\n" +
                "   \"occupation\":2,\n" +
                "   \"has_credit_card\":1,\n" +
                "   \"has_ec_card\":1,\n" +
                "   \"has_real_estate\":1,\n" +
                "   \"main_earner\":1,\n" +
                "   \"housing_type\":1,\n" +
                "   \"car_owner\":0,\n" +
                "   \"tax_identification_number\":5961420893s8\n" +
                "}";

        PersonalDetails personalDetails = PersonalDetails.builder()
                .gender(Gender.MALE)
                .firstName("Max")
                .lastName("model man")
                .nationality(Nationality.DE)
                .familyStatus(FamilyStatus.MARRIED)
                .birthDate(LocalDate.of(1969, 01, 01))
                .numberOfCreditCard(1)
                .mainEarner(DomainDefault.MAIN_EARNER)
                .housingType(HousingType.OWNER)
                .finance(new Finance(Income.builder().build(), Expenses.builder().build(), BigDecimal.TEN))
                .build();

        PersonalData personalData = mapper.toAuxmoneyPersonalData(personalDetails);
        String auxmoneyRequestAsJson = JsonUtil.getObjectAsJsonString(personalData);

        JSONAssert.assertEquals(auxmoneyRequestAsJson, expectedJson, JSONCompareMode.LENIENT);
    }
}
