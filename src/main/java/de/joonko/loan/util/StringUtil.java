package de.joonko.loan.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;

@UtilityClass
public class StringUtil {

    public String maskIban(String iban, String maskChar) {
        return (iban != null && iban.length() > 6) ? iban.substring(0, 2) + StringUtils.repeat(maskChar, iban.length() - 6) + iban.substring(iban.length() - 4) : null;
    }

    public String maskLastNCharacters(String val, String maskChar, int n) {
        return (val != null && val.length() > n) ? val.substring(0, val.length() - n) + StringUtils.repeat(maskChar, n) : null;
    }

    public String limitAndTrimLastWord(@Nullable String str, int limit) {
        if (str == null) {
            return null;
        }

        str = str.trim();

        if (str.length() <= limit) {
            return str;
        }

        str = str.substring(0, limit);

        int index = str.lastIndexOf(" ");

        if (index == -1) {
            return str;
        }

        return str.substring(0, index).trim();
    }
}
