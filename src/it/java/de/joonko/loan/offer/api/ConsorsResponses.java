package de.joonko.loan.offer.api;

import de.joonko.loan.common.JsonResponses;

public class ConsorsResponses extends JsonResponses {

    static final String TOKEN_RESPONSE = json("{" +
            "  'token': 'Bearer fake-token'" +
            "}");

    static final String PRODUCT_RESPONSE = json("{" +
            "  'products': {" +
            "    '1998_810': {" +
            "      'code': '810'," +
            "      'description': 'Persönlicher Bedarf'," +
            "      'insuranceTypes': [" +
            "        'DEATH_DISABILITY'," +
            "        'DEATH'," +
            "        'DEATH_DISABILITY_UNEMPLOYMENT'" +
            "      ]," +
            "      'insuranceCondition': {" +
            "        'insuranceAddOn': {" +
            "          'DEATH_DISABILITY_UNEMPLOYMENT': []," +
            "          'DEATH': []," +
            "          'DEATH_DISABILITY': []" +
            "        }" +
            "      }," +
            "      'financialConditionConfigurations': [" +
            "        {" +
            "          'interestRate': 4.90," +
            "          'duration': {" +
            "            'minimum': 6," +
            "            'maximum': 36" +
            "          }," +
            "          'creditLimit': {" +
            "            'minimum': 1500," +
            "            'maximum': 50000" +
            "          }" +
            "        }," +
            "        {" +
            "          'interestRate': 7.90," +
            "          'duration': {" +
            "            'minimum': 85," +
            "            'maximum': 120" +
            "          }," +
            "          'creditLimit': {" +
            "            'minimum': 1500," +
            "            'maximum': 50000" +
            "          }" +
            "        }," +
            "        {" +
            "          'interestRate': 5.90," +
            "          'duration': {" +
            "            'minimum': 37," +
            "            'maximum': 84" +
            "          }," +
            "          'creditLimit': {" +
            "            'minimum': 1500," +
            "            'maximum': 50000" +
            "          }" +
            "        }" +
            "      ]," +
            "      '_links': [" +
            "        {" +
            "          'name': 'Financial Calculations'," +
            "          'href': '/partner/freie_verfuegung/financialcalculations?version=5.0'," +
            "          'method': 'GET'," +
            "          'rel': '_financialcalculations'" +
            "        }," +
            "        {" +
            "          'name': 'Representative Example'," +
            "          'href': '/partner/freie_verfuegung/financialcalculations/sample?version=5.0'," +
            "          'method': 'GET'," +
            "          'rel': '_financialcalculations/sample'" +
            "        }," +
            "        {" +
            "          'name': 'Validation Rules'," +
            "          'href': '/partner/freie_verfuegung/validationrules?version=5.0'," +
            "          'method': 'GET'," +
            "          'rel': '_validationrules'" +
            "        }," +
            "        {" +
            "          'name': 'Authenticate Access Token'," +
            "          'href': '/partner/neu_test/accessToken?version=5.0'," +
            "          'method': 'POST'," +
            "          'rel': '_accessToken'" +
            "        }" +
            "      ]," +
            "      'type': 'CL'" +
            "    }" +
            "  }" +
            "}");

    static final String VALIDATION_RULES_RESPONSE = json("{ '_links': [" +
            "    {" +
            "      'name': 'Validate subscriber Data'," +
            "      'href': '/subscription/freie_verfuegung?version=5.0'," +
            "      'method': 'POST'," +
            "      'rel': '_validatesubscription'" +
            "    }" +
            "  ]" +
            "}");

