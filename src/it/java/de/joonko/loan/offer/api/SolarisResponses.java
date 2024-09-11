package de.joonko.loan.offer.api;

import de.joonko.loan.common.JsonResponses;

public class SolarisResponses extends JsonResponses {

    public static final String ACCESS_TOKEN_RESPONSE = json("{\n" +
            "    \"access_token\": \"accessToken\",\n" +
            "    \"token_type\": \"Bearer\",\n" +
            "    \"expires_in\": 604799\n" +
            "}");

    static final String CREATE_PERSON_RESPONSE = json("{\n" +
            "    \"id\": \"fakePersonId\",\n" +
            "    \"salutation\": \"MS\",\n" +
            "    \"title\": null,\n" +
            "    \"first_name\": \"X-MANUALTEST-HAPPYPATH\",\n" +
            "    \"last_name\": \"Abele\",\n" +
            "    \"address\": {\n" +
            "        \"line_1\": \"UNTERE SCHULSTR. 4\",\n" +
            "        \"line_2\": \"Etage 2\",\n" +
            "        \"postal_code\": \"92283\",\n" +
            "        \"city\": \"Lauterhofen\",\n" +
            "        \"country\": \"DE\",\n" +
            "        \"state\": \"BY\"\n" +
            "    },\n" +
            "    \"contact_address\": {\n" +
            "        \"line_1\": \"UNTERE SCHULSTR. 6\",\n" +
            "        \"line_2\": \"Etage 4\",\n" +
            "        \"postal_code\": \"92283\",\n" +
            "        \"city\": \"Lauterhofen\",\n" +
            "        \"country\": \"DE\",\n" +
            "        \"state\": \"BY\"\n" +
            "    },\n" +
            "    \"email\": \"person@example.com\",\n" +
            "    \"mobile_number\": \"+49301234567\",\n" +
            "    \"birth_name\": \"Doe\",\n" +
            "    \"birth_date\": \"1989-06-14\",\n" +
            "    \"birth_city\": \"Oldenburg\",\n" +
            "    \"birth_country\": \"DE\",\n" +
            "    \"nationality\": \"DE\",\n" +
            "    \"employment_status\": \"EMPLOYED\",\n" +
            "    \"job_title\": \"Head of everything\",\n" +
            "    \"tax_information\": {\n" +
            "        \"tax_assessment\": null,\n" +
            "        \"marital_status\": \"UNKNOWN\"\n" +
            "    },\n" +
            "    \"fatca_relevant\": true,\n" +
            "    \"fatca_crs_confirmed_at\": \"2017-01-01T00:00:00.000Z\",\n" +
            "    \"business_purpose\": \"helping people to find themselves\",\n" +
            "    \"industry\": \"DEBIT_BALANCE_SALARY_ACCOUNT\",\n" +
            "    \"industry_key\": \"ECONOMICALLY_DEPENDENT_INDIVIDUALS\",\n" +
            "    \"terms_conditions_signed_at\": \"2017-01-01T00:00:00.000Z\",\n" +
            "    \"own_economic_interest_signed_at\": null,\n" +
            "    \"flagged_by_compliance\": false,\n" +
            "    \"expected_monthly_revenue_cents\": null,\n" +
            "    \"vat_number\": null,\n" +
            "    \"website_social_media\": null,\n" +
            "    \"business_trading_name\": null,\n" +
            "    \"nace_code\": null,\n" +
            "    \"business_address_line_1\": null,\n" +
            "    \"business_address_line_2\": null,\n" +
            "    \"business_postal_code\": null,\n" +
            "    \"business_city\": null,\n" +
            "    \"business_country\": null,\n" +
            "    \"screening_progress\": \"NOT_SCREENED\"\n" +
            "}");

    static final String CREATE_CREDIT_RECORD_RESPONSE = json("{\n" +
            "    \"status\": \"available\",\n" +
            "    \"person_id\": \"8e8aee7d8f3dc19e2e4f877baed0d3dccper\",\n" +
            "    \"id\": \"0de07abd686b43348edbd2421e8ab68accrd\",\n" +
            "    \"created_at\": \"2020-05-06T07:22:54Z\"\n" +
            "}");

