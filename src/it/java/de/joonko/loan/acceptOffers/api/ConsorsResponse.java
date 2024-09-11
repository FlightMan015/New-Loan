package de.joonko.loan.acceptOffers.api;

import de.joonko.loan.common.JsonResponses;

public class ConsorsResponse extends JsonResponses {

    public static final String TOKEN_RESPONSE = json("{" +
            "  'token': 'Bearer fake-token'" +
            "}");

    public static String OFFER_ACCPETED = json("{" +
            "  'supportingDocumentsRequired': [" +
            "    50035," +
            "    30652," +
            "    50038," +
            "    50033," +
            "    50067" +
            "  ]," +
            "  'availableLegitiamtionOptions': [" +
            "    'ONLINE_IDENT'," +
            "    'POST_IDENT'," +
            "    'BRANCH_IDENT'" +
            "  ]," +
            "  'subscriptionStatus': 'APPROVED'," +
            "  'contractIdentifier': '58498821'," +
            "  'financialCalculation': {" +
            "    'creditAmount': 30000," +
            "    'duration': 84," +
            "    'monthlyRate': 434.61," +
            "    'effectiveRate': 5.9," +
            "    'nominalRate': 5.75," +
            "    'totalInterestAmount': 6507.24," +
            "    'totalPayment': 36507.24" +
            "  }," +
            "  'errors': {}," +
            "  'debugInfo': {" +
            "    'mDebtFactor': '0.6'," +
            "    'mMinimumRate': '0.0'," +
            "    'customerSpecificDiscount': '0.0'," +
            "    'mBasicDiscount': '2.2250738585072014E-308'," +
            "    'mBasicRate': '2.2250738585072014E-308'," +
            "    'mAgencyDecisionCode': '100.0'," +
            "    'mESDecision': '2.2250738585072014E-308'," +
            "    'mESDecisionAml': '0'," +
            "    'mESDecisionAmlCode': '100.0'," +
            "    'mDecisionCode': 'null'," +
            "    'mExpectedLossOfInstallmentCredit': '0.0'," +
            "    'mMaxDurationForInstallmentLoans': '144.0'," +
            "    'mMaxLineForInstallmentLoanPreNoDocs': '0.0'," +
            "    'mMaxLineForInstallmentLoanPreDocs': '0.0'," +
            "    'mMaxLineForInstallmentLoanNoDocs': '72383.0'," +
            "    'mMaxLineForInstallmentLoanDocs': '0.0'," +
            "    'mBICDMA': '0.0'," +
            "    'mDMA': '2.2250738585072014E-308'," +
            "    'insuranceCommision': '2.2250738585072014E-308'," +
            "    'issueCommision': '2.2250738585072014E-308'," +
            "    'variableCommision': '2.2250738585072014E-308'," +
            "    'customerRunningProducts': []" +
            "  }," +
            "  'warning': {}," +
            "  '_links': [" +
            "    {" +
            "      'name': 'Online Identification and Qualified Electronic Signature'," +
            "      'href': 'https://gateway.test.idnow.de/api/v1/cfgesign/bridge/create?data=6c3132766a307255544e5a397a594c2f68494c555035786954714c6c6d56755473413431534f6f35476b4b4266464c5a58512f314a4f4f4d6f547931647968672f6466524973526e45592f443439347a31342f724966376136654b7346694f466b68495568797a6a58304e7962726b6a596e30794d7950364854714666342b69495047623344644a6b326e424c6672415863566663547442306e584542373258507455304935386941693172557a7146303370774b35326362714e7a3776685350743463696f62376179546a4945767172705446516e634f34734d5647436b73734e726f72694e747a625a636c4b72376343457a594c4e7833612f595a78764b424530565243697975624d7061686835644d4b57792f4567452f616174417437684553344631746c627953564b573231514f364f412b4638647a4348633778623533595734484d72414f6a5179527a4b2f5542382f6c7a6b6e6d684c4137383164575a544a2b4a3330766868665878486b2f62396478726b4a474b61693172464653312f31706f33755536376e7a39377166476f3833452b334f7134346f436f355738717365364b543449784f76494c71474c793574313149724b58346242447779573435496e68352f46596a3444305a343656526875496866717461353468785236346e2f44394f6a6a6d464a6c4f62434f5254324b55686f66626839787053557279773231796665614c676335614b614d3970764966667456384d46754432454536464f58566b5137744b38443775682f333041442f476536446e612b2b695356494c7a574b69584a64476f3266633941666f6b2f43756435664964574c48686e536952426674486375496668754930774c47425a76705972616d6d484c764933465946696a6852764d76537a4647736a7459314450506977307771464f3077362b2b2b7648704e667073774e775167587a716f765377452f62496e614167556d6f7a79644c634769756265676c512b527369773d3d&signature=68cc68c26295667ad2d9a3a696afbefa8e117b4cf4ea4917971da70ffbf549bd'," +
            "      'method': 'GET'," +
            "      'rel': '_onlineIdent'" +
            "    }," +
            "    {" +
            "      'name': 'Cancel Subscription'," +
            "      'href': '/subscription/79316c32354d344b5578474d51434c5a2b664e49795974764e3461344965624b6d427a6d502f76545746733d?version=5.0'," +
            "      'method': 'DELETE'," +
            "      'rel': '_cancelSubscriptionDocument'" +
            "    }," +
            "    {" +
            "      'name': 'Download Subscription Document'," +
            "      'href': '/subscription/79316c32354d344b5578474d51434c5a2b664e49795974764e3461344965624b6d427a6d502f76545746733d/documents?version=5.0'," +
            "      'method': 'GET'," +
            "      'rel': '_downloadSubscriptionDocument'" +
            "    }," +
            "    {" +
            "      'name': 'Email Subscription Document'," +
            "      'href': '/subscription/79316c32354d344b5578474d51434c5a2b664e49795974764e3461344965624b6d427a6d502f76545746733d/documents/email?version=5.0'," +
            "      'method': 'GET'," +
            "      'rel': '_emailSubscriptionDocument'" +
            "    }" +
            "  ]," +
            "  'daarequired': false" +
            "}");

}
