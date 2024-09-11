package de.joonko.loan.identification.idnow;

import de.joonko.loan.identification.mapper.idnow.AionCreateIdentRequestMapper;
import de.joonko.loan.identification.model.CreateIdentRequest;
import de.joonko.loan.partner.auxmoney.getoffers.BaseMapperTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AionCreateIdentRequestMapperTest extends BaseMapperTest {

    @Autowired
    private AionCreateIdentRequestMapper aionCreateIdentRequestMapper;

    private static final String LAST_NAME = "Tabbitha";
    private static final String REPRESENTATIVE_ID = "c68ad88d-88a9-4b8d-b0b9-a9f5cd1ad2bb";

    @Test
    void mapRepresentativeId() {
        // given
        var createIdentRequest = CreateIdentRequest.builder()
                .lastName(LAST_NAME)
                .build();

        // when
        var actualResponse = aionCreateIdentRequestMapper.toIdNowCreateIdentRequest(createIdentRequest, REPRESENTATIVE_ID);

        // then
        assertAll(
                () -> assertEquals(REPRESENTATIVE_ID, actualResponse.getCustom1()),
                () -> assertEquals(LAST_NAME, actualResponse.getLastName())
        );
    }
}
