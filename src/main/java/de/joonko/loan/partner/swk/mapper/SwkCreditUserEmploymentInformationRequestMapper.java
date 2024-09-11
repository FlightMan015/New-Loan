package de.joonko.loan.partner.swk.mapper;

import de.joonko.loan.offer.domain.EmploymentDetails;
import de.joonko.loan.offer.domain.EmploymentType;
import de.joonko.loan.partner.swk.SwkDefaults;
import de.joonko.loan.partner.swk.stub.CreditApplicationServiceStub;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDate;


@Mapper(componentModel = "spring")
public interface SwkCreditUserEmploymentInformationRequestMapper {

    @Mapping(target = "occupationGroup", source = "employmentType", qualifiedByName = "getOccupationGroup")
    @Mapping(target = "employedSinceDate", source = "employmentSince")
    @Mapping(target = "temporaryUntil", source = "professionEndDate")
    @Mapping(target = "temporary", source = "professionEndDate", qualifiedByName = "getTemporaryMapping")
    @Mapping(target = "employerName", source = "employerName")
    @Mapping(target = "employerAddress.street", source = "streetName")
    @Mapping(target = "employerAddress.housenumber", source = "houseNumber")
    @Mapping(target = "employerAddress.zipcode", source = "zipCode.code")
    @Mapping(target = "employerAddress.city", source = "city")
    @Mapping(target = "employerChangePlanned", constant = SwkDefaults.SWK_FALSE)
    @Mapping(target = "employerPhone", ignore = true)
    @Mapping(target = "inProbationaryPeriod", constant = SwkDefaults.SWK_FALSE)
    CreditApplicationServiceStub.EmploymentInformation toEmploymentInformation(EmploymentDetails employmentDetails);

    @Named("getTemporaryMapping")
    default boolean getTemporaryMapping(LocalDate professionEndDate) {
        return professionEndDate != null;
    }

    @Named("getOccupationGroup")
    default Integer getOccupationGroup(EmploymentType employmentType) {
        return employmentType.equals(EmploymentType.REGULAR_EMPLOYED) ? 3 : 21;
    }
}
