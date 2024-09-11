package de.joonko.loan.partner.creditPlus.mapper;

import de.joonko.loan.offer.domain.EmploymentDetails;
import de.joonko.loan.partner.creditPlus.CreditPlusDefaults;
import de.joonko.loan.partner.creditPlus.stub.EfinComparerServiceStub;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDate;

@Mapper(componentModel = "spring")
public interface CreditPlusDebtorWorkDataMapper {

    @Mapping(target = "category", constant = CreditPlusDefaults.CATEGORY)
    @Mapping(target = "industry", constant = CreditPlusDefaults.INDUSTRY)
    @Mapping(target = "job", ignore = true)
    @Mapping(target = "employerName", source = "employerName")
    @Mapping(target = "employerStreet", source = "streetName")
    @Mapping(target = "employerPostalCode", source = "zipCode.code")
    @Mapping(target = "employerCity", source = "city")
    @Mapping(target = "employedDate", source = "employmentSince")
    @Mapping(target = "employeeLimitation", source = "professionEndDate", qualifiedByName = "getEmployeeLimitation")
    @Mapping(target = "dateOfEmployeeLimitation", source = "professionEndDate")
    EfinComparerServiceStub.WorkData toWorkData(EmploymentDetails employmentDetails);

    @Named("getEmployeeLimitation")
    default boolean getEmployeeLimitation(LocalDate professionEndDate) {
        return professionEndDate != null;
    }

}
