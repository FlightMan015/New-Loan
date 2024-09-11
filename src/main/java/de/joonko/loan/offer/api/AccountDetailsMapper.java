package de.joonko.loan.offer.api;

import de.joonko.loan.avro.dto.dac.DigitalAccountStatement;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Mapper(componentModel = "spring")
public interface AccountDetailsMapper {

    @Mapping(target = "nameOnAccount" , source = "bankAccountName")
    @Mapping(target = "balanceDate", source = "createdAt", qualifiedByName = "createdAt")
    AccountDetails toAccountDetails(DigitalAccountStatement account);


    @Named("createdAt")
    default LocalDate createdAt(String createdAt) {
        return Optional.ofNullable(createdAt).map(LocalDateTime::parse).map(LocalDateTime::toLocalDate).orElse(null);
    }



}
