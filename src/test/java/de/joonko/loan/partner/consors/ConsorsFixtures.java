package de.joonko.loan.partner.consors;

import de.joonko.loan.partner.consors.model.SubscriptionStatus;

public class ConsorsFixtures {

    public static String get200Auth() {
        return "{\n" +
                "   \"token\": \"jwtToken\"\n" +
                "}";
    }


    public static final String PRODUCT_RESPONSE = "{\n" +
            "  \"products\": {\n" +
            "    \"1998_810\": {\n" +
            "      \"code\": \"810\",\n" +
            "      \"description\": \"Persönlicher Bedarf\",\n" +
            "      \"insuranceTypes\": [\n" +
            "        \"DEATH_DISABILITY\",\n" +
            "        \"DEATH\",\n" +
            "        \"DEATH_DISABILITY_UNEMPLOYMENT\"\n" +
            "      ],\n" +
            "      \"insuranceCondition\": {\n" +
            "        \"insuranceAddOn\": {\n" +
            "          \"DEATH_DISABILITY_UNEMPLOYMENT\": [],\n" +
            "          \"DEATH\": [],\n" +
            "          \"DEATH_DISABILITY\": []\n" +
            "        }\n" +
            "      },\n" +
            "      \"financialConditionConfigurations\": [\n" +
            "        {\n" +
            "          \"interestRate\": 4.90,\n" +
            "          \"duration\": {\n" +
            "            \"minimum\": 6,\n" +
            "            \"maximum\": 36\n" +
            "          },\n" +
            "          \"creditLimit\": {\n" +
            "            \"minimum\": 1500,\n" +
            "            \"maximum\": 50000\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"interestRate\": 7.90,\n" +
            "          \"duration\": {\n" +
            "            \"minimum\": 85,\n" +
            "            \"maximum\": 120\n" +
            "          },\n" +
            "          \"creditLimit\": {\n" +
            "            \"minimum\": 1500,\n" +
            "            \"maximum\": 50000\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"interestRate\": 5.90,\n" +
            "          \"duration\": {\n" +
            "            \"minimum\": 37,\n" +
            "            \"maximum\": 84\n" +
            "          },\n" +
            "          \"creditLimit\": {\n" +
            "            \"minimum\": 1500,\n" +
            "            \"maximum\": 50000\n" +
            "          }\n" +
            "        }\n" +
            "      ],\n" +
            "      \"_links\": [\n" +
            "        {\n" +
            "          \"name\": \"Financial Calculations\",\n" +
            "          \"href\": \"/partner/freie_verfuegung/financialcalculations?version=5.0\",\n" +
            "          \"method\": \"GET\",\n" +
            "          \"rel\": \"_financialcalculations\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"Representative Example\",\n" +
            "          \"href\": \"/partner/freie_verfuegung/financialcalculations/sample?version=5.0\",\n" +
            "          \"method\": \"GET\",\n" +
            "          \"rel\": \"_financialcalculations/sample\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"Validation Rules\",\n" +
            "          \"href\": \"/partner/freie_verfuegung/validationrules?version=5.0\",\n" +
            "          \"method\": \"GET\",\n" +
            "          \"rel\": \"_validationrules\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"Authenticate Access Token\",\n" +
            "          \"href\": \"/partner/neu_test/accessToken?version=5.0\",\n" +
            "          \"method\": \"POST\",\n" +
            "          \"rel\": \"_accessToken\"\n" +
            "        }\n" +
            "      ],\n" +
            "      \"type\": \"CL\"\n" +
            "    }\n" +
            "  }\n" +
            "}\n";

