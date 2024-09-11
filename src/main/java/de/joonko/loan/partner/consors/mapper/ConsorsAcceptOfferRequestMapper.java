package de.joonko.loan.partner.consors.mapper;

import de.joonko.loan.acceptoffer.domain.OfferRequest;
import de.joonko.loan.partner.consors.model.ConsorsAcceptOfferRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ConsorsAcceptOfferRequestMapper {

    @Mapping(source = "loanAsked", target = "financialCondition.creditAmount")
    @Mapping(expression = "java(offerRequest.getDuration().getValue())", target = "financialCondition.duration")
    @Mapping(constant = "1", target = "paymentDay")
    ConsorsAcceptOfferRequest toConsorsRequest(OfferRequest offerRequest);
}
