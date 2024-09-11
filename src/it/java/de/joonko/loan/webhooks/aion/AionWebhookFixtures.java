package de.joonko.loan.webhooks.aion;

public class AionWebhookFixtures {

    public String getSuccessWebhookRequest() {
        return "{" +
                "\"id\":\"f7c90247-558c-4f32-be9c-ad84363b4df5\"," +
                "\"sourceSystem\":\"BPM\"," +
                "\"eventDateTime\":\"2011-12-03T10:15:30+01:00\"," +
                "\"type\":\"credits.cashloan.loanopen\"," +
                "\"payload\":" +
                "  {" +
                "   \"processInstanceId\" : \"cdb18fd5-529c-4dd7-a701-e31b434ec113\"," +
                "   \"iban\":\"DExxxx\"," +
                "   \"offerId\":\"f5684d24-3682-44c0-8e2b-190b2a30c491\"," +
                "   \"customerId\" : \"111222\"," +
                "   \"status\":\"SUCCESS\",   " +
                "   \"customerInfo\": \"Your loan was disbursed and money transferred to your account, you should receive email form Aion Bank with loan agreement\"  " +
                "  }" +
                "}";
    }

    public String getFailureWebhookRequest() {
        return "{" +
                "\"id\":\"f7c90247-558c-4f32-be9c-ad84363b4df5\"," +
                "\"sourceSystem\":\"BPM\"," +
                "\"eventDateTime\":\"2011-12-03T10:15:30+01:00\"," +
                "\"type\":\"credits.cashloan.loanopen\"," +
                "\"payload\":" +
                "  {" +
                "   \"processInstanceId\" : \"cdb18fd5-529c-4dd7-a701-e31b434ec114\"," +
                "   \"iban\":\"DExxxx\"," +
                "   \"offerId\":\"f5684d24-3682-44c0-8e2b-190b2a30c491\"," +
                "   \"customerId\" : \"111222\"," +
                "   \"status\":\"FAILED\",   " +
                "   \"customerInfo\": \"Unfortunately we can not grant you the loan\"  " +
                "  }" +
                "}";
    }

    public String getInvalidTypeWebhookRequest() {
        return "{" +
                "\"id\":\"f7c90247-558c-4f32-be9c-ad84363b4df5\"," +
                "\"sourceSystem\":\"BPM\"," +
                "\"eventDateTime\":\"2011-12-03T10:15:30+01:00\"," +
                "\"type\":\"invalid-type\"," +
                "\"payload\":" +
                "  {" +
                "   \"processInstanceId\" : \"cdb18fd5-529c-4dd7-a701-e31b434ec113\"," +
                "   \"iban\":\"DExxxx\"," +
                "   \"offerId\":\"f5684d24-3682-44c0-8e2b-190b2a30c491\"," +
                "   \"customerId\" : \"111222\"," +
                "   \"status\":\"FAILED\",   " +
                "   \"customerInfo\": \"Unfortunately we can not grant you the loan\"  " +
                "  }" +
                "}";
    }
}
