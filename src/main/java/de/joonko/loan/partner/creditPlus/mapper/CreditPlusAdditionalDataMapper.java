package de.joonko.loan.partner.creditPlus.mapper;

import de.joonko.loan.offer.domain.DigitalAccountStatements;
import de.joonko.loan.partner.creditPlus.CreditPlusDefaults;
import de.joonko.loan.partner.creditPlus.stub.EfinComparerServiceStub;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CreditPlusAdditionalDataAccountMapper.class})
public interface CreditPlusAdditionalDataMapper {

    @Mapping(target = "transferDay", constant = CreditPlusDefaults.TRANSFER_DAY)
    @Mapping(target = "account", source = ".")
    @Mapping(target = "dispatchType", constant = CreditPlusDefaults.DISPATCH_TYPE)
    EfinComparerServiceStub.AddionalData toAdditionalData(DigitalAccountStatements digitalAccountStatements);
}
