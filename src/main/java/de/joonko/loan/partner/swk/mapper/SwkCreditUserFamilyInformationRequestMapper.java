package de.joonko.loan.partner.swk.mapper;

import de.joonko.loan.offer.domain.FamilyStatus;
import de.joonko.loan.offer.domain.PersonalDetails;
import de.joonko.loan.partner.swk.stub.CreditApplicationServiceStub;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface SwkCreditUserFamilyInformationRequestMapper {

    @Mapping(target = "maritalStatus", source = "familyStatus", qualifiedByName = "toMaritalStatus")
    @Mapping(target = "numberOfChildren", source = "numberOfChildren")
    @Mapping(target = "longTermRelationship", source = "familyStatus", qualifiedByName = "toLongTermRelationship")
    CreditApplicationServiceStub.FamilyInformation toFamilyInformation(PersonalDetails personalDetails);

    @Named("toMaritalStatus")
    default Integer toMaritalStatus(FamilyStatus familyStatus) {
        switch (familyStatus) {
            case SINGLE:
            case LIVING_IN_LONGTERM_RELATIONSHIP:
                return 1;
            case MARRIED:
                return 2;
            case WIDOWED:
                return 3;
            case DIVORCED:
                return 4;
            case LIVING_SEPARATELY:
                return 5;
        }
        return null;
    }

    @Named("toLongTermRelationship")
    default boolean toLongTermRelationship(FamilyStatus familyStatus) {
        return familyStatus == FamilyStatus.LIVING_IN_LONGTERM_RELATIONSHIP;
    }
}