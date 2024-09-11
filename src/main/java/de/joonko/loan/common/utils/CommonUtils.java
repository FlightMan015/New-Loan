package de.joonko.loan.common.utils;

import org.slf4j.MDC;

import java.text.Normalizer;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommonUtils {

    public static void loadLogContext(String applicationId, String dacId) {
        MDC.clear();
        MDC.put("loanApplicationId", "loanApplicationId:" + applicationId);
        MDC.put("dacId", "dacId:" + dacId);
    }


    public static String normalizeString(String value) {
        if (null != value) {
            String result = value;
            result = result.replaceAll("\\.", "")
                    .replaceAll("\\,", "");
            result = Normalizer
                    .normalize(result, Normalizer.Form.NFD)
                    .replaceAll("[^\\p{ASCII}]", "");
            return result.strip();
        }
        return null;
    }

    public static String generateUUID(String prefix) {
        return String.format("%s %s", prefix, UUID.randomUUID().toString().toUpperCase().replace("-", ""));
    }
}
