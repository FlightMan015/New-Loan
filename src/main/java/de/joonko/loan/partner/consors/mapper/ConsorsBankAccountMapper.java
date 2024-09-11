package de.joonko.loan.partner.consors.mapper;

import de.joonko.loan.offer.domain.DigitalAccountStatements;
import de.joonko.loan.partner.consors.ConsorsDefaults;
import de.joonko.loan.partner.consors.model.BankAccount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ConsorsBankAccountMapper {


    @Mapping(target = "accountNumber", ignore = true)
    @Mapping(target = "blz", ignore = true)
    @Mapping(target = "accountSince", source = "accountSince", dateFormat = "YYYY-MM")
    @Mapping(source = "iban", target = "iban")
    @Mapping(source = "bic", target = "bic")
    @Mapping(target = "owner", constant = ConsorsDefaults.OWNER)
    public BankAccount fromJoonko(DigitalAccountStatements digitalAccountStatements);
}