    static final String PERSONALIZED_CALCULATION_SINGLE_OFFER_RESPONSE = json("{" +
            "  'financialCalculations': {" +
            "    'defaultIndex': 637," +
            "    'financialCalculation': [" +
            "      {" +
            "        'index': 0," +
            "        'creditAmount': 1500," +
            "        'duration': 24," +
            "        'monthlyRate': 253.5," +
            "        'effectiveRate': 4.9," +
            "        'nominalRate': 4.78," +
            "        'totalInterestAmount': 21," +
            "        'totalPayment': 1521" +
            "      }" +
            "    ]," +
            "    'insuranceTypes': [" +
            "      'DEATH_DISABILITY'," +
            "      'DEATH'," +
            "      'DEATH_DISABILITY_UNEMPLOYMENT'" +
            "    ]," +
            "    'durationStepping': [" +
            "      6," +
            "      12," +
            "      24," +
            "      36," +
            "      48," +
            "      60," +
            "      72," +
            "      84," +
            "      96," +
            "      108," +
            "      120" +
            "    ]," +
            "    'amountStepping': [" +
            "      1500," +
            "      2000," +
            "      2500," +
            "      3000," +
            "      3500," +
            "      4000," +
            "      4500," +
            "      5000," +
            "      5500," +
            "      6000," +
            "      6500," +
            "      7000," +
            "      7500," +
            "      8000," +
            "      8500," +
            "      9000," +
            "      9500," +
            "      10000," +
            "      11000," +
            "      12000," +
            "      13000," +
            "      14000," +
            "      15000," +
            "      16000," +
            "      17000," +
            "      18000," +
            "      19000," +
            "      20000," +
            "      21000," +
            "      22000," +
            "      23000," +
            "      24000," +
            "      25000," +
            "      26000," +
            "      27000," +
            "      28000," +
            "      29000," +
            "      30000," +
            "      31000," +
            "      32000," +
            "      33000," +
            "      34000," +
            "      35000," +
            "      36000," +
            "      37000," +
            "      38000," +
            "      39000," +
            "      40000," +
            "      41000," +
            "      42000," +
            "      43000," +
            "      44000," +
            "      45000," +
            "      46000," +
            "      47000," +
            "      48000," +
            "      49000," +
            "      50000" +
            "    ]," +
            "    'subscriptionStatus': 'INCOMPLETE'," +
            "    '_links': [" +
            "      {" +
            "        'name': 'Insurance Calculations'," +
            "        'href': '/subscription/freie_verfuegung/insurance?version=5.0'," +
            "        'method': 'POST'," +
            "        'rel': '_insurancecalculations'" +
            "      }," +
            "      {" +
            "        'name': 'Finalize Subscription'," +
            "        'href': '/subscription/freie_verfuegung/6178796253616d6f6d70505045356c394f4f384562536a6953453667564934672b352f55742b57663836303d/finalizesubscription?version=5.0'," +
            "        'method': 'PUT'," +
            "        'rel': '_finalizesubscription'" +
            "      }" +
            "    ]," +
            "    'debugInfo': {" +
            "      'mDebtFactor': '0.0'," +
            "      'mMinimumRate': '0.0'," +
            "      'customerSpecificDiscount': '0.0'," +
            "      'mBasicDiscount': '2.2250738585072014E-308'," +
            "      'mBasicRate': '2.2250738585072014E-308'," +
            "      'mAgencyDecisionCode': '100.0'," +
            "      'mESDecision': '100.0'," +
            "      'mESDecisionAml': '0'," +
            "      'mESDecisionAmlCode': '100.0'," +
            "      'mDecisionCode': 'PRO'," +
            "      'mExpectedLossOfInstallmentCredit': '1.78'," +
            "      'mMaxDurationForInstallmentLoans': '144.0'," +
            "      'mMaxLineForInstallmentLoanPreNoDocs': '0.0'," +
            "      'mMaxLineForInstallmentLoanPreDocs': '0.0'," +
            "      'mMaxLineForInstallmentLoanNoDocs': '73530.0'," +
            "      'mMaxLineForInstallmentLoanDocs': '0.0'," +
            "      'mBICDMA': '0.0'," +
            "      'mDMA': '2.2250738585072014E-308'," +
            "      'insuranceCommision': '2.2250738585072014E-308'," +
            "      'issueCommision': '2.2250738585072014E-308'," +
            "      'variableCommision': '2.2250738585072014E-308'," +
            "      'customerRunningProducts': []" +
            "    }" +
            "  }" +
            "}");

