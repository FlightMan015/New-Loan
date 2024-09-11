package de.joonko.loan.identification;

public class IDNowResponses {
    public final static String CREATE_IDENT_RESPONSE = "{\n" +
            "         \"id\": \"TST-ZSHVT\"\n" +
            "        }";

    public final static String GET_TOKEN_RESPONSE = "{\n" +
            "  \"authToken\": \"eyJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJkZS5pZG5vdy5nYXRld2F5IiwiYXVkIjoiZGUuaWRub3cuYXBwbGljYXRpb24iLCJleHAiOjE1ODkyNzQ0ODEsImp0aSI6ImhYNG90WU9BUXE4cTIzWHpLZ2QzaXciLCJpYXQiOjE1ODkyNzA4ODEsIm5iZiI6MTU4OTI3MDc2MSwic3ViIjoiam9vbmtvc3drYXV4bW9uZXllc2lnbiIsInR5cGUiOiJDT01QQU5ZIiwicGVybWlzc2lvbnMiOiJBTEwifQ.Zx59mpZ5y3J1AG_dlTAdsieSomfoyNOQ0oZm3fsO70y7N-OSgv4pggxdBzXS6jf0i6v6a5EDQogrfCyko2ogPg\"\n" +
            "}";
    public final static String GET_IDENTIFICATION_STATUS_RESPONSE = "{\n" +
            "  \"attachments\": {\n" +
            "    \"pdf\": \"b0222bf5-b03c-484f-8b1a-751bcb4c8727.pdf\",\n" +
            "    \"xml\": \"b0222bf5-b03c-484f-8b1a-751bcb4c8727.xml\",\n" +
            "    \"videolog\": \"b0222bf5-b03c-484f-8b1a-751bcb4c8727.mp4\",\n" +
            "    \"idbackside\": \"b0222bf5-b03c-484f-8b1a-751bcb4c8727_idbackside.jpg\",\n" +
            "    \"idfrontside\": \"b0222bf5-b03c-484f-8b1a-751bcb4c8727_idfrontside.jpg\",\n" +
            "    \"security1\": \"b0222bf5-b03c-484f-8b1a-751bcb4c8727_security1.jpg\",\n" +
            "    \"userface\": \"b0222bf5-b03c-484f-8b1a-751bcb4c8727_userface.jpg\",\n" +
            "    \"security2\": \"b0222bf5-b03c-484f-8b1a-751bcb4c8727_security2.jpg\",\n" +
            "    \"security3\": \"b0222bf5-b03c-484f-8b1a-751bcb4c8727_security3.jpg\",\n" +
            "    \"security_covered\": \"b0222bf5-b03c-484f-8b1a-751bcb4c8727_security_covered.jpg\"\n" +
            "  },\n" +
            "  \"identificationprocess\": {\n" +
            "    \"result\": \"SUCCESS_DATA_CHANGED\",\n" +
            "    \"id\": \"TST-DTMWN\",\n" +
            "    \"href\": \"/api/v1/joonkoswkauxmoneyesign/identifications/b0222bf5-b03c-484f-8b1a-751bcb4c8727.zip\",\n" +
            "    \"type\": \"WEB\",\n" +
            "    \"companyid\": \"joonkoswkauxmoneyesign\",\n" +
            "    \"filename\": \"b0222bf5-b03c-484f-8b1a-751bcb4c8727.zip\",\n" +
            "    \"agentname\": \"TROBOT\",\n" +
            "    \"identificationtime\": 1591891775,\n" +
            "    \"transactionnumber\": \"b0222bf5-b03c-484f-8b1a-751bcb4c8727\"\n" +
            "  },\n" +
            "  \"customdata\": {\n" +
            "    \"custom1\": \"X-MANUALTEST-LONGREVIEW\",\n" +
            "    \"custom2\": null,\n" +
            "    \"custom3\": null,\n" +
            "    \"custom4\": null,\n" +
            "    \"custom5\": null\n" +
            "  },\n" +
            "  \"contactdata\": {\n" +
            "    \"mobilephone\": \"+441789012345\",\n" +
            "    \"email\": \"hello@fdgdgdg.org\"\n" +
            "  },\n" +
            "  \"userdata\": {\n" +
            "    \"birthday\": {\n" +
            "      \"status\": \"CHANGE\",\n" +
            "      \"value\": \"2002-02-02\",\n" +
            "      \"original\": \"1987-05-23\"\n" +
            "    },\n" +
            "    \"address\": {\n" +
            "      \"zipcode\": {\n" +
            "        \"status\": \"MATCH\",\n" +
            "        \"value\": \"W1U\"\n" +
            "      },\n" +
            "      \"country\": {\n" +
            "        \"status\": \"MATCH\",\n" +
            "        \"value\": \"GB\"\n" +
            "      },\n" +
            "      \"city\": {\n" +
            "        \"status\": \"MATCH\",\n" +
            "        \"value\": \"LONDON\"\n" +
            "      },\n" +
            "      \"street\": {\n" +
            "        \"status\": \"MATCH\",\n" +
            "        \"value\": \"BAKER STREET\"\n" +
            "      },\n" +
            "      \"streetnumber\": {\n" +
            "        \"status\": \"NEW\",\n" +
            "        \"value\": \"1\"\n" +
            "      }\n" +
            "    },\n" +
            "    \"firstname\": {\n" +
            "      \"status\": \"MATCH\",\n" +
            "      \"value\": \"X-MANUALTEST-HOLDCERTIFICATE\"\n" +
            "    },\n" +
            "    \"birthplace\": {\n" +
            "      \"status\": \"MATCH\",\n" +
            "      \"value\": \"LONDON\"\n" +
            "    },\n" +
            "    \"nationality\": {\n" +
            "      \"status\": \"MATCH\",\n" +
            "      \"value\": \"GB\"\n" +
            "    },\n" +
            "    \"gender\": {\n" +
            "      \"status\": \"MATCH\",\n" +
            "      \"value\": \"FEMALE\"\n" +
            "    },\n" +
            "    \"identlanguage\": {\n" +
            "      \"status\": \"MATCH\",\n" +
            "      \"value\": \"en\"\n" +
            "    },\n" +
            "    \"title\": {\n" +
            "      \"status\": \"NEW\",\n" +
            "      \"value\": \"TITLE\"\n" +
            "    },\n" +
            "    \"lastname\": {\n" +
            "      \"status\": \"MATCH\",\n" +
            "      \"value\": \"JONES\"\n" +
            "    }\n" +
            "  },\n" +
            "  \"identificationdocument\": {\n" +
            "    \"country\": {\n" +
            "      \"status\": \"NEW\",\n" +
            "      \"value\": \"DE\"\n" +
            "    },\n" +
            "    \"number\": {\n" +
            "      \"status\": \"NEW\",\n" +
            "      \"value\": \"T01008921\"\n" +
            "    },\n" +
            "    \"issuedby\": {\n" +
            "      \"status\": \"NEW\",\n" +
            "      \"value\": \"ISSUER\"\n" +
            "    },\n" +
            "    \"dateissued\": {\n" +
            "      \"status\": \"NEW\",\n" +
            "      \"value\": \"2010-11-01\"\n" +
            "    },\n" +
            "    \"type\": {\n" +
            "      \"status\": \"NEW\",\n" +
            "      \"value\": \"IDCARD\"\n" +
            "    },\n" +
            "    \"validuntil\": {\n" +
            "      \"status\": \"NEW\",\n" +
            "      \"value\": \"2020-10-31\"\n" +
            "    }\n" +
            "  }\n" +
            "}";
}
