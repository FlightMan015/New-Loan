package de.joonko.loan.identification.service.idnow.testdata;

public class IdNowResponses {

    public static String get200LoginResponse() {
        return "{\n" +
                "    \"authToken\": \"token\"\n" +
                "}";
    }

    public static String get200IdentResponse() {
        return "{\n" +
                "    \"identificationprocess\": {\n" +
                "        \"result\": \"FRAUD_SUSPICION_PENDING\",\n" +
                "        \"companyid\": \"ihrebank\",\n" +
                "        \"filename\": \"89035879032.zip\",\n" +
                "        \"agentname\": \"HKULKARNI\",\n" +
                "        \"identificationtime\": \"2019-06-21T23:42:39+02:00\",\n" +
                "        \"id\": \"DEV-SAVJC\",\n" +
                "        \"href\": \"/api/v1/ihrebank/identifications/89035879032.zip\",\n" +
                "        \"type\": \"APP\",\n" +
                "        \"transactionnumber\": \"89035879032\"\n" +
                "    },\n" +
                "    \"customdata\": {\n" +
                "        \"custom3\": null,\n" +
                "        \"custom4\": null,\n" +
                "        \"custom1\": null,\n" +
                "        \"custom2\": null,\n" +
                "        \"custom5\": null\n" +
                "    },\n" +
                "    \"contactdata\": {\n" +
                "        \"mobilephone\": \"+4915257459721\",\n" +
                "        \"email\": \"john.klhkl@test.com\"\n" +
                "    },\n" +
                "    \"userdata\": {\n" +
                "        \"birthday\": {\n" +
                "            \"status\": \"CHANGE\",\n" +
                "            \"value\": \"1964-08-12\",\n" +
                "            \"original\": \"1985-01-01\"\n" +
                "        },\n" +
                "        \"firstname\": {\n" +
                "            \"status\": \"MATCH\",\n" +
                "            \"value\": \"JOHN\"\n" +
                "        },\n" +
                "        \"address\": {\n" +
                "            \"zipcode\": {\n" +
                "                \"status\": \"MATCH\",\n" +
                "                \"value\": \"82444\"\n" +
                "            },\n" +
                "            \"country\": {\n" +
                "                \"status\": \"MATCH\",\n" +
                "                \"value\": \"DE\"\n" +
                "            },\n" +
                "            \"city\": {\n" +
                "                \"status\": \"MATCH\",\n" +
                "                \"value\": \"MUNICH\"\n" +
                "            },\n" +
                "            \"street\": {\n" +
                "                \"status\": \"MATCH\",\n" +
                "                \"value\": \"ISARSTRASSE\"\n" +
                "            },\n" +
                "            \"streetnumber\": {\n" +
                "                \"status\": \"MATCH\",\n" +
                "                \"value\": \"22\"\n" +
                "            }\n" +
                "        },\n" +
                "        \"birthplace\": {\n" +
                "            \"status\": \"MATCH\",\n" +
                "            \"value\": \"MUNICH\"\n" +
                "        },\n" +
                "        \"nationality\": {\n" +
                "            \"status\": \"MATCH\",\n" +
                "            \"value\": \"DE\"\n" +
                "        },\n" +
                "        \"gender\": {\n" +
                "            \"status\": \"MATCH\",\n" +
                "            \"value\": \"MALE\"\n" +
                "        },\n" +
                "        \"identlanguage\": {\n" +
                "            \"status\": \"MATCH\",\n" +
                "            \"value\": \"en\"\n" +
                "        },\n" +
                "        \"title\": {\n" +
                "            \"status\": \"MATCH\",\n" +
                "            \"value\": \"DR.\"\n" +
                "        },\n" +
                "        \"lastname\": {\n" +
                "            \"status\": \"MATCH\",\n" +
                "            \"value\": \"CARTER\"\n" +
                "        }\n" +
                "    },\n" +
                "    \"identificationdocument\": {\n" +
                "        \"country\": {\n" +
                "            \"status\": \"NEW\",\n" +
                "            \"value\": \"DE\"\n" +
                "        },\n" +
                "        \"number\": {\n" +
                "            \"status\": \"NEW\",\n" +
                "            \"value\": \"T01008921\"\n" +
                "        },\n" +
                "        \"issuedby\": {\n" +
                "            \"status\": \"NEW\",\n" +
                "            \"value\": \"DEU-BO-02001\"\n" +
                "        },\n" +
                "        \"dateissued\": {\n" +
                "            \"status\": \"NEW\",\n" +
                "            \"value\": \"2010-11-01\"\n" +
                "        },\n" +
                "        \"type\": {\n" +
                "            \"status\": \"NEW\",\n" +
                "            \"value\": \"IDCARD\"\n" +
                "        },\n" +
                "        \"validuntil\": {\n" +
                "            \"status\": \"NEW\",\n" +
                "            \"value\": \"2020-10-31\"\n" +
                "        }\n" +
                "    },\n" +
                "    \"attachments\": {\n" +
                "        \"pdf\": \"89035879032.pdf\",\n" +
                "        \"xml\": \"89035879032.xml\",\n" +
                "        \"videolog\": \"89035879032.mp4\",\n" +
                "        \"idbackside\": \"89035879032_idbackside.jpg\",\n" +
                "        \"idfrontside\": \"89035879032_idfrontside.jpg\",\n" +
                "        \"security1\": \"89035879032_security1.jpg\",\n" +
                "        \"userface\": \"89035879032_userface.jpg\",\n" +
                "        \"security2\": \"89035879032_security2.jpg\",\n" +
                "        \"security_covered\": \"89035879032_security_covered.jpg\",\n" +
                "        \"security3\": \"89035879032_security3.jpg\"\n" +
                "    }\n" +
                "}";
    }