    static final String FINALIZED_CALCULATION_GREEN_OFFER_RESPONSE = json("{"+
            "  'supportingDocumentsRequired': [],"+
            "  'availableLegitiamtionOptions': ["+
            "    'ONLINE_IDENT',"+
            "    'POST_IDENT',"+
            "    'BRANCH_IDENT'"+
            "  ],"+
            "  'subscriptionStatus': 'APPROVED',"+
            "  'contractIdentifier': '58507465',"+
            "  'financialCalculation': {"+
            "    'creditAmount': 30000,"+
            "    'duration': 84,"+
            "    'monthlyRate': 434.61,"+
            "    'effectiveRate': 5.9,"+
            "    'nominalRate': 5.75,"+
            "    'totalInterestAmount': 6507.24,"+
            "    'totalPayment': 36507.24"+
            "  },"+
            "  'errors': {},"+
            "  'debugInfo': {"+
            "    'mDebtFactor': '0.58',"+
            "    'mMinimumRate': '0.0',"+
            "    'customerSpecificDiscount': '0.0',"+
            "    'mBasicDiscount': '2.2250738585072014E-308',"+
            "    'mBasicRate': '2.2250738585072014E-308',"+
            "    'mAgencyDecisionCode': '100.0',"+
            "    'mESDecision': '2.2250738585072014E-308',"+
            "    'mESDecisionAml': '0',"+
            "    'mESDecisionAmlCode': '100.0',"+
            "    'mDecisionCode': 'null',"+
            "    'mExpectedLossOfInstallmentCredit': '0.0',"+
            "    'mMaxDurationForInstallmentLoans': '144.0',"+
            "    'mMaxLineForInstallmentLoanPreNoDocs': '0.0',"+
            "    'mMaxLineForInstallmentLoanPreDocs': '0.0',"+
            "    'mMaxLineForInstallmentLoanNoDocs': '72734.0',"+
            "    'mMaxLineForInstallmentLoanDocs': '0.0',"+
            "    'mBICDMA': '0.0',"+
            "    'mDMA': '2.2250738585072014E-308',"+
            "    'insuranceCommision': '2.2250738585072014E-308',"+
            "    'issueCommision': '2.2250738585072014E-308',"+
            "    'variableCommision': '2.2250738585072014E-308',"+
            "    'customerRunningProducts': []"+
            "  },"+
            "  'warning': {},"+
            "  '_links': ["+
            "    {"+
            "      'name': 'Online Identification and Qualified Electronic Signature',"+
            "      'href': 'https://gateway.test.idnow.de/api/v1/cfgesign/bridge/create?data=6c3132766a307255544e5a397a594c2f68494c555035786954714c6c6d56755473413431534f6f35476b4b4266464c5a58512f314a4f4f4d6f547931647968672f6466524973526e45592f443439347a31342f724966376136654b7346694f466b68495568797a6a58304e7962726b6a596e30794d7950364854714666342b69734341444c5966696b4361443546755a46382b376f47417a512b662b744b565768307658667445696a4d766a2f68493935714e524b416d6c302b354573774e4177716c5a4945575a5a313253694a3863587737614158644d46323776716e4d434961437a6f506a636c516d2b3059686468664b544c483734514c42704851344e35714c6b7a5363324a73517774547568747846786350456a6c5949334435693643566668355179425546524436324b72684d63754f544654724545446a39793864592f476d376b5961526671736e646d74796e4c324942706938323153554e6c4d744a62577a696445385a6a624b59316d527933734745554e47504a56794766797231567777387136796e774f4c507a4662703866394d5a394c37574e6b6b5630324e34774a4e3433653032544b74567a6a69654743417547782b39665476784c4b4d61324475436b5034372b4247516c713239575074353433376b374b3047474741654974556b385a4b7351544149384d3137663331306653484c506f692f3434624f5278434a436f564278646e7246497964545673367a5464397a536c666f68584c775730464e686570426d6c63326a456d63646e4b4152447733427653504b6b415644424d7175355878696c376e39314b495955516667544150677542384b446272767476414234654d59737a63486c5051347a5945686a61724a3050544f4a6b7134514b2b727a4e616b796d7a67494c5a337758665765795757483658744974704a49675a624c663731616c7361785a337a644a717170696c794c6d5964624c56705631736a4262776f757449585a3575673d3d&signature=0144e75884b2d7a8ca55d1e8dc525dbaeb55cbc75b4d78432dd3b2bfd94953ba',"+
            "      'method': 'GET',"+
            "      'rel': '_onlineIdent'"+
            "    },"+
            "    {"+
            "      'name': 'Cancel Subscription',"+
            "      'href': '/subscription/3770754a744d4e6a7772446b514250383358643056534e4d4674773232476f6f59355a74506f67576233553d?version=5.0',"+
            "      'method': 'DELETE',"+
            "      'rel': '_cancelSubscriptionDocument'"+
            "    },"+
            "    {"+
            "      'name': 'Download Subscription Document',"+
            "      'href': '/subscription/3770754a744d4e6a7772446b514250383358643056534e4d4674773232476f6f59355a74506f67576233553d/documents?version=5.0',"+
            "      'method': 'GET',"+
            "      'rel': '_downloadSubscriptionDocument'"+
            "    },"+
            "    {"+
            "      'name': 'Email Subscription Document',"+
            "      'href': '/subscription/3770754a744d4e6a7772446b514250383358643056534e4d4674773232476f6f59355a74506f67576233553d/documents/email?version=5.0',"+
            "      'method': 'GET',"+
            "      'rel': '_emailSubscriptionDocument'"+
            "    }"+
            "  ],"+
            "  'daarequired': false"+
            "}");

