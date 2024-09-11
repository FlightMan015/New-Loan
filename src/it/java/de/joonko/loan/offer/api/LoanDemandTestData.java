package de.joonko.loan.offer.api;

import java.time.LocalDate;

public class LoanDemandTestData {
    public static String getRequestWithInvalidDate() {
        return "{" +
                "  \"loanAsked\": 1500," +
                "  \"personalDetails\": {" +
                "    \"gender\": \"MALE\"," +
                "    \"familyStatus\": \"MARRIED\"," +
                "    \"birthDate\": \"22222-05-01\"," +
                "    \"nationality\": \"DE\"," +
                "    \"firstName\": \"test\"," +
                "    \"lastName\": \"test\"" +
                "    \"housingType\": \"OWNER\"" +
                "  }" +
                "}";
    }

    public static String getRequestWithValidDate() {
        return "{\n" +
                "  \"loanAsked\": 1500,\n" +
                "  \"termsAccepted\": true,\n" +
                "  \"dacId\": \"12qw34er56as-09qw23as45df\",\n" +
                "  \"clientIp\": \"127.0.0.1\",\n" +
                "  \"personalDetails\": {\n" +
                "    \"gender\": \"MALE\",\n" +
                "    \"familyStatus\": \"SINGLE\",\n" +
                "    \"firstName\": \"Max\",\n" +
                "    \"lastName\": \"MUSTERMANN\",\n" +
                "    \"placeOfBirth\": \"Berlin\",\n" +
                "    \"nationality\": \"DE\",\n" +
                "    \"birthDate\": \"1990-10-11\",\n" +
                "    \"numberOfChildren\": \"0\",\n" +
                "    \"housingType\": \"OWNER\",\n" +
                "    \"numberOfCreditCard\": 1,\n" +
                "    \"carInformation\": false\n" +
                "  },\n" +
                "  \"expenses\": {\n" +
                "    \"alimony\": 0,\n" +
                "    \"insuranceAndSavings\": 86.37,\n" +
                "    \"loanInstalments\": 257.38,\n" +
                "    \"mortgages\": 0,\n" +
                "    \"privateHealthInsurance\": 1510.3899999999999,\n" +
                "    \"rent\": 1924,\n" +
                "    \"vehicleInsurance\": 1000,\n" +
                "    \"loanInstallmentsSwk\": 100,\n" +
                "    \"acknowledgedMortgages\": 0,\n" +
                "    \"acknowledgedRent\": 1924\n" +
                "  },\n" +
                "\"ftsTransactionId\":\"12ab34cd56ef-78gh90ij12kl34mn\"," +
                "  \"income\": {\n" +
                "    \"alimonyPayments\": 0,\n" +
                "    \"childBenefits\": 0,\n" +
                "    \"netIncome\": 10000,\n" +
                "    \"otherRevenue\": 856.79,\n" +
                "    \"pensionBenefits\": 0,\n" +
                "    \"rentalIncome\": 0,\n" +
                "    \"acknowledgedNetIncome\": 10000\n" +
                "  },\n" +
                "  \"contactData\": {\n" +
                "    \"city\": \"Berlin\",\n" +
                "    \"streetName\": \"Hardenbergstraße\",\n" +
                "    \"houseNumber\": \"32\",\n" +
                "    \"postcode\": \"10623\",\n" +
                "    \"mobile\": \"4917624088579\",\n" +
                "    \"email\": \"qa.user@joonko.io\",\n" +
                "    \"livingSince\": {\n" +
                "      \"month\": 1,\n" +
                "      \"year\": 2020\n" +
                "    },\n" +
                "    \"previousAddress\": {\n" +
                "      \"streetName\": \"Mllerstr. 77\",\n" +
                "      \"postCode\": \"10623\",\n" +
                "      \"city\": \"Berlin\",\n" +
                "      \"country\": \"DE\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"employmentDetails\": {\n" +
                "    \"employmentType\": \"REGULAR_EMPLOYED\",\n" +
                "    \"employerName\": \"Joonko AG\",\n" +
                "    \"employmentSince\": {\n" +
                "      \"month\": 5,\n" +
                "      \"year\": 2012\n" +
                "    },\n" +
                "    \"streetName\": \"Hardenberstr.\",\n" +
                "    \"postcode\": \"10587\",\n" +
                "    \"city\": \"Berlin\"\n" +
                "  },\n" +
                "\"customDACData\": {\n"+
                "\"has3IncomeTags\": true,\n"+
                "\"netIncomeHasGovSupport\": false,\n"+
                "\"carInformation\": true\n"+
                "},\n"+
                "  \"accountDetails\": {\n" +
                "    \"balance\": 2123,\n" +
                "    \"balanceDate\": \"2020-03-13\",\n" +
                "    \"bic\": \"BYLADEM1001\",\n" +
                "    \"currency\": \"EUR\",\n" +
                "    \"iban\": \"DE12500105170648489890\",\n" +
                "    \"limit\": 1000,\n" +
                "    \"nameOnAccount\": \"MUSTERMANN, HARTMUT\",\n" +
                "    \"createdAt\": \"2020-08-31T15:53:10.547Z\",\n" +
                "    \"days\": 90,\n" +
                "    \"transactions\": [\n" +
                "      {\n" +
                "        \"amount\": -9.55,\n" +
                "        \"iban\": \"\",\n" +
                "        \"bic\": \"\",\n" +
                "        \"bookingDate\": \"2020-03-13\",\n" +
                "        \"purpose\": \"ABSCHLUSS KEINE BELEG INFORMATIONEN, SIEHE GGF. KONTOAUSZUG !\",\n" +
                "        \"isPreBooked\": false\n" +
                "      },\n" +
                "      {\n" +
                "        \"amount\": -962,\n" +
                "        \"iban\": \"\",\n" +
                "        \"bic\": \"\",\n" +
                "        \"bookingDate\": \"2020-03-13\",\n" +
                "        \"purpose\": \"SEPA-DAUERAUFTRAG EMPFAENGER HAUSVERWALTUNG Musterhaus IBAN DE234234324234 BIC GENODEF1AAAA Order-Nr. 00022688654 VERWENDUNGSZWECK MIETE MUSTERMANN\",\n" +
                "        \"isPreBooked\": false\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  \"accountSnapshot\" : {\n" +
                "    \t\"account\": {\n" +
                "        \"limit\": \"1000.0\",\n" +
                "        \"joint_account\": false,\n" +
                "        \"iban\": \"DE92370601930002130041\",\n" +
                "        \"holder\": \"MUSTERMANN, HARTMUT\",\n" +
                "        \"description\": \"Girokonto\",\n" +
                "        \"country_id\": \"DE\",\n" +
                "        \"bic\": \"TESTDE88XXX\",\n" +
                "        \"bank_name\": \"TestBank\"\n" +
                "    },\n" +
                "    \"balance\": {\n" +
                "        \"limit\": \"1000.0\",\n" +
                "        \"date\": \"2020-03-10\",\n" +
                "        \"currency\": \"EUR\",\n" +
                "        \"balance\": \"2123.0\",\n" +
                "        \"available\": \"3123.0\"\n" +
                "    },\n" +
                "    \"turnovers\": [\n" +
                "        {\n" +
                "            \"tags\": [\n" +
                "                \"household\",\n" +
                "                \"expenditure\"\n" +
                "            ],\n" +
                "            \"purpose\": [\n" +
                "                \"SEPA BASISLASTSCHRIFT Energy2day GmbH KD-NR. 850721713015-721713 monatlicher Abschlag Discounter-Strom GlaeubigerID DE35EDS00000438652 KUNDENREFERENZ S301008592-8 50721713012016040610001 MANDATSREFERENZ 85072171301 5 GLAEUBIGER-ID DE35EDS00000438652\"\n" +
                "            ],\n" +
                "            \"prebooked\": false,\n" +
                "            \"currency\": \"EUR\",\n" +
                "            \"counter_iban\": \"\",\n" +
                "            \"counter_holder\": \"Energy2day GmbH\",\n" +
                "            \"counter_bic\": \"\",\n" +
                "            \"booking_date\": \"2020-03-07\",\n" +
                "            \"amount\": \"-42.0\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"tags\": [\n" +
                "                \"salary\",\n" +
                "                \"income\",\n" +
                "                \"revenue\"\n" +
                "            ],\n" +
                "            \"purpose\": [\n" +
                "                \"SEPA-GEHALTSGUTSCHRIFT FinTecSystems GmbH LOHN / GEHALT 12/16 KUNDENREFERENZ 7235703105-0 001008LG0000\"\n" +
                "            ],\n" +
                "            \"prebooked\": false,\n" +
                "            \"currency\": \"EUR\",\n" +
                "            \"counter_iban\": \"\",\n" +
                "            \"counter_holder\": \"FinTecSystems GmbH\",\n" +
                "            \"counter_bic\": \"\",\n" +
                "            \"booking_date\": \"2020-03-08\",\n" +
                "            \"amount\": \"3509.0\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"tags\": [\n" +
                "                \"expenditure\"\n" +
                "            ],\n" +
                "            \"purpose\": [\n" +
                "                \"SEPA BASISLASTSCHRIFT IM AUFTR.V. HOLDER EXTRA-KONTO 123123123 MANDATSREFERENZ 12312312 DAASEPAP.01.001.113573 GLAEUBIGER-ID DE65ING000000000000\"\n" +
                "            ],\n" +
                "            \"prebooked\": false,\n" +
                "            \"currency\": \"EUR\",\n" +
                "            \"counter_iban\": \"\",\n" +
                "            \"counter_holder\": \"HOLDER\",\n" +
                "            \"counter_bic\": \"\",\n" +
                "            \"booking_date\": \"2020-03-08\",\n" +
                "            \"amount\": \"-2.0\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"tags\": [\n" +
                "                \"e-commerce\",\n" +
                "                \"expenditure\"\n" +
                "            ],\n" +
                "            \"purpose\": [\n" +
                "                \"SEPA BASISLASTSCHRIFT PayPal Europe S.a.r.l. et C ie S.C.A PP.4161.PP . SHIZOOSERVI, Ihr Einkauf bei SHIZOOSERVI KUNDENREFERENZ 100042965042 7 PP.4161.PP PAYPAL MANDATSREFERENZ 57NJ224MRKE 5J GLAEUBIGER-ID LU96ZZZ0000000000000000058\"\n" +
                "            ],\n" +
                "            \"prebooked\": false,\n" +
                "            \"currency\": \"EUR\",\n" +
                "            \"counter_iban\": \"\",\n" +
                "            \"counter_holder\": \"PayPal Europe\",\n" +
                "            \"counter_bic\": \"\",\n" +
                "            \"booking_date\": \"2020-03-08\",\n" +
                "            \"amount\": \"-15.55\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"tags\": [\n" +
                "                \"refund\",\n" +
                "                \"rent\",\n" +
                "                \"rental-revenue-habitation\",\n" +
                "                \"revenue\"\n" +
                "            ],\n" +
                "            \"purpose\": [\n" +
                "                \"SEPA-GUTSCHRIFT FELIX nebenkosten rueckerstattung 2015\"\n" +
                "            ],\n" +
                "            \"prebooked\": false,\n" +
                "            \"currency\": \"EUR\",\n" +
                "            \"counter_iban\": \"\",\n" +
                "            \"counter_holder\": \"Felix Mustermann\",\n" +
                "            \"counter_bic\": \"\",\n" +
                "            \"booking_date\": \"2020-03-09\",\n" +
                "            \"amount\": \"100.0\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"date\": \"2020-03-15\",\n" +
                "    \"tagger_version\": \"\",\n" +
                "    \"filters\": [],\n" +
                "    \"days\": 365\n" +
                "    }\n" +
                "}";
    }