    public static String get401Response() {
        return "{\n" +
                "    \"errors\": [\n" +
                "        {\n" +
                "            \"cause\": \"INVALID_LOGIN_TOKEN\",\n" +
                "            \"errorType\": null,\n" +
                "            \"id\": \"91118930\",\n" +
                "            \"key\": null,\n" +
                "            \"message\": null,\n" +
                "            \"translationKey\": null\n" +
                "        }\n" +
                "    ]\n" +
                "}";
    }

    public static String get404IdentResponse() {
        return "{\n" +
                "    \"errors\": [\n" +
                "        {\n" +
                "            \"cause\": \"OBJECT_NOT_FOUND\",\n" +
                "            \"errorType\": null,\n" +
                "            \"id\": \"92346250\",\n" +
                "            \"key\": \"Ident DEV-QCQSL - No identification request found matching the provided parameters\",\n" +
                "            \"message\": null,\n" +
                "            \"translationKey\": null\n" +
                "        }\n" +
                "    ]\n" +
                "}";
    }

    public static String get201CreatingIdentResponse() {
        return "{\n" +
                "    \"id\": \"TST-FXWF\"\n" +
                "}";
    }

    public static String get200ListOfDocumentsResponse() {
        return "[\n" +
                "    {\n" +
                "        \"optional\": false,\n" +
                "        \"name\": \"Agreement\",\n" +
                "        \"identifier\": \"agreement\",\n" +
                "        \"mimeType\": \"application/pdf\",\n" +
                "        \"sortOrder\": 1,\n" +
                "        \"viewPolicy\": \"DEFAULT\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"optional\": false,\n" +
                "        \"name\": \"Schedule\",\n" +
                "        \"identifier\": \"schedule\",\n" +
                "        \"mimeType\": \"application/pdf\",\n" +
                "        \"sortOrder\": 2,\n" +
                "        \"viewPolicy\": \"DEFAULT\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"optional\": false,\n" +
                "        \"name\": \"Secci\",\n" +
                "        \"identifier\": \"secci\",\n" +
                "        \"mimeType\": \"application/pdf\",\n" +
                "        \"sortOrder\": 3,\n" +
                "        \"viewPolicy\": \"DEFAULT\"\n" +
                "    }\n" +
                "]";
    }
}
