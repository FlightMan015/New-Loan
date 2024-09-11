package de.joonko.loan.partner.auxmoney.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.joonko.loan.offer.domain.ContactData;
import de.joonko.loan.offer.domain.Email;
import de.joonko.loan.offer.domain.ZipCode;
import de.joonko.loan.partner.auxmoney.getoffers.BaseMapperTest;
import de.joonko.loan.partner.auxmoney.model.BorrowerContactData;
import de.joonko.loan.util.JsonUtil;
import io.github.glytching.junit.extension.random.Random;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;


class AuxmoneyContactDataMapperTest extends BaseMapperTest {

    @Autowired
    AuxmoneyContactDataMapper mapper;

    @Test
    @DisplayName("should convert contact data to auxmoney borrowerContactData")
    void toAuxmoneyContactData(@Random ContactData contactData) {

        BorrowerContactData borrowerContactData = mapper.toAuxmoneyContactData(contactData);
        Assert.assertEquals(contactData.getLivingSince(), borrowerContactData.getLivingSince());
        Assert.assertEquals(contactData.getStreetName(), borrowerContactData.getStreetName());
        Assert.assertEquals(contactData.getStreetNumber(), borrowerContactData.getStreetNumber());
        Assert.assertEquals(contactData.getZipCode()
                .getCode(), borrowerContactData.getZipCode());
        Assert.assertEquals(contactData.getCity(), borrowerContactData.getCity());
        Assert.assertEquals(contactData.getMobile(), borrowerContactData.getTelephone());


    }

    @Test
    void email(@Random ContactData contactData) {
        contactData.setEmail(new Email("someOne@joonko.io"));
        BorrowerContactData borrowerContactData = mapper.toAuxmoneyContactData(contactData);
        Assert.assertEquals("someOne@joonko.io", borrowerContactData.getEmail());
    }

    @Nested
    class MobileNumber {
        @Test
        @DisplayName("Should convert to auxmoney mobile format ")
        void toAuxmoneyMobileNumber(@Random ContactData contactData) {
            contactData.setMobile("491748273421011");
            BorrowerContactData borrowerContactData = mapper.toAuxmoneyContactData(contactData);
            Assert.assertEquals("491748273421011", borrowerContactData.getMobileTelephone());
        }
    }


    @Test
    @DisplayName("should convert contact data to auxmoney borrowerContactData with correct expected format")
    void toAuxmoneyContactDataJsonFormat() throws JsonProcessingException, JSONException {
        String expectedJson = "{\n" +
                "\"living_since\" : \"2010-01-01\" ,\n" +
                "\"street_name\" : \"Königsallee\" ,\n" +
                "\"street_number\" : \"60 F\" ,\n" +
                "\"zip_code\" : \"40212\" ,\n" +
                "\"city\" : \"Düsseldorf\" ,\n" +
                "\"telephone\" : \"491748273421011\" ,\n" +
                "\"mobile_telephone\" : \"491748273421011\" ,\n" +
                "\"email\" : \"someOne@joonko.io\"\n" +
                "}";
        ContactData build = new ContactData("Düsseldorf", "Königsallee", "60 F", new ZipCode("40212"), LocalDate.of(2010, 1, 1), null, new Email("someOne@joonko.io"), "491748273421011");
        BorrowerContactData borrowerContactData = mapper.toAuxmoneyContactData(build);
        String auxmoneyRequestAsJson = JsonUtil.getObjectAsJsonString(borrowerContactData);
        JSONAssert.assertEquals(expectedJson, auxmoneyRequestAsJson, JSONCompareMode.LENIENT);

    }


    @Test
    @DisplayName("should convert email object to default email")
    void convertToEmail(@Random ContactData contactData) {
        BorrowerContactData borrowerContactData = mapper.toAuxmoneyContactData(contactData);
        Assert.assertEquals(borrowerContactData.getEmail(),
                borrowerContactData.getEmail());
    }

    @Test
    @DisplayName("should convert leavingSince")
    void leavingSince(@Random ContactData contactData) {
        contactData.setLivingSince(LocalDate.of(2020, 02, 01));
        BorrowerContactData borrowerContactData = mapper.toAuxmoneyContactData(contactData);
        Assert.assertNotNull(borrowerContactData.getLivingSince());
        Assert.assertEquals(LocalDate.of(2020, 02, 01), borrowerContactData.getLivingSince());
    }
}
