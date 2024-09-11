package de.joonko.loan.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@ReadingConverter
public class FromOptionalConverter<T> implements Converter<Object, Optional<T>> {

    @Override
    public Optional<T> convert(Object source) {
        return Optional.ofNullable((T) source);
    }
}
