package de.joonko.loan.partner.consors.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Named;

import joptsimple.internal.Strings;

@Mapper(componentModel = "spring")
public class StringMapper {

    @Named("trimWhiteSpaces")
    static String trimWhiteSpaces(String withTrailingSpaces) {
        if(!Strings.isNullOrEmpty(withTrailingSpaces)){
            return withTrailingSpaces.strip();
        }
        return withTrailingSpaces;
    }
}

