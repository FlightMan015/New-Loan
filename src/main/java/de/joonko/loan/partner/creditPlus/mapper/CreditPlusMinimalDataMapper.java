package de.joonko.loan.partner.creditPlus.mapper;

import de.joonko.loan.offer.domain.DigitalAccountStatements;
import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.partner.creditPlus.CreditPlusDefaults;
import de.joonko.loan.partner.creditPlus.CreditPlusMapperUtils;
import de.joonko.loan.partner.creditPlus.stub.EfinComparerServiceStub;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;
import java.util.TimeZone;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mapper(componentModel = "spring")
public interface CreditPlusMinimalDataMapper {

    @Mapping(target = "accountOwner", source = "digitalAccountStatements.owner")
    @Mapping(target = "iban", source = "digitalAccountStatements.iban")
    @Mapping(target = "childBenefit", source = "digitalAccountStatements", qualifiedByName = "toChildBenefit")
    @Mapping(target = "employerName", source = "employmentDetails.employerName")
    @Mapping(target = "income", source = "digitalAccountStatements", qualifiedByName = "toIncome")
    @Mapping(target = "numberOfChildren", source = "personalDetails.numberOfChildren")
    @Mapping(target = "pension", source = "digitalAccountStatements", qualifiedByName = "toPension")
    @Mapping(target = "rent", source = "digitalAccountStatements", qualifiedByName = "toRent")
    @Mapping(target = "requestId", source = ".", qualifiedByName = "toRequestId")
    @Mapping(target = "score", constant = CreditPlusDefaults.SCORE)
    @Mapping(target = "vehicleExists", source = "digitalAccountStatements", qualifiedByName = "toVehicle")
    EfinComparerServiceStub.Minimal toMinimalData(LoanDemand loanDemand);

    @Named("toRent")
    default BigDecimal mapRent(DigitalAccountStatements digitalAccountStatements) {
        return CreditPlusMapperUtils.getCalculatedAmount(digitalAccountStatements, CreditPlusDefaults.CATEGORY_ID_RENT);
    }

    @Named("toIncome")
    default BigDecimal mapIncome(DigitalAccountStatements digitalAccountStatements) {
        return CreditPlusMapperUtils.getCalculatedAmount(digitalAccountStatements, Stream.concat(CreditPlusDefaults.CATEGORY_INCOME.stream(), CreditPlusDefaults.CATEGORY_OTHER_INCOME.stream())
                .collect(Collectors.toList()));
    }

    @Named("toChildBenefit")
    default BigDecimal toChildBenefit(DigitalAccountStatements digitalAccountStatements) {
        return CreditPlusMapperUtils.calculateChildBenefit(digitalAccountStatements);
    }

    @Named("toPension")
    default BigDecimal toPension(DigitalAccountStatements digitalAccountStatements) {
        return CreditPlusMapperUtils.getCalculatedAmount(digitalAccountStatements, CreditPlusDefaults.CATEGORY_PENSION);
    }

    @Named("toVehicle")
    default Boolean toVehicle(DigitalAccountStatements digitalAccountStatements) {
        BigDecimal vehicleValue = CreditPlusMapperUtils.getCalculatedAmount(digitalAccountStatements, CreditPlusDefaults.CATEGORY_VEHICLE);
        if (vehicleValue.compareTo(BigDecimal.valueOf(50)) > 0) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    @Named("toRequestId")
    default Integer toRequestId(LoanDemand loanDemand) {
        return Integer.valueOf(String.format("%040d", new BigInteger(UUID.randomUUID().toString().replace("-", ""), 16))
                .substring(0, 7)
                .concat(String.valueOf(loanDemand.getDuration().value)));
    }
}
