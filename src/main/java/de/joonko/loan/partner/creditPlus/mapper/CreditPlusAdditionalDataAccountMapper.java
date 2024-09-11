package de.joonko.loan.partner.creditPlus.mapper;

import de.joonko.loan.offer.domain.DigitalAccountStatements;
import de.joonko.loan.partner.creditPlus.CreditPlusDefaults;
import de.joonko.loan.partner.creditPlus.stub.EfinComparerServiceStub;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CreditPlusAdditionalDataAccountMapper {

    @Mapping(target = "accountOwner", constant = CreditPlusDefaults.ACCOUNT_OWNER)
    @Mapping(target = "iban", source = "iban")
    EfinComparerServiceStub.Account toAccount(DigitalAccountStatements digitalAccountStatements);
}