    static final String PRODUCT_RESPONSE_WITHOUT_VALIDATION_LINKS = "{\n" +
            "  \"products\": {\n" +
            "    \"1998_810\": {\n" +
            "      \"code\": \"810\",\n" +
            "      \"description\": \"Persönlicher Bedarf\",\n" +
            "      \"insuranceTypes\": [\n" +
            "        \"DEATH_DISABILITY\",\n" +
            "        \"DEATH\",\n" +
            "        \"DEATH_DISABILITY_UNEMPLOYMENT\"\n" +
            "      ],\n" +
            "      \"insuranceCondition\": {\n" +
            "        \"insuranceAddOn\": {\n" +
            "          \"DEATH_DISABILITY_UNEMPLOYMENT\": [],\n" +
            "          \"DEATH\": [],\n" +
            "          \"DEATH_DISABILITY\": []\n" +
            "        }\n" +
            "      },\n" +
            "      \"financialConditionConfigurations\": [\n" +
            "        {\n" +
            "          \"interestRate\": 4.90,\n" +
            "          \"duration\": {\n" +
            "            \"minimum\": 6,\n" +
            "            \"maximum\": 36\n" +
            "          },\n" +
            "          \"creditLimit\": {\n" +
            "            \"minimum\": 1500,\n" +
            "            \"maximum\": 50000\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"interestRate\": 7.90,\n" +
            "          \"duration\": {\n" +
            "            \"minimum\": 85,\n" +
            "            \"maximum\": 120\n" +
            "          },\n" +
            "          \"creditLimit\": {\n" +
            "            \"minimum\": 1500,\n" +
            "            \"maximum\": 50000\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"interestRate\": 5.90,\n" +
            "          \"duration\": {\n" +
            "            \"minimum\": 37,\n" +
            "            \"maximum\": 84\n" +
            "          },\n" +
            "          \"creditLimit\": {\n" +
            "            \"minimum\": 1500,\n" +
            "            \"maximum\": 50000\n" +
            "          }\n" +
            "        }\n" +
            "      ],\n" +
            "      \"_links\": [\n" +
            "        {\n" +
            "          \"name\": \"Financial Calculations\",\n" +
            "          \"href\": \"/partner/freie_verfuegung/financialcalculations?version=5.0\",\n" +
            "          \"method\": \"GET\",\n" +
            "          \"rel\": \"_financialcalculations\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"Representative Example\",\n" +
            "          \"href\": \"/partner/freie_verfuegung/financialcalculations/sample?version=5.0\",\n" +
            "          \"method\": \"GET\",\n" +
            "          \"rel\": \"_financialcalculations/sample\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"Authenticate Access Token\",\n" +
            "          \"href\": \"/partner/neu_test/accessToken?version=5.0\",\n" +
            "          \"method\": \"POST\",\n" +
            "          \"rel\": \"_accessToken\"\n" +
            "        }\n" +
            "      ],\n" +
            "      \"type\": \"CL\"\n" +
            "    }\n" +
            "  }\n" +
            "}\n";

