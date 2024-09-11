package de.joonko.loan.partner.swk.mapper;

import de.joonko.loan.config.SwkConfig;
import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.partner.swk.stub.CreditApplicationServiceStub;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class SwkClientIdentificationRequestMapper {

    @Autowired
    protected SwkConfig swkConfig;

    @Mapping(target = "clientSessionId", ignore = true)
    @Mapping(target = "requestId", expression = "java(de.joonko.loan.db.vo.ExternalIdentifiers.swkExternalIdentifierFromApplicationId(loanDemand.getLoanApplicationId()))")
    @Mapping(target = "username", expression = "java(swkConfig.getUsername())")
    @Mapping(target = "password", expression = "java(swkConfig.getPassword())")
    @Mapping(target = "partnerId", expression = "java(swkConfig.getPartnerid())")
    @Mapping(target = "requestType", expression = "java(swkConfig.getRequestType())")
    public abstract CreditApplicationServiceStub.ClientIdentification toClientIdentification(LoanDemand loanDemand);


}
