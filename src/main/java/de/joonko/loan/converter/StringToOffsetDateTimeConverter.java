package de.joonko.loan.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

@Component
@ReadingConverter
public class StringToOffsetDateTimeConverter implements Converter<String, OffsetDateTime> {

    @Override
    public OffsetDateTime convert(@Nullable String document) {
        if (document == null) return null;
        return OffsetDateTime.parse(document, DateTimeFormatter.ISO_OFFSET_DATE_TIME);

    }
}
