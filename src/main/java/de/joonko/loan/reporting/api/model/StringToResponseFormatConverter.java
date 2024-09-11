package de.joonko.loan.reporting.api.model;

import org.springframework.core.convert.converter.Converter;

import java.util.Locale;

public class StringToResponseFormatConverter implements Converter<String, ResponseFormat> {

    @Override
    public ResponseFormat convert(String s) {
        return ResponseFormat.valueOf(s.toUpperCase(Locale.ROOT));
    }
}