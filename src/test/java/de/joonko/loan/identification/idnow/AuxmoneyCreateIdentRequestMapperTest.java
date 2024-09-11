package de.joonko.loan.identification.idnow;

import de.joonko.loan.identification.IdentificationFixture;
import de.joonko.loan.identification.config.IdentificationPropConfig;
import de.joonko.loan.identification.mapper.idnow.AuxmoneyCreateIdentRequestMapper;
import de.joonko.loan.identification.model.CreateIdentRequest;
import de.joonko.loan.partner.auxmoney.getoffers.BaseMapperTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

class AuxmoneyCreateIdentRequestMapperTest extends BaseMapperTest {

    @Autowired
    private AuxmoneyCreateIdentRequestMapper auxmoneyCreateIdentRequestMapper;

    @Autowired
    private IdentificationPropConfig identificationPropConfig;

    @Test
    void personalData() {
        CreateIdentRequest auxmoneyRequest = IdentificationFixture.getCreateIdentRequest("Auxmoney");
        de.joonko.loan.identification.model.idnow.CreateIdentRequest createIdentRequest = auxmoneyCreateIdentRequestMapper.toIdNowCreateIdentRequest(auxmoneyRequest);
        assertEquals(auxmoneyRequest.getBirthday(), createIdentRequest.getBirthday());
        assertEquals(auxmoneyRequest.getBirthplace(), createIdentRequest.getBirthplace());
        assertEquals(auxmoneyRequest.getCity(), createIdentRequest.getCity());
        assertEquals(auxmoneyRequest.getCountry(), createIdentRequest.getCountry());
        assertEquals(auxmoneyRequest.getEmail(), createIdentRequest.getEmail());
        assertEquals(auxmoneyRequest.getFirstName(), createIdentRequest.getFirstName());
        assertEquals(auxmoneyRequest.getLastName(), createIdentRequest.getLastName());
        assertEquals(auxmoneyRequest.getGender(), createIdentRequest.getGender());
        assertEquals(auxmoneyRequest.getStreet(), createIdentRequest.getStreet());
        assertEquals(auxmoneyRequest.getZipCode(), createIdentRequest.getZipCode());

    }

    @Nested
    class customData {
        @Test
        void customData1() {
            identificationPropConfig.setAutoidentification(false);
            CreateIdentRequest auxmoneyRequest = IdentificationFixture.getCreateIdentRequest("Auxmoney");
            de.joonko.loan.identification.model.idnow.CreateIdentRequest createIdentRequest = auxmoneyCreateIdentRequestMapper.toIdNowCreateIdentRequest(auxmoneyRequest);
            assertNull(createIdentRequest.getCustom1());
        }

        @Test
        void customData1Autoidentification() {
            identificationPropConfig.setAutoidentification(true);
            CreateIdentRequest auxmoneyRequest = IdentificationFixture.getCreateIdentRequest("Auxmoney");
            de.joonko.loan.identification.model.idnow.CreateIdentRequest createIdentRequest = auxmoneyCreateIdentRequestMapper.toIdNowCreateIdentRequest(auxmoneyRequest);
            assertEquals("X-MANUALTEST-HAPPYPATH", createIdentRequest.getCustom1());
        }
    }

}
