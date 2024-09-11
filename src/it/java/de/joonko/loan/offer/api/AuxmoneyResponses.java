package de.joonko.loan.offer.api;

import de.joonko.loan.common.JsonResponses;

public class AuxmoneyResponses extends JsonResponses {

    final static String SINGLE_OFFER = json("{" +
            "  'credit_id': 1," +
            "  'user_id': 2," +
            "  'is_error': false," +
            "  'is_success': true," +
            "  'offers': [" +
            "    {" +
            "      'amount': 1500," +
            "      'rkv': 1," +
            "      'prices': [" +
            "        {" +
            "          'duration': 24," +
            "          'loan': 1500," +
            "          'total_credit_amount': 2000," +
            "          'rate': 1.23," +
            "          'interest': 2.34," +
            "          'installment_amount': 345.67," +
            "          'commission_amount': 4.56," +
            "          'price_id': 1," +
            "          'loan_asked': 1500," +
            "          'eff_rate': 7.89," +
            "          'interest_amount': 9.1011" +
            "        }" +
            "      ]" +
            "    }" +
            "  ]" +
            "}");


    final static String SINGLE_OFFER_CALL = json("{\n" +
            "  \"violations\": [],\n" +
            "  \"user_id\": 123456789,\n" +
            "  \"credit_id\": 987654321,\n" +
            "  \"is_success\": true,\n" +
            "  \"is_error\": false,\n" +
            "  \"contract\": \"iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg==\",\n" +
            "  \"duration\": 48,\n" +
            "  \"loan\": 56600,\n" +
            "  \"loan_asked\": 1500,\n" +
            "  \"rate\": 5.5,\n" +
            "  \"eff_rate\": 6.53,\n" +
            "  \"insurance_fee\": 8236.89,\n" +
            "  \"interest\": 6.5259,\n" +
            "  \"total_credit_amount\": 6.5259,\n" +
            "  \"installment_amount\": 1319.8,\n" +
            "  \"manual_quality_assurance\": false,\n" +
            "  \"ekf_url\": \"https://acc.auxacc.de/firstContact/instant/987654321/d55b5cbbbf7706f837ffc867c03c58dc\",\n" +
            "  \"external_id\": \"Ext_1\",\n" +
            "  \"skip_ekf\": false,\n" +
            "  \"idd_rkv_presale_url\": \"https://acc.auxacc.de/contact/dokumente/borrower/SorglosPaket_VorvertraglicheInformationen.pdf\",\n" +
            "  \"rkv\": 3\n" +
            "}");

}
