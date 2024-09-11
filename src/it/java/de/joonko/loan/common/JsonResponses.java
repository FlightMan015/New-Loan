package de.joonko.loan.common;

public class JsonResponses {

    public static String json(String string) {
        return string.replaceAll("'", "\"");
    }

}
