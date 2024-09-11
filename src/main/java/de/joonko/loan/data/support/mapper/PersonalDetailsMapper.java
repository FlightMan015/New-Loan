package de.joonko.loan.data.support.mapper;

import de.joonko.loan.data.support.model.PersonalDetailTopic;
import de.joonko.loan.offer.domain.LoanDemand;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
@RequiredArgsConstructor
public abstract class PersonalDetailsMapper {

    @Mapping(target = "applicationId", expression = "java(loanDemand.getLoanApplicationId())")
    @Mapping(target = "loanAsked", source = "loanDemand.loanAsked")
    @Mapping(target = "duration", expression = "java(loanDemand.getDuration().getValue())")
    @Mapping(target = "personalDetails", source = "loanDemand.personalDetails")
    @Mapping(target = "employmentDetails", source = "loanDemand.employmentDetails")
    @Mapping(target = "contactData", source = "loanDemand.contactData")
    @Mapping(target = "dacId", source = "loanDemand.dacId")
    @Mapping(target = "internalUse", source = "dataInternalUse")
    public abstract PersonalDetailTopic mapToPersonalDetails(LoanDemand loanDemand, boolean dataInternalUse);

}