    static final String VALIDATE_SUBSCRIPTION_RESPONSE = json("{" +
            "  'subscriptionidentifier': '744151656e5743456b614730422b7379626a7464356d42736f6f7a38527779327a777a6c6f754d4f7645413d'," +
            "  '_links': [" +
            "    {" +
            "      'name': 'Personalized calculations'," +
            "      'href': '/subscription/freie_verfuegung/744151656e5743456b614730422b7379626a7464356d42736f6f7a38527779327a777a6c6f754d4f7645413d/personalizedcalculations?version=5.0'," +
            "      'method': 'PUT'," +
            "      'rel': '_personalizedcalculations'" +
            "    }," +
            "    {" +
            "      'name': 'update subscriber Data'," +
            "      'href': '/subscription/freie_verfuegung/744151656e5743456b614730422b7379626a7464356d42736f6f7a38527779327a777a6c6f754d4f7645413d?version=5.0'," +
            "      'method': 'PUT'," +
            "      'rel': '_updatesubscription'" +
            "    }," +
            "    {" +
            "      'name': 'Finalize Subscription'," +
            "      'href': '/subscription/freie_verfuegung/744151656e5743456b614730422b7379626a7464356d42736f6f7a38527779327a777a6c6f754d4f7645413d/finalizesubscription?version=5.0'," +
            "      'method': 'PUT'," +
            "      'rel': '_finalizesubscription'" +
            "    }" +
            "  ]," +
            "  'errors': {}," +
            "  'warning': {" +
            "    'city': [" +
            "      'München'" +
            "    ]," +
            "    'street': [" +
            "      'Leopoldstr. 120'" +
            "    ]," +
            "    'zipcode': [" +
            "      '80802'" +
            "    ]" +
            "  }" +
            "}");


}
