package de.joonko.loan.partner.creditPlus.mapper;

import de.joonko.loan.offer.domain.DigitalAccountStatements;
import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.partner.creditPlus.CreditPlusMapperUtils;
import de.joonko.loan.partner.creditPlus.stub.EfinComparerServiceStub;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

@Mapper(componentModel = "spring", uses = {CreditPlusMinimalDataMapper.class})
public interface CreditPlusDebtorDacDataMapper {

    @Mapping(target = "accountOwner", source = "digitalAccountStatements.owner")
    @Mapping(target = "iban", source = "digitalAccountStatements.iban")
    @Mapping(target = "minimalData", source = ".")
    @Mapping(target = "revenues", source = "digitalAccountStatements", qualifiedByName = "toRevenues")
    @Mapping(target = "timestamp", source = "digitalAccountStatements.createdAt", qualifiedByName = "getTimestamp")
    EfinComparerServiceStub.DacData toDacData(LoanDemand loanDemand);

    @Named("getTimestamp")
    default Calendar getTimestamp(LocalDateTime createdAt) {
        return GregorianCalendar.from(createdAt.atZone(ZoneId.systemDefault()));
    }

    @Named("toRevenues")
    default EfinComparerServiceStub.Revenue[] toRevenues(DigitalAccountStatements digitalAccountStatements) {
        return CreditPlusMapperUtils.mapToRevenues(digitalAccountStatements);
    }
}
