package de.joonko.loan.customer.support.mapper;

import de.joonko.loan.customer.support.model.User;
import de.joonko.loan.offer.domain.LoanDemand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "loanApplicationId", expression = "java(loanDemand.getLoanApplicationId())")
    @Mapping(target = "loanAsked", source = "loanAsked")
    @Mapping(target = "email", source = "contactData.email.emailString")
    @Mapping(target = "phone", source = "contactData.mobile")
    @Mapping(target = "firstName", source = "personalDetails.firstName")
    @Mapping(target = "lastName", source = "personalDetails.lastName")
    @Mapping(target = "houseNumber", source = "contactData.streetNumber")
    @Mapping(target = "street", source = "contactData.streetName")
    @Mapping(target = "city", source = "contactData.city")
    @Mapping(target = "postalCode", source = "contactData.zipCode.code")
    @Mapping(target = "payment_date", constant = "1")
    User mapToUser(LoanDemand loanDemand);
}
