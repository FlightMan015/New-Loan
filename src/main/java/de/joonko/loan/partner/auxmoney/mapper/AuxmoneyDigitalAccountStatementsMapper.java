package de.joonko.loan.partner.auxmoney.mapper;

import de.joonko.loan.partner.auxmoney.model.DigitalAccountStatements;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = AuxmoneyTransactionMapper.class)
interface AuxmoneyDigitalAccountStatementsMapper {

    @Mapping(source = "bankAccountName", target = "name")
    @Mapping(source = "owner", target = "owner")
    @Mapping(source = "dacSource", target = "dacSource")
    @Mapping(source = "category", target = "category")
    @Mapping(source = "iban", target = "iban")
    @Mapping(source = "bic", target = "bic")
    @Mapping(source = "bankAccountType", target = "type")
    @Mapping(source = "balance", target = "balance")
    @Mapping(source = "balanceDate", target = "balanceDate")
    @Mapping(source = "currency", target = "currency")
    @Mapping(source = "transactions", target = "transactions")
    DigitalAccountStatements toAuxmoneyDigitalAccountStatements(de.joonko.loan.offer.domain.DigitalAccountStatements digitalAccountStatements);
}
