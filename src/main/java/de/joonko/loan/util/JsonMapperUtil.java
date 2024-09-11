package de.joonko.loan.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
@UtilityClass
public class JsonMapperUtil {

    public static String getJsonString(Object object) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(object);
    }

    public static String getBase64Encoded(Object object) throws JsonProcessingException {
        return Base64.getEncoder().encodeToString(getJsonString(object).getBytes(StandardCharsets.UTF_8));
    }
}
