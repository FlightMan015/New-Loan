package de.joonko.loan.partner.swk.mapper;

import de.joonko.loan.offer.domain.Gender;
import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.partner.swk.SwkDefaults;
import de.joonko.loan.partner.swk.stub.CreditApplicationServiceStub;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface SwkCreditUserPersonRequestMapper {

    @Mapping(target = "birthDate", source = "personalDetails.birthDate")
    @Mapping(target = "birthPlace", source = "personalDetails.placeOfBirth")
    @Mapping(target = "email", source = "contactData.email.emailString")
    @Mapping(target = "firstName", source = "personalDetails.firstName")
    @Mapping(target = "lastName", source = "personalDetails.lastName")
    @Mapping(target = "mobile", source = "contactData.mobile")
    @Mapping(target = "phone", ignore = true)
    @Mapping(target = "gender", source = "personalDetails.gender", qualifiedByName = "toGender")
    @Mapping(target = "homeAddress.street", source = "contactData.streetName")
    @Mapping(target = "homeAddress.housenumber", source = "contactData.streetNumber")
    @Mapping(target = "homeAddress.city", source = "contactData.city")
    @Mapping(target = "homeAddress.zipcode", source = "contactData.zipCode.code")
    CreditApplicationServiceStub.Person toPerson(LoanDemand loanDemand);

    @Named("toGender")
    default String toGender(Gender gender) {
        return gender.equals(Gender.MALE) ? SwkDefaults.SWK_MALE : SwkDefaults.SWK_FEMALE;
    }
}