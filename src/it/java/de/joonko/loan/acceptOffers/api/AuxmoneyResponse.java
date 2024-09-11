package de.joonko.loan.acceptOffers.api;

import de.joonko.loan.common.JsonResponses;

public class AuxmoneyResponse extends JsonResponses {

    final static String OFFER_APPROVED = json("{" +
            "   'violations': [ ]," +
            "   'user_id': 3679055," +
            "   'credit_id': 15171199," +
            "   'is_success': true," +
            "   'is_error': false," +
            "   'duration': 48," +
            "   'loan': 56600," +
            "   'loan_asked': 47400," +
            "   'rate': 5.5," +
            "   'eff_rate': 6.53," +
            "   'insurance_fee': 8236.89," +
            "   'interest': 6.5259," +
            "   'installment_amount': 1319.8," +
            "   'manual_quality_assurance': false," +
            "   'ekf_url': 'https://acc.auxacc.de/firstContact/instant/15171199/d55b5cbbbf7706f837ffc867c03c58dc'," +
            "   'external_id': 'Ext_1'," +
            "   'skip_ekf': false," +
            "   'idd_rkv_presale_url': 'https://acc.auxacc.de/contact/dokumente/borrower/SorglosPaket_VorvertraglicheInformationen.pdf'," +
            "   'rkv': 3" +
            "}");
}
