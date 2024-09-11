package de.joonko.loan.partner.aion.testdata;

public class AionResponses {
    public static String get200OfferChoice() {
        return "{\n" +
                "   \"processId\": \"db207bb1-9a57-4eb6-9f9b-08e04e52d924\",\n" +
                "   \"variables\": [\n" +
                "        {\n" +
                "            \"name\": \"REPRESENTATIVE_ID\",\n" +
                "            \"value\": \"9b3cfec4-b9cd-4bb7-8c71-66645c985bca\"\n" +
                "        },\n" +
                "       {\n" +
                "           \"name\": \"DRAFT_AGREEMENT\",\n" +
                "           \"value\": {\n" +
                "               \"agreement\": {\n" +
                "                   \"fileContent\": \"agreementbase64\",\n" +
                "                   \"fileName\": \"DRAFT_LOAN_AGREEMENT.pdf\"\n" +
                "               },\n" +
                "               \"schedule\": {\n" +
                "                   \"fileContent\": \"schedulebase64\",\n" +
                "                   \"fileName\": \"DRAFT_SCHEDULE_FOR_LOAN.pdf\"\n" +
                "               },\n" +
                "               \"secci\": {\n" +
                "                   \"fileContent\": \"seccibase64\",\n" +
                "                   \"fileName\": \"SECCI_FORM.pdf\"\n" +
                "               }\n" +
                "           }\n" +
                "       }\n" +
                "   ]\n" +
                "}";
    }

    public static String get400OfferChoice() {
        return "{\"errorUUID\":\"8413adc3-d995-4ced-bd80-3d7e39a08f4e\",\"message\":\"400 BAD_REQUEST \\\"Problem with calling the process\\\"\"}";
    }

    public static String get200Auth() {
        return "{\n" +
                "   \"access_token\": \"token\"\n" +
                "}";
    }
}
