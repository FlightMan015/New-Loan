package de.joonko.loan.partner.creditPlus.mapper;

import de.joonko.loan.config.CreditPlusConfig;
import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.partner.creditPlus.CreditPlusDefaults;
import de.joonko.loan.partner.creditPlus.stub.EfinComparerServiceStub;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = {CreditPlusAdditionalDataMapper.class, CreditPlusDebtorMapper.class})
public abstract class CreditPlusCreateCreditOfferRequestMapper {

    @Autowired
    protected CreditPlusConfig creditPlusConfig;

    @Mapping(target = "amount", source = "loanAsked")
    @Mapping(target = "dealerOrderNumber", source = "loanApplicationId")
    @Mapping(target = "duration", source = "duration.value")
    @Mapping(target = "productType", expression = "java(creditPlusConfig.getProductType())")
    @Mapping(target = "ipAddress", source = "requestIp")
    @Mapping(target = "additionalData", source = "digitalAccountStatements")
    @Mapping(target = "debtor1", source = ".")
    public abstract EfinComparerServiceStub.CreditOfferDac toCreditOfferDac(LoanDemand loanDemand);
}
