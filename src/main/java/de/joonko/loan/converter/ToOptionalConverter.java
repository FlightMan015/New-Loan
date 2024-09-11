package de.joonko.loan.converter;


import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@WritingConverter
public class ToOptionalConverter<T> implements Converter<Optional<T>, Object> {

    @Override
    public Object convert(Optional<T> source) {
        return source.orElse(null);
    }
}
