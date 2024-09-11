package de.joonko.loan.identification.idnow;

import de.joonko.loan.identification.IdentificationFixture;
import de.joonko.loan.identification.config.IdentificationPropConfig;
import de.joonko.loan.identification.mapper.idnow.ConsorsCreateIdentRequestMapper;
import de.joonko.loan.identification.model.CreateIdentRequest;
import de.joonko.loan.partner.auxmoney.getoffers.BaseMapperTest;
import de.joonko.loan.partner.consors.ConsorsPropertiesConfig;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;


public class ConsorsCreateIdentRequestMapperTest extends BaseMapperTest {

    @Autowired
    private ConsorsCreateIdentRequestMapper consorsCreateIdentRequestMapper;
    @Autowired
    private IdentificationPropConfig identificationPropConfig;
    @Autowired
    private ConsorsPropertiesConfig consorsPropertiesConfig;

    @Test
    void personalData() {
        CreateIdentRequest consorsFinanz = IdentificationFixture.getCreateIdentRequest("Consors Finanz");
        de.joonko.loan.identification.model.idnow.CreateIdentRequest createIdentRequest = consorsCreateIdentRequestMapper.toIdNowCreateIdentRequest(consorsFinanz);
        assertEquals(consorsFinanz.getBirthday(), createIdentRequest.getBirthday());
        assertEquals(consorsFinanz.getBirthplace(), createIdentRequest.getBirthplace());
        assertEquals(consorsFinanz.getCity(), createIdentRequest.getCity());
        assertEquals(consorsFinanz.getCountry(), createIdentRequest.getCountry());
        assertEquals(consorsFinanz.getEmail(), createIdentRequest.getEmail());
        assertEquals(consorsFinanz.getFirstName(), createIdentRequest.getFirstName());
        assertEquals(consorsFinanz.getLastName(), createIdentRequest.getLastName());
        assertEquals(consorsFinanz.getGender(), createIdentRequest.getGender());
        assertEquals(consorsFinanz.getStreet(), createIdentRequest.getStreet());
        assertEquals(consorsFinanz.getHouseNumber(), createIdentRequest.getHouseNumber());
        assertEquals(consorsFinanz.getZipCode(), createIdentRequest.getZipCode());
        assertEquals("+4901789012345", createIdentRequest.getMobilePhone());

    }

    @Nested
    class customData {
        @Test
        void customData1() {
            identificationPropConfig.setAutoidentification(false);
            consorsPropertiesConfig.setPartnerId("2620987");
            CreateIdentRequest consorsFinanz = IdentificationFixture.getCreateIdentRequest("Consors Finanz");
            de.joonko.loan.identification.model.idnow.CreateIdentRequest createIdentRequest = consorsCreateIdentRequestMapper.toIdNowCreateIdentRequest(consorsFinanz);
            assertEquals("2620987", createIdentRequest.getCustom1());
        }

        @Test
        void customData1Autoidentification() {
            identificationPropConfig.setAutoidentification(true);
            CreateIdentRequest consorsFinanz = IdentificationFixture.getCreateIdentRequest("Consors Finanz");
            de.joonko.loan.identification.model.idnow.CreateIdentRequest createIdentRequest = consorsCreateIdentRequestMapper.toIdNowCreateIdentRequest(consorsFinanz);
            assertEquals("X-MANUALTEST-HAPPYPATH", createIdentRequest.getCustom1());
        }

        @Test
        void customData2() {
            CreateIdentRequest consorsFinanz = IdentificationFixture.getCreateIdentRequest("Consors Finanz");
            de.joonko.loan.identification.model.idnow.CreateIdentRequest createIdentRequest = consorsCreateIdentRequestMapper.toIdNowCreateIdentRequest(consorsFinanz);
            assertEquals("100", createIdentRequest.getCustom2());
        }

        @Test
        void customData3() {
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");

            String dateInString = format.format(new Date());

            CreateIdentRequest consorsFinanz = IdentificationFixture.getCreateIdentRequest("Consors Finanz");
            de.joonko.loan.identification.model.idnow.CreateIdentRequest createIdentRequest = consorsCreateIdentRequestMapper.toIdNowCreateIdentRequest(consorsFinanz);
            assertEquals(dateInString, createIdentRequest.getCustom3());
        }

        @Test
        void customData4() {

            CreateIdentRequest consorsFinanz = IdentificationFixture.getCreateIdentRequest("Consors Finanz");
            de.joonko.loan.identification.model.idnow.CreateIdentRequest createIdentRequest = consorsCreateIdentRequestMapper.toIdNowCreateIdentRequest(consorsFinanz);
            assertEquals("181", createIdentRequest.getCustom4());
        }

        @Test
        void customData5() {

            CreateIdentRequest consorsFinanz = IdentificationFixture.getCreateIdentRequest("Consors Finanz");
            de.joonko.loan.identification.model.idnow.CreateIdentRequest createIdentRequest = consorsCreateIdentRequestMapper.toIdNowCreateIdentRequest(consorsFinanz);
            assertEquals("0", createIdentRequest.getCustom5());
        }


    }

}
