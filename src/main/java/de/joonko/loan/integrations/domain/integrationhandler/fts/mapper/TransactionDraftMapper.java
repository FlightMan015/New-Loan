package de.joonko.loan.integrations.domain.integrationhandler.fts.mapper;


import de.joonko.loan.offer.domain.TransactionDraft;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.Instant;
import java.time.LocalDate;
import java.util.TimeZone;

@Mapper(componentModel = "spring")
public interface TransactionDraftMapper {

    @Mapping(target = "bookingDate", source = "bookingDate", qualifiedByName = "longToLocalDate")
    @Mapping(target = "purpose", source = "bookingPurpose")
    @Mapping(target = "currency", source = "currency")
    @Mapping(target = "partnerName", source = "partnerName")
    TransactionDraft map(de.joonko.loan.avro.dto.salary_account.Transactions transaction);


    @Named("longToLocalDate")
    default LocalDate longToLocalDate(Long localDate) {
        if (null != localDate)
            return LocalDate.ofInstant(Instant.ofEpochSecond(localDate),
                    TimeZone.getDefault().toZoneId());
        return null;
    }
}
