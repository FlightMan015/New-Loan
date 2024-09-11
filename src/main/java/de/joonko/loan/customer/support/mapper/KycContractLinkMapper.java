package de.joonko.loan.customer.support.mapper;

import de.joonko.loan.customer.support.model.KycStatusEvent;
import de.joonko.loan.db.service.LoanDemandStoreService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class KycContractLinkMapper {

    @Autowired
    LoanDemandStoreService loanDemandStoreService;

    @Mapping(target = "email", source = "emailId")
    @Mapping(target = "loanContract", source = "link")
    @Mapping(target = "createdAt", expression = "java(System.currentTimeMillis())")
    public abstract KycStatusEvent mapToContractLinkEvent(String emailId, String link);
}
