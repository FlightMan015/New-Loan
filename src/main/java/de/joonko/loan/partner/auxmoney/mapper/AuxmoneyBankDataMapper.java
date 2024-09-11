package de.joonko.loan.partner.auxmoney.mapper;

import de.joonko.loan.offer.domain.DigitalAccountStatements;
import de.joonko.loan.partner.auxmoney.model.BankData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuxmoneyBankDataMapper {


    @Mapping(source = "bic", target = "bic")
    @Mapping(source = "iban", target = "iban")
    BankData toAuxmoneyBankData(DigitalAccountStatements digitalAccountStatements);
}
