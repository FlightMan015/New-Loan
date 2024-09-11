package de.joonko.loan.partner.swk.mapper;

import de.joonko.loan.offer.domain.FamilyStatus;
import de.joonko.loan.offer.domain.PersonalDetails;
import de.joonko.loan.partner.auxmoney.getoffers.BaseMapperTest;
import de.joonko.loan.partner.swk.stub.CreditApplicationServiceStub;
import io.github.glytching.junit.extension.random.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;


class SwkCreditUserFamilyInformationRequestMapperTest extends BaseMapperTest {

    @Autowired
    SwkCreditUserFamilyInformationRequestMapper mapper;

    @Random
    PersonalDetails personalDetails;

    @Test
    void toFamilyInformation() {
        CreditApplicationServiceStub.FamilyInformation familyInformation = mapper.toFamilyInformation(personalDetails);
        assertNotNull(familyInformation.getMaritalStatus());
        assertNotNull(familyInformation.getNumberOfChildren());
        assertNotNull(familyInformation.getLongTermRelationship());

    }

    @DisplayName("All family status mapped correctly to the SWK")
    @ParameterizedTest(name = "{0} maps to {1}")
    @MethodSource("familyStatusProvider")
    void mapFamilyStatusProvider(FamilyStatus status, int swkMap) {
        personalDetails.setFamilyStatus(status);
        CreditApplicationServiceStub.FamilyInformation familyInformation = mapper.toFamilyInformation(personalDetails);
        assertEquals(swkMap, familyInformation.getMaritalStatus());
        assertEquals(status, personalDetails.getFamilyStatus());
    }

    static Stream<Arguments> familyStatusProvider() {
        return Stream.of(
                arguments(FamilyStatus.SINGLE, 1),
                arguments(FamilyStatus.MARRIED, 2),
                arguments(FamilyStatus.WIDOWED, 3),
                arguments(FamilyStatus.DIVORCED, 4),
                arguments(FamilyStatus.LIVING_SEPARATELY, 5),
                arguments(FamilyStatus.LIVING_IN_LONGTERM_RELATIONSHIP, 1)
        );
    }

    @Test
    void toNumberOfChildren() {
        CreditApplicationServiceStub.FamilyInformation familyInformation = mapper.toFamilyInformation(personalDetails);
        assertEquals(personalDetails.getNumberOfChildren(), familyInformation.getNumberOfChildren());
    }

    @Test
    void toLongTermRelationshipTrue() {
        personalDetails.setFamilyStatus(FamilyStatus.LIVING_IN_LONGTERM_RELATIONSHIP);
        CreditApplicationServiceStub.FamilyInformation familyInformation = mapper.toFamilyInformation(personalDetails);
        assertTrue(familyInformation.getLongTermRelationship());
    }

    @Test
    void toLongTermRelationshipfalse() {
        personalDetails.setFamilyStatus(FamilyStatus.SINGLE);
        CreditApplicationServiceStub.FamilyInformation familyInformation = mapper.toFamilyInformation(personalDetails);
        assertFalse(familyInformation.getLongTermRelationship());
    }
}