package de.joonko.loan.integrations.domain.integrationhandler.fts.mapper;


import de.joonko.loan.acceptoffer.domain.Balance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Mapper(componentModel = "spring")
public interface BalanceDomainMapper {

    @Mapping(target = "date", source = "date" , qualifiedByName = "stringToDate")
    Balance map(de.joonko.loan.avro.dto.salary_account.Balance balance);

    @Named("stringToDate")
    default LocalDate stringToDate(String date) {
        return Optional.ofNullable(date)
                .map(d -> d.replace(" ", "T"))
                .map(LocalDateTime::parse)
                .map(LocalDateTime::toLocalDate)
                .orElse(null);
    }



}