    static final String GET_OFFER_RESPONSE = json("{\n" +
            "    \"offer\": {\n" +
            "        \"monthly_installment\": {\n" +
            "            \"value\": 2999,\n" +
            "            \"unit\": \"cents\",\n" +
            "            \"currency\": \"EUR\"\n" +
            "        },\n" +
            "        \"loan_term\": 48,\n" +
            "        \"loan_amount\": {\n" +
            "            \"value\": 150000,\n" +
            "            \"unit\": \"cents\",\n" +
            "            \"currency\": \"EUR\"\n" +
            "        },\n" +
            "        \"interest_rate\": 0.0479,\n" +
            "        \"id\": \"47534b7f1fd444f787e27fbf94a18c0fcofr\",\n" +
            "        \"effective_interest_rate\": 0.0489,\n" +
            "        \"approximate_total_loan_expenses\": {\n" +
            "            \"value\": 107904,\n" +
            "            \"unit\": \"cents\",\n" +
            "            \"currency\": \"EUR\"\n" +
            "        }\n" +
            "    },\n" +
            "    \"loan_decision\": \"OFFERED\",\n" +
            "    \"id\": \"2d14c935f4bf401ca3ac8b655b45c2aaclap\",\n" +
            "    \"disposable_income\": {\n" +
            "        \"value\": 792688,\n" +
            "        \"unit\": \"cents\",\n" +
            "        \"currency\": \"EUR\"\n" +
            "    },\n" +
            "    \"customer_category\": \"GREEN\"\n" +
            "}");