    public static ContactData getContactData() {
        return ContactData.builder()
                .city("Berlin")
                .streetName("Hardenbergstraße")
                .postCode("10623")
                .houseNumber("32")
                .email("someone@joonko.io")
                .mobile("491748273421011")
                .livingSince(ShortDate.builder()
                        .month(11)
                        .year(2020)
                        .build())
                .previousAddress(LoanDemandFixtures.getPreviousAddress())
                .build();
    }

    public static PersonalDetails getPersonalDetails() {
        return PersonalDetails.builder()
                .numberOfChildren(0)
                .birthDate(LocalDate.now()
                        .minusYears(25)
                        .minusDays(150))
                .familyStatus(FamilyStatus.MARRIED)
                .firstName("Joonko")
                .gender(Gender.MALE)
                .housingType(HousingType.OWNER)
                .lastName("Finleap")
                .nationality(Nationality.DE)
                .numberOfCreditCard(1)
                .placeOfBirth("SOMEWHERE")
                .build();
    }

    public static EmploymentDetails getEmploymentDetails() {
        return EmploymentDetails.builder()
                .city("Berlin")
                .employerName("Joonko AG")
                .employmentSince(new ShortDate(5, 2012))
                .postCode("10587")
                .employmentType(de.joonko.loan.offer.api.EmploymentType.REGULAR_EMPLOYED)
                .streetName("HardenbergStr.")
                .build();
    }

    public static CustomDACData getCustomDACDataThatWillNotFilterSantander() {
        return CustomDACData.builder()
                .has3IncomeTags(true)
                .netIncomeHasGovSupport(false)
                .carInformation(true)
                .build();
    }
}