    public static final String VALIDATION_RULES_RESPONSE = "{ \"_links\": [\n" +
            "    {\n" +
            "      \"name\": \"Validate subscriber Data\",\n" +
            "      \"href\": \"/subscription/freie_verfuegung?version=5.0\",\n" +
            "      \"method\": \"POST\",\n" +
            "      \"rel\": \"_validatesubscription\"\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    public static final String VALIDATION_RULES_RESPONSE_WITHOUT_LINKS = "{ \"_links\": [\n" +
            "    {\n" +
            "      \"name\": \"Validate subscriber Data\",\n" +
            "      \"href\": \"/subscription/freie_verfuegung?version=5.0\",\n" +
            "      \"method\": \"POST\",\n" +
            "      \"rel\": \"Wrong Links\"\n" +
            "    }\n" +
            "  ]\n" +
            "}";
    public static final String PERSONALIZED_CALCULATION_RESPONSE = "{\n" +
            "  \"financialCalculations\": {\n" +
            "    \"defaultIndex\": 637,\n" +
            "    \"financialCalculation\": [\n" +
            "      {\n" +
            "        \"index\": 0,\n" +
            "        \"creditAmount\": 1500,\n" +
            "        \"duration\": 6,\n" +
            "        \"monthlyRate\": 253.5,\n" +
            "        \"effectiveRate\": 4.9,\n" +
            "        \"nominalRate\": 4.78,\n" +
            "        \"totalInterestAmount\": 21,\n" +
            "        \"totalPayment\": 1521\n" +
            "      },\n" +
            "      {\n" +
            "        \"index\": 1,\n" +
            "        \"creditAmount\": 1500,\n" +
            "        \"duration\": 12,\n" +
            "        \"monthlyRate\": 128.26,\n" +
            "        \"effectiveRate\": 4.9,\n" +
            "        \"nominalRate\": 4.78,\n" +
            "        \"totalInterestAmount\": 39.12,\n" +
            "        \"totalPayment\": 1539.12\n" +
            "      },\n" +
            "      {\n" +
            "        \"index\": 2,\n" +
            "        \"creditAmount\": 1500,\n" +
            "        \"duration\": 24,\n" +
            "        \"monthlyRate\": 65.66,\n" +
            "        \"effectiveRate\": 4.9,\n" +
            "        \"nominalRate\": 4.78,\n" +
            "        \"totalInterestAmount\": 75.84,\n" +
            "        \"totalPayment\": 1575.84\n" +
            "      },\n" +
            "      {\n" +
            "        \"index\": 3,\n" +
            "        \"creditAmount\": 2800,\n" +
            "        \"duration\": 12,\n" +
            "        \"monthlyRate\": 128.26,\n" +
            "        \"effectiveRate\": 4.9,\n" +
            "        \"nominalRate\": 4.78,\n" +
            "        \"totalInterestAmount\": 39.12,\n" +
            "        \"totalPayment\": 2839.12\n" +
            "      },\n" +
            "      {\n" +
            "        \"index\": 4,\n" +
            "        \"creditAmount\": 1500,\n" +
            "        \"duration\": 78,\n" +
            "        \"monthlyRate\": 15.66,\n" +
            "        \"effectiveRate\": 4.9,\n" +
            "        \"nominalRate\": 4.78,\n" +
            "        \"totalInterestAmount\": 75.84,\n" +
            "        \"totalPayment\": 1575.84\n" +
            "      }\n" +
            "      \n" +
            "    ],\n" +
            "    \"insuranceTypes\": [\n" +
            "      \"DEATH_DISABILITY\",\n" +
            "      \"DEATH\",\n" +
            "      \"DEATH_DISABILITY_UNEMPLOYMENT\"\n" +
            "    ],\n" +
            "    \"durationStepping\": [\n" +
            "      6,\n" +
            "      12,\n" +
            "      24,\n" +
            "      36,\n" +
            "      48,\n" +
            "      60,\n" +
            "      72,\n" +
            "      84,\n" +
            "      96,\n" +
            "      108,\n" +
            "      120\n" +
            "    ],\n" +
            "    \"amountStepping\": [\n" +
            "      1500,\n" +
            "      2000,\n" +
            "      2500,\n" +
            "      3000,\n" +
            "      3500,\n" +
            "      4000,\n" +
            "      4500,\n" +
            "      5000,\n" +
            "      5500,\n" +
            "      6000,\n" +
            "      6500,\n" +
            "      7000,\n" +
            "      7500,\n" +
            "      8000,\n" +
            "      8500,\n" +
            "      9000,\n" +
            "      9500,\n" +
            "      10000,\n" +
            "      11000,\n" +
            "      12000,\n" +
            "      13000,\n" +
            "      14000,\n" +
            "      15000,\n" +
            "      16000,\n" +
            "      17000,\n" +
            "      18000,\n" +
            "      19000,\n" +
            "      20000,\n" +
            "      21000,\n" +
            "      22000,\n" +
            "      23000,\n" +
            "      24000,\n" +
            "      25000,\n" +
            "      26000,\n" +
            "      27000,\n" +
            "      28000,\n" +
            "      29000,\n" +
            "      30000,\n" +
            "      31000,\n" +
            "      32000,\n" +
            "      33000,\n" +
            "      34000,\n" +
            "      35000,\n" +
            "      36000,\n" +
            "      37000,\n" +
            "      38000,\n" +
            "      39000,\n" +
            "      40000,\n" +
            "      41000,\n" +
            "      42000,\n" +
            "      43000,\n" +
            "      44000,\n" +
            "      45000,\n" +
            "      46000,\n" +
            "      47000,\n" +
            "      48000,\n" +
            "      49000,\n" +
            "      50000\n" +
            "    ],\n" +
            "    \"subscriptionStatus\": \"INCOMPLETE\",\n" +
            "    \"_links\": [\n" +
            "      {\n" +
            "        \"name\": \"Insurance Calculations\",\n" +
            "        \"href\": \"/subscription/freie_verfuegung/insurance?version=5.0\",\n" +
            "        \"method\": \"POST\",\n" +
            "        \"rel\": \"_insurancecalculations\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"name\": \"Finalize Subscription\",\n" +
            "        \"href\": \"/subscription/freie_verfuegung/6178796253616d6f6d70505045356c394f4f384562536a6953453667564934672b352f55742b57663836303d/finalizesubscription?version=5.0\",\n" +
            "        \"method\": \"PUT\",\n" +
            "        \"rel\": \"_finalizesubscription\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"debugInfo\": {\n" +
            "      \"mDebtFactor\": \"0.0\",\n" +
            "      \"mMinimumRate\": \"0.0\",\n" +
            "      \"customerSpecificDiscount\": \"0.0\",\n" +
            "      \"mBasicDiscount\": \"2.2250738585072014E-308\",\n" +
            "      \"mBasicRate\": \"2.2250738585072014E-308\",\n" +
            "      \"mAgencyDecisionCode\": \"100.0\",\n" +
            "      \"mESDecision\": \"100.0\",\n" +
            "      \"mESDecisionAml\": \"0\",\n" +
            "      \"mESDecisionAmlCode\": \"100.0\",\n" +
            "      \"mDecisionCode\": \"PRO\",\n" +
            "      \"mExpectedLossOfInstallmentCredit\": \"1.78\",\n" +
            "      \"mMaxDurationForInstallmentLoans\": \"144.0\",\n" +
            "      \"mMaxLineForInstallmentLoanPreNoDocs\": \"0.0\",\n" +
            "      \"mMaxLineForInstallmentLoanPreDocs\": \"0.0\",\n" +
            "      \"mMaxLineForInstallmentLoanNoDocs\": \"73530.0\",\n" +
            "      \"mMaxLineForInstallmentLoanDocs\": \"0.0\",\n" +
            "      \"mBICDMA\": \"0.0\",\n" +
            "      \"mDMA\": \"2.2250738585072014E-308\",\n" +
            "      \"insuranceCommision\": \"2.2250738585072014E-308\",\n" +
            "      \"issueCommision\": \"2.2250738585072014E-308\",\n" +
            "      \"variableCommision\": \"2.2250738585072014E-308\",\n" +
            "      \"customerRunningProducts\": []\n" +
            "    }\n" +
            "  }\n" +
            "}";

    public static final String VALIDATE_SUBSCRIPTION_RESPONSE = "{\n" +
            "  \"subscriptionidentifier\": \"744151656e5743456b614730422b7379626a7464356d42736f6f7a38527779327a777a6c6f754d4f7645413d\",\n" +
            "  \"_links\": [\n" +
            "    {\n" +
            "      \"name\": \"Personalized calculations\",\n" +
            "      \"href\": \"/subscription/freie_verfuegung/744151656e5743456b614730422b7379626a7464356d42736f6f7a38527779327a777a6c6f754d4f7645413d/personalizedcalculations?version=5.0\",\n" +
            "      \"method\": \"PUT\",\n" +
            "      \"rel\": \"_personalizedcalculations\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"update subscriber Data\",\n" +
            "      \"href\": \"/subscription/freie_verfuegung/744151656e5743456b614730422b7379626a7464356d42736f6f7a38527779327a777a6c6f754d4f7645413d?version=5.0\",\n" +
            "      \"method\": \"PUT\",\n" +
            "      \"rel\": \"_updatesubscription\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"Finalize Subscription\",\n" +
            "      \"href\": \"/subscription/freie_verfuegung/744151656e5743456b614730422b7379626a7464356d42736f6f7a38527779327a777a6c6f754d4f7645413d/finalizesubscription?version=5.0\",\n" +
            "      \"method\": \"PUT\",\n" +
            "      \"rel\": \"_finalizesubscription\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"errors\": {},\n" +
            "  \"warning\": {\n" +
            "    \"city\": [\n" +
            "      \"München\"\n" +
            "    ],\n" +
            "    \"street\": [\n" +
            "      \"Leopoldstr. 120\"\n" +
            "    ],\n" +
            "    \"zipcode\": [\n" +
            "      \"80802\"\n" +
            "    ]\n" +
            "  }\n" +
            "}";

    public static final String VALIDATE_SUBSCRIPTION_RESPONSE_WITHOUT_LINK = "{\n" +
            "  \"subscriptionidentifier\": \"744151656e5743456b614730422b7379626a7464356d42736f6f7a38527779327a777a6c6f754d4f7645413d\",\n" +
            "  \"_links\": [\n" +
            "    {\n" +
            "      \"name\": \"update subscriber Data\",\n" +
            "      \"href\": \"/subscription/freie_verfuegung/744151656e5743456b614730422b7379626a7464356d42736f6f7a38527779327a777a6c6f754d4f7645413d?version=5.0\",\n" +
            "      \"method\": \"PUT\",\n" +
            "      \"rel\": \"_updatesubscription\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"Finalize Subscription\",\n" +
            "      \"href\": \"/subscription/freie_verfuegung/744151656e5743456b614730422b7379626a7464356d42736f6f7a38527779327a777a6c6f754d4f7645413d/finalizesubscription?version=5.0\",\n" +
            "      \"method\": \"PUT\",\n" +
            "      \"rel\": \"_finalizesubscription\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"errors\": {},\n" +
            "  \"warning\": {\n" +
            "    \"city\": [\n" +
            "      \"München\"\n" +
            "    ],\n" +
            "    \"street\": [\n" +
            "      \"Leopoldstr. 120\"\n" +
            "    ],\n" +
            "    \"zipcode\": [\n" +
            "      \"80802\"\n" +
            "    ]\n" +
            "  }\n" +
            "}";

    public static String getFinalizeCalculationResponse(SubscriptionStatus subscriptionStatus) {
        switch (subscriptionStatus) {
            case STUDY:
                return FINALIZE_CALCULATION_RESPONSE_STUDY;
            case APPROVED:
                return FINALIZE_CALCULATION_RESPONSE_APPROVED;
            case REFUSED:
                return FINALIZE_CALCULATION_RESPONSE_REFUSED;
            default:
                return "";
        }
    }

    public static final String FINALIZE_CALCULATION_RESPONSE_REFUSED = "{\n" +
            "  \"subscriptionStatus\": \"REFUSED\",\n" +
            "  \"contractIdentifier\": \"77384778\",\n" +
            "  \"errors\": {\n" +
            "  }\n" +
            "}";

    public static final String FINALIZE_CALCULATION_RESPONSE_STUDY = "{\n" +
            "  \"subscriptionStatus\": \"STUDY\",\n" +
            "  \"contractIdentifier\": \"75756571\",\n" +
            "  \"errors\": {\n" +
            "  }\n" +
            "}";

    public static final String FINALIZE_CALCULATION_RESPONSE_APPROVED = "{\n" +
            "  \"supportingDocumentsRequired\": [\n" +
            "  ],\n" +
            "  \"availableLegitiamtionOptions\": [\n" +
            "    \"ONLINE_IDENT\",\n" +
            "    \"POST_IDENT\",\n" +
            "    \"BRANCH_IDENT\"\n" +
            "  ],\n" +
            "  \"subscriptionStatus\": \"APPROVED\",\n" +
            "  \"contractIdentifier\": \"58507465\",\n" +
            "  \"financialCalculation\": {\n" +
            "    \"creditAmount\": 30000,\n" +
            "    \"duration\": 84,\n" +
            "    \"monthlyRate\": 434.61,\n" +
            "    \"effectiveRate\": 5.9,\n" +
            "    \"nominalRate\": 5.75,\n" +
            "    \"totalInterestAmount\": 6507.24,\n" +
            "    \"totalPayment\": 36507.24\n" +
            "  },\n" +
            "  \"errors\": {},\n" +
            "  \"debugInfo\": {\n" +
            "    \"mDebtFactor\": \"0.58\",\n" +
            "    \"mMinimumRate\": \"0.0\",\n" +
            "    \"customerSpecificDiscount\": \"0.0\",\n" +
            "    \"mBasicDiscount\": \"2.2250738585072014E-308\",\n" +
            "    \"mBasicRate\": \"2.2250738585072014E-308\",\n" +
            "    \"mAgencyDecisionCode\": \"100.0\",\n" +
            "    \"mESDecision\": \"2.2250738585072014E-308\",\n" +
            "    \"mESDecisionAml\": \"0\",\n" +
            "    \"mESDecisionAmlCode\": \"100.0\",\n" +
            "    \"mDecisionCode\": \"null\",\n" +
            "    \"mExpectedLossOfInstallmentCredit\": \"0.0\",\n" +
            "    \"mMaxDurationForInstallmentLoans\": \"144.0\",\n" +
            "    \"mMaxLineForInstallmentLoanPreNoDocs\": \"0.0\",\n" +
            "    \"mMaxLineForInstallmentLoanPreDocs\": \"0.0\",\n" +
            "    \"mMaxLineForInstallmentLoanNoDocs\": \"72734.0\",\n" +
            "    \"mMaxLineForInstallmentLoanDocs\": \"0.0\",\n" +
            "    \"mBICDMA\": \"0.0\",\n" +
            "    \"mDMA\": \"2.2250738585072014E-308\",\n" +
            "    \"insuranceCommision\": \"2.2250738585072014E-308\",\n" +
            "    \"issueCommision\": \"2.2250738585072014E-308\",\n" +
            "    \"variableCommision\": \"2.2250738585072014E-308\",\n" +
            "    \"customerRunningProducts\": []\n" +
            "  },\n" +
            "  \"warning\": {},\n" +
            "  \"_links\": [\n" +
            "    {\n" +
            "      \"name\": \"Online Identification and Qualified Electronic Signature\",\n" +
            "      \"href\": \"https://gateway.test.idnow.de/api/v1/cfgesign/bridge/create?data=6c3132766a307255544e5a397a594c2f68494c555035786954714c6c6d56755473413431534f6f35476b4b4266464c5a58512f314a4f4f4d6f547931647968672f6466524973526e45592f443439347a31342f724966376136654b7346694f466b68495568797a6a58304e7962726b6a596e30794d7950364854714666342b69734341444c5966696b4361443546755a46382b376f47417a512b662b744b565768307658667445696a4d766a2f68493935714e524b416d6c302b354573774e4177716c5a4945575a5a313253694a3863587737614158644d46323776716e4d434961437a6f506a636c516d2b3059686468664b544c483734514c42704851344e35714c6b7a5363324a73517774547568747846786350456a6c5949334435693643566668355179425546524436324b72684d63754f544654724545446a39793864592f476d376b5961526671736e646d74796e4c324942706938323153554e6c4d744a62577a696445385a6a624b59316d527933734745554e47504a56794766797231567777387136796e774f4c507a4662703866394d5a394c37574e6b6b5630324e34774a4e3433653032544b74567a6a69654743417547782b39665476784c4b4d61324475436b5034372b4247516c713239575074353433376b374b3047474741654974556b385a4b7351544149384d3137663331306653484c506f692f3434624f5278434a436f564278646e7246497964545673367a5464397a536c666f68584c775730464e686570426d6c63326a456d63646e4b4152447733427653504b6b415644424d7175355878696c376e39314b495955516667544150677542384b446272767476414234654d59737a63486c5051347a5945686a61724a3050544f4a6b7134514b2b727a4e616b796d7a67494c5a337758665765795757483658744974704a49675a624c663731616c7361785a337a644a717170696c794c6d5964624c56705631736a4262776f757449585a3575673d3d&signature=0144e75884b2d7a8ca55d1e8dc525dbaeb55cbc75b4d78432dd3b2bfd94953ba\",\n" +
            "      \"method\": \"GET\",\n" +
            "      \"rel\": \"_onlineIdent\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"Cancel Subscription\",\n" +
            "      \"href\": \"/subscription/3770754a744d4e6a7772446b514250383358643056534e4d4674773232476f6f59355a74506f67576233553d?version=5.0\",\n" +
            "      \"method\": \"DELETE\",\n" +
            "      \"rel\": \"_cancelSubscriptionDocument\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"Download Subscription Document\",\n" +
            "      \"href\": \"/subscription/3770754a744d4e6a7772446b514250383358643056534e4d4674773232476f6f59355a74506f67576233553d/documents?version=5.0\",\n" +
            "      \"method\": \"GET\",\n" +
            "      \"rel\": \"_downloadSubscriptionDocument\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"Email Subscription Document\",\n" +
            "      \"href\": \"/subscription/3770754a744d4e6a7772446b514250383358643056534e4d4674773232476f6f59355a74506f67576233553d/documents/email?version=5.0\",\n" +
            "      \"method\": \"GET\",\n" +
            "      \"rel\": \"_emailSubscriptionDocument\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"daarequired\": false\n" +
            "}";

    public static final String FINALIZE_CALCULATION_YELLOW_PROFILE_RESPONSE = "{\n" +
            "  \"supportingDocumentsRequired\": [\n" +
            "    50055,\n" +
            "    50035,\n" +
            "    30652,\n" +
            "    50038,\n" +
            "    50033,\n" +
            "    50067\n" +
            "  ],\n" +
            "  \"availableLegitiamtionOptions\": [\n" +
            "    \"ONLINE_IDENT\",\n" +
            "    \"POST_IDENT\",\n" +
            "    \"BRANCH_IDENT\"\n" +
            "  ],\n" +
            "  \"subscriptionStatus\": \"APPROVED\",\n" +
            "  \"contractIdentifier\": \"58507465\",\n" +
            "  \"financialCalculation\": {\n" +
            "    \"creditAmount\": 30000,\n" +
            "    \"duration\": 84,\n" +
            "    \"monthlyRate\": 434.61,\n" +
            "    \"effectiveRate\": 5.9,\n" +
            "    \"nominalRate\": 5.75,\n" +
            "    \"totalInterestAmount\": 6507.24,\n" +
            "    \"totalPayment\": 36507.24\n" +
            "  },\n" +
            "  \"errors\": {},\n" +
            "  \"debugInfo\": {\n" +
            "    \"mDebtFactor\": \"0.58\",\n" +
            "    \"mMinimumRate\": \"0.0\",\n" +
            "    \"customerSpecificDiscount\": \"0.0\",\n" +
            "    \"mBasicDiscount\": \"2.2250738585072014E-308\",\n" +
            "    \"mBasicRate\": \"2.2250738585072014E-308\",\n" +
            "    \"mAgencyDecisionCode\": \"100.0\",\n" +
            "    \"mESDecision\": \"2.2250738585072014E-308\",\n" +
            "    \"mESDecisionAml\": \"0\",\n" +
            "    \"mESDecisionAmlCode\": \"100.0\",\n" +
            "    \"mDecisionCode\": \"null\",\n" +
            "    \"mExpectedLossOfInstallmentCredit\": \"0.0\",\n" +
            "    \"mMaxDurationForInstallmentLoans\": \"144.0\",\n" +
            "    \"mMaxLineForInstallmentLoanPreNoDocs\": \"0.0\",\n" +
            "    \"mMaxLineForInstallmentLoanPreDocs\": \"0.0\",\n" +
            "    \"mMaxLineForInstallmentLoanNoDocs\": \"72734.0\",\n" +
            "    \"mMaxLineForInstallmentLoanDocs\": \"0.0\",\n" +
            "    \"mBICDMA\": \"0.0\",\n" +
            "    \"mDMA\": \"2.2250738585072014E-308\",\n" +
            "    \"insuranceCommision\": \"2.2250738585072014E-308\",\n" +
            "    \"issueCommision\": \"2.2250738585072014E-308\",\n" +
            "    \"variableCommision\": \"2.2250738585072014E-308\",\n" +
            "    \"customerRunningProducts\": []\n" +
            "  },\n" +
            "  \"warning\": {},\n" +
            "  \"_links\": [\n" +
            "    {\n" +
            "      \"name\": \"Online Identification and Qualified Electronic Signature\",\n" +
            "      \"href\": \"https://gateway.test.idnow.de/api/v1/cfgesign/bridge/create?data=6c3132766a307255544e5a397a594c2f68494c555035786954714c6c6d56755473413431534f6f35476b4b4266464c5a58512f314a4f4f4d6f547931647968672f6466524973526e45592f443439347a31342f724966376136654b7346694f466b68495568797a6a58304e7962726b6a596e30794d7950364854714666342b69734341444c5966696b4361443546755a46382b376f47417a512b662b744b565768307658667445696a4d766a2f68493935714e524b416d6c302b354573774e4177716c5a4945575a5a313253694a3863587737614158644d46323776716e4d434961437a6f506a636c516d2b3059686468664b544c483734514c42704851344e35714c6b7a5363324a73517774547568747846786350456a6c5949334435693643566668355179425546524436324b72684d63754f544654724545446a39793864592f476d376b5961526671736e646d74796e4c324942706938323153554e6c4d744a62577a696445385a6a624b59316d527933734745554e47504a56794766797231567777387136796e774f4c507a4662703866394d5a394c37574e6b6b5630324e34774a4e3433653032544b74567a6a69654743417547782b39665476784c4b4d61324475436b5034372b4247516c713239575074353433376b374b3047474741654974556b385a4b7351544149384d3137663331306653484c506f692f3434624f5278434a436f564278646e7246497964545673367a5464397a536c666f68584c775730464e686570426d6c63326a456d63646e4b4152447733427653504b6b415644424d7175355878696c376e39314b495955516667544150677542384b446272767476414234654d59737a63486c5051347a5945686a61724a3050544f4a6b7134514b2b727a4e616b796d7a67494c5a337758665765795757483658744974704a49675a624c663731616c7361785a337a644a717170696c794c6d5964624c56705631736a4262776f757449585a3575673d3d&signature=0144e75884b2d7a8ca55d1e8dc525dbaeb55cbc75b4d78432dd3b2bfd94953ba\",\n" +
            "      \"method\": \"GET\",\n" +
            "      \"rel\": \"_onlineIdent\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"Cancel Subscription\",\n" +
            "      \"href\": \"/subscription/3770754a744d4e6a7772446b514250383358643056534e4d4674773232476f6f59355a74506f67576233553d?version=5.0\",\n" +
            "      \"method\": \"DELETE\",\n" +
            "      \"rel\": \"_cancelSubscriptionDocument\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"Download Subscription Document\",\n" +
            "      \"href\": \"/subscription/3770754a744d4e6a7772446b514250383358643056534e4d4674773232476f6f59355a74506f67576233553d/documents?version=5.0\",\n" +
            "      \"method\": \"GET\",\n" +
            "      \"rel\": \"_downloadSubscriptionDocument\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"Email Subscription Document\",\n" +
            "      \"href\": \"/subscription/3770754a744d4e6a7772446b514250383358643056534e4d4674773232476f6f59355a74506f67576233553d/documents/email?version=5.0\",\n" +
            "      \"method\": \"GET\",\n" +
            "      \"rel\": \"_emailSubscriptionDocument\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"daarequired\": false\n" +
            "}";

}
