package de.joonko.loan.offer.api;

import de.joonko.loan.common.JsonResponses;

public class SantanderResponses  {

    static final String SINGLE_OFFER_RESPONSE = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
            "   <soapenv:Body>\n" +
            "      <GetKreditvertragsangebotResponse xmlns=\"http://ws.bco.caps.scb.de\">\n" +
            "         <antragsstatus xmlns=\"\">\n" +
            "            <scbAntragId>13237702</scbAntragId>\n" +
            "            <status>GENEHMIGT</status>\n" +
            "            <kreditvertrag>\n" +
            "               <beschreibung>Kreditvertrag</beschreibung>\n" +
            "               <dokument></dokument>\n" +
            "            </kreditvertrag>\n" +
            "            <finanzierung>\n" +
            "               <abloesebetrag>0.0</abloesebetrag>\n" +
            "               <kreditbetragNetto>1000.00</kreditbetragNetto>\n" +
            "               <laufzeitInMonaten>12</laufzeitInMonaten>\n" +
            "               <ersteRateDatum>2020-11-01</ersteRateDatum>\n" +
            "               <auszahlungDatum>2020-09-17</auszahlungDatum>\n" +
            "               <wunschrate>83.33</wunschrate>\n" +
            "               <ratenbetragMonatl>83.33</ratenbetragMonatl>\n" +
            "               <rsv>OHNE_RSV</rsv>\n" +
            "               <rsv2Dn>false</rsv2Dn>\n" +
            "               <rsvBetrag>0.00</rsvBetrag>\n" +
            "               <effektivzinsPaProz>0.000</effektivzinsPaProz>\n" +
            "               <nominalzinsPaProz>0.000</nominalzinsPaProz>\n" +
            "               <zinsenGesamt>0.00</zinsenGesamt>\n" +
            "               <zinsenGesamtProz>0.000</zinsenGesamtProz>\n" +
            "               <bearbeitGeb>0.00</bearbeitGeb>\n" +
            "               <bearbeitGebProz>0.000</bearbeitGebProz>\n" +
            "               <kreditbetragGesamt>999.96</kreditbetragGesamt>\n" +
            "            </finanzierung>\n" +
            "         </antragsstatus>\n" +
            "         <parameter xmlns=\"\">\n" +
            "            <key>dn1.islegitimiert</key>\n" +
            "            <value>false</value>\n" +
            "         </parameter>\n" +
            "         <parameter xmlns=\"\">\n" +
            "            <key>dn1.iskunde</key>\n" +
            "            <value>false</value>\n" +
            "         </parameter>\n" +
            "      </GetKreditvertragsangebotResponse>\n" +
            "   </soapenv:Body>\n" +
            "</soapenv:Envelope>";


    static final String UPLOAD_DOCUMENT_RESPONSE = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
            "   <soapenv:Body>\n" +
            "      <SetDocumentResponse xmlns=\"http://ws.docs.caps.scb.de\">\n" +
            "         <success xmlns=\"\">true</success>\n" +
            "      </SetDocumentResponse>\n" +
            "   </soapenv:Body>\n" +
            "</soapenv:Envelope>";

}
