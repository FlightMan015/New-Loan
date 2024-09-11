package de.joonko.loan.partner.swk.mapper;

import de.joonko.loan.offer.domain.Nationality;
import de.joonko.loan.offer.domain.PersonalDetails;
import de.joonko.loan.partner.auxmoney.getoffers.BaseMapperTest;
import de.joonko.loan.partner.swk.stub.CreditApplicationServiceStub;
import io.github.glytching.junit.extension.random.Random;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class SwkCreditUserResidencyInformationRequestMapperTest extends BaseMapperTest {

    @Autowired
    SwkCreditUserResidencyInformationRequestMapper mapper;
    @Random
    PersonalDetails personalDetails;

    @Test
    void citizenshipDE() {
        personalDetails.setNationality(Nationality.DE);
        CreditApplicationServiceStub.ResidencyInformation residencyInformation = mapper.toResidency(personalDetails);
        assertEquals(residencyInformation.getCitizenship(), Nationality.DE.getCountryCode().getAlpha2());
    }

    @ParameterizedTest(name = "Should map nationality [{arguments}]")
    @EnumSource(Nationality.class)
    void allNationaltyNotNull(Nationality nationality) {
        personalDetails.setNationality(nationality);
        assertNotNull(mapper.toResidency(personalDetails));
    }

    @Test
    void limitedResidencePermit() {
        CreditApplicationServiceStub.ResidencyInformation residencyInformation = mapper.toResidency(personalDetails);
        assertFalse(residencyInformation.getLimitedResidencePermit());
    }

    @Test
    void residencePermitUntilDate() {
        CreditApplicationServiceStub.ResidencyInformation residencyInformation = mapper.toResidency(personalDetails);
        assertNull(residencyInformation.getResidencePermitUntilDate());
    }
}