    static final String UPLOAD_ACCOUNT_SNAP_RESPONSE = json("{\n" +
            "    \"wizard_session_key\": null,\n" +
            "    \"location\": null,\n" +
            "    \"id\": \"01401df3d75e40f8a8eadf0694a55ff1snap\",\n" +
            "    \"account_id\": null\n" +
            "}");
    public static final String GET_APPLICATION_STATUS_RESPONSE = json("{\n" +
            "    \"status_description\": null,\n" +
            "    \"status\": \"approved\",\n" +
            "    \"signing_id\": \"cbcfd69d8e3c3d870e32310e7e4f7d91csig\",\n" +
            "    \"loan_id\": null,\n" +
            "    \"id\": \"196486efcc594520870207dbb0189f84clap\",\n" +
            "    \"approximate_total_loan_expenses\": {\n" +
            "        \"value\": 174490,\n" +
            "        \"unit\": \"cents\",\n" +
            "        \"currency\": \"EUR\"\n" +
            "    }\n" +
            "}");
    public static final String GET_SIGNINGS_RESPONSE = json("{\n" +
            "    \"status\": \"approved\",\n" +
            "    \"signing_id\": \"cbcfd69d8e3c3d870e32310e7e4f7d91csig\",\n" +
            "    \"identification_id\": null,\n" +
            "    \"id\": \"196486efcc594520870207dbb0189f84clap\",\n" +
            "    \"documents\": [\n" +
            "        {\n" +
            "            \"id\": \"document_1\",\n" +
            "            \"name\": \"cdoc_1\",\n" +
            "            \"document_type\": \"SIGNED_CONTRACT\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"id\": \"document_2\",\n" +
            "            \"name\": \"ldoc_1\",\n" +
            "            \"document_type\": \"SIGNED_CONTRACT\"\n" +
            "        }\n" +
            "    ]\n" +
            "}");
    static final String FTS__ACCOUNTSNAPSHOT_RESPONSE = json("{\n" +
            "    \"account\": {\n" +
            "      \"limit\": \"1000.0\",\n" +
            "      \"joint_account\": false,\n" +
            "      \"iban\": \"DE92370601930002130041\",\n" +
            "      \"holder\": \"MUSTERMANN, HARTMUT\",\n" +
            "      \"description\": \"Girokonto\",\n" +
            "      \"country_id\": \"DE\",\n" +
            "      \"bic\": \"TESTDE88XXX\",\n" +
            "      \"bank_name\": \"TestBank\"\n" +
            "    },\n" +
            "    \"balance\": {\n" +
            "      \"limit\": \"1000.0\",\n" +
            "      \"date\": \"2020-03-10\",\n" +
            "      \"currency\": \"EUR\",\n" +
            "      \"balance\": \"2123.0\",\n" +
            "      \"available\": \"3123.0\"\n" +
            "    },\n" +
            "    \"turnovers\": [\n" +
            "      {\n" +
            "        \"tags\": [\n" +
            "          \"household\",\n" +
            "          \"expenditure\"\n" +
            "        ],\n" +
            "        \"purpose\": [\n" +
            "          \"SEPA BASISLASTSCHRIFT Energy2day GmbH KD-NR. 850721713015-721713 monatlicher Abschlag Discounter-Strom GlaeubigerID DE35EDS00000438652 KUNDENREFERENZ S301008592-8 50721713012016040610001 MANDATSREFERENZ 85072171301 5 GLAEUBIGER-ID DE35EDS00000438652\"\n" +
            "        ],\n" +
            "        \"prebooked\": false,\n" +
            "        \"currency\": \"EUR\",\n" +
            "        \"counter_iban\": \"\",\n" +
            "        \"counter_holder\": \"Energy2day GmbH\",\n" +
            "        \"counter_bic\": \"\",\n" +
            "        \"booking_date\": \"2020-03-07\",\n" +
            "        \"amount\": \"-42.0\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"tags\": [\n" +
            "          \"salary\",\n" +
            "          \"income\",\n" +
            "          \"revenue\"\n" +
            "        ],\n" +
            "        \"purpose\": [\n" +
            "          \"SEPA-GEHALTSGUTSCHRIFT FinTecSystems GmbH LOHN / GEHALT 12/16 KUNDENREFERENZ 7235703105-0 001008LG0000\"\n" +
            "        ],\n" +
            "        \"prebooked\": false,\n" +
            "        \"currency\": \"EUR\",\n" +
            "        \"counter_iban\": \"\",\n" +
            "        \"counter_holder\": \"FinTecSystems GmbH\",\n" +
            "        \"counter_bic\": \"\",\n" +
            "        \"booking_date\": \"2020-03-08\",\n" +
            "        \"amount\": \"3509.0\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"tags\": [\n" +
            "          \"expenditure\"\n" +
            "        ],\n" +
            "        \"purpose\": [\n" +
            "          \"SEPA BASISLASTSCHRIFT IM AUFTR.V. HOLDER EXTRA-KONTO 123123123 MANDATSREFERENZ 12312312 DAASEPAP.01.001.113573 GLAEUBIGER-ID DE65ING000000000000\"\n" +
            "        ],\n" +
            "        \"prebooked\": false,\n" +
            "        \"currency\": \"EUR\",\n" +
            "        \"counter_iban\": \"\",\n" +
            "        \"counter_holder\": \"HOLDER\",\n" +
            "        \"counter_bic\": \"\",\n" +
            "        \"booking_date\": \"2020-03-08\",\n" +
            "        \"amount\": \"-2.0\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"tags\": [\n" +
            "          \"e-commerce\",\n" +
            "          \"expenditure\"\n" +
            "        ],\n" +
            "        \"purpose\": [\n" +
            "          \"SEPA BASISLASTSCHRIFT PayPal Europe S.a.r.l. et C ie S.C.A PP.4161.PP . SHIZOOSERVI, Ihr Einkauf bei SHIZOOSERVI KUNDENREFERENZ 100042965042 7 PP.4161.PP PAYPAL MANDATSREFERENZ 57NJ224MRKE 5J GLAEUBIGER-ID LU96ZZZ0000000000000000058\"\n" +
            "        ],\n" +
            "        \"prebooked\": false,\n" +
            "        \"currency\": \"EUR\",\n" +
            "        \"counter_iban\": \"\",\n" +
            "        \"counter_holder\": \"PayPal Europe\",\n" +
            "        \"counter_bic\": \"\",\n" +
            "        \"booking_date\": \"2020-03-08\",\n" +
            "        \"amount\": \"-15.55\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"tags\": [\n" +
            "          \"refund\",\n" +
            "          \"rent\",\n" +
            "          \"rental-revenue-habitation\",\n" +
            "          \"revenue\"\n" +
            "        ],\n" +
            "        \"purpose\": [\n" +
            "          \"SEPA-GUTSCHRIFT FELIX nebenkosten rueckerstattung 2015\"\n" +
            "        ],\n" +
            "        \"prebooked\": false,\n" +
            "        \"currency\": \"EUR\",\n" +
            "        \"counter_iban\": \"\",\n" +
            "        \"counter_holder\": \"Felix Mustermann\",\n" +
            "        \"counter_bic\": \"\",\n" +
            "        \"booking_date\": \"2020-03-09\",\n" +
            "        \"amount\": \"100.0\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"tags\": [\n" +
            "          \"credit\",\n" +
            "          \"expenditure\"\n" +
            "        ],\n" +
            "        \"purpose\": [\n" +
            "          \"SEPA BASISLASTSCHRIFT COMMERZ FINANZ GMBH 44238936379001160401 1 KUNDENREFERENZ 2222222222 01160401 1 MANDATSREFERENZ 333333333 92R GLAEUBIGER-ID DE01234566\"\n" +
            "        ],\n" +
            "        \"prebooked\": false,\n" +
            "        \"currency\": \"EUR\",\n" +
            "        \"counter_iban\": \"\",\n" +
            "        \"counter_holder\": \"COMMERZ FINANZ GMBH\",\n" +
            "        \"counter_bic\": \"\",\n" +
            "        \"booking_date\": \"2020-03-09\",\n" +
            "        \"amount\": \"-128.69\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"tags\": [\n" +
            "          \"insurance-indemnity-accident\",\n" +
            "          \"insurance\",\n" +
            "          \"expenditure\"\n" +
            "        ],\n" +
            "        \"purpose\": [\n" +
            "          \"SEPA BASISLASTSCHRIFT IM AUFTR.V. HUK24 AG HUK24, HAUSRAT-VERSICHERUNG 1234567 KUNDENREFERENZ 123456 29. 12.16 760/593847-V-14F MANDATSREFERENZ MB027129219 GLAEUBIGER-ID DE17ZZZ0000000000\"\n" +
            "        ],\n" +
            "        \"prebooked\": false,\n" +
            "        \"currency\": \"EUR\",\n" +
            "        \"counter_iban\": \"\",\n" +
            "        \"counter_holder\": \"HUK24 AG\",\n" +
            "        \"counter_bic\": \"\",\n" +
            "        \"booking_date\": \"2020-03-09\",\n" +
            "        \"amount\": \"-40.36\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"tags\": [\n" +
            "          \"telecommunication\",\n" +
            "          \"expenditure\"\n" +
            "        ],\n" +
            "        \"purpose\": [\n" +
            "          \"SEPA BASISLASTSCHRIFT Kabelfernsehen Muenchen KD.NR.: 123456 LT. CABLESUR F/ FON VERTRAG: 01/2017 Hartmut Mustermann KUNDENREFERENZ 0000A2017012 6151101260117300301168 MANDATSREFERENZ 301168 GLAEUBIGER-ID DE28ZZZ00000000000\"\n" +
            "        ],\n" +
            "        \"prebooked\": false,\n" +
            "        \"currency\": \"EUR\",\n" +
            "        \"counter_iban\": \"\",\n" +
            "        \"counter_holder\": \"Kabelfernsehen Muenchen\",\n" +
            "        \"counter_bic\": \"\",\n" +
            "        \"booking_date\": \"2020-03-09\",\n" +
            "        \"amount\": \"-34.9\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"tags\": [\n" +
            "          \"telecommunication\",\n" +
            "          \"expenditure\"\n" +
            "        ],\n" +
            "        \"purpose\": [\n" +
            "          \"SEPA BASISLASTSCHRIFT Telekom Deutschland GmbH MOBILFUNK KUNDENKONTO 230492039403294032940/12 .08.16 KUNDENREFERENZ ZAHLUNGSBELE G 24234234234 MANDATSREFERENZ DE00000000 600000000000000007955628 GLAEUBIGER-ID DE93ZZZ0000000000\"\n" +
            "        ],\n" +
            "        \"prebooked\": false,\n" +
            "        \"currency\": \"EUR\",\n" +
            "        \"counter_iban\": \"\",\n" +
            "        \"counter_holder\": \"Telekom Deutschland GmbH\",\n" +
            "        \"counter_bic\": \"\",\n" +
            "        \"booking_date\": \"2020-03-09\",\n" +
            "        \"amount\": \"-87.3\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"tags\": [\n" +
            "          \"media\",\n" +
            "          \"expenditure\"\n" +
            "        ],\n" +
            "        \"purpose\": [\n" +
            "          \"SEPA BASISLASTSCHRIFT maxdome GmbH . TXID 123456 29 . KUNDENREFERENZ 123456 29 . MANDATSREFERENZ PO-23423423. GLAEUBIGER-ID DE12ZZZ00000000000\"\n" +
            "        ],\n" +
            "        \"prebooked\": false,\n" +
            "        \"currency\": \"EUR\",\n" +
            "        \"counter_iban\": \"\",\n" +
            "        \"counter_holder\": \"maxdome GmbH\",\n" +
            "        \"counter_bic\": \"\",\n" +
            "        \"booking_date\": \"2020-03-10\",\n" +
            "        \"amount\": \"-2.66\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"date\": \"2020-03-15\",\n" +
            "    \"tagger_version\": \"\",\n" +
            "    \"filters\": [],\n" +
            "    \"days\": 365\n" +
            "  }");
    public static final String GET_IDENTIFICATION_STATUS_RESPONSE = json("{\n" +
            "    \"status\": \"successful\",\n" +
            "    \"id\": \"0de07abd686b43348edbd2421e8ab68accrd\",\n" +
            "    \"created_at\": \"2020-05-06T07:22:54Z\"\n" +
            "}");
}
