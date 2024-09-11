package de.joonko.loan.partner.postbank.testData;

public class PostbankResponses {

    public static String get200ForLoanDemand(String applicationId) {
        return "<?xml version='1.0' encoding='UTF-8'?>\n" +
                "<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "    <S:Body>\n" +
                "        <serviceKreditantragResponse xmlns=\"http://privatkredit.postbank.de/types\">\n" +
                "            <return>\n" +
                "                <status>\n" +
                "                    <state>1</state>\n" +
                "                    <messages>\n" +
                "                        <message>Auszahlungsdatum wurde korrigiert!</message>\n" +
                "                        <message>dac-info: Verarbeitung der DAC-Dateien abgeschlossen.</message>\n" +
                "                    </messages>\n" +
                "                </status>\n" +
                "                <vertragsnr>5743578</vertragsnr>\n" +
                "                <vertragsid>" + applicationId + "</vertragsid>\n" +
                "            </return>\n" +
                "        </serviceKreditantragResponse>\n" +
                "    </S:Body>\n" +
                "</S:Envelope>";
    }

    public static String get200WithDacError(String applicationId) {
        return "<?xml version='1.0' encoding='UTF-8'?>\n" +
                "<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "    <S:Body>\n" +
                "        <serviceKreditantragResponse xmlns=\"http://privatkredit.postbank.de/types\">\n" +
                "            <return>\n" +
                "                <status>\n" +
                "                    <state>1</state>\n" +
                "                    <messages>\n" +
                "                        <message>Auszahlungsdatum wurde korrigiert!</message>\n" +
                "                        <message>dac-error: technischer Fehler beim Verarbeiten der DAC-Dateien!</message>\n" +
                "                    </messages>\n" +
                "                </status>\n" +
                "                <vertragsnr>5745094</vertragsnr>\n" +
                "                <vertragsid>" + applicationId + "</vertragsid>\n" +
                "            </return>\n" +
                "        </serviceKreditantragResponse>\n" +
                "    </S:Body>\n" +
                "</S:Envelope>";
    }

    public static String get200WithInvalidIban(String applicationId) {
        return "<?xml version='1.0' encoding='UTF-8'?>\n" +
                "<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "    <S:Body>\n" +
                "        <serviceKreditantragResponse xmlns=\"http://privatkredit.postbank.de/types\">\n" +
                "            <return>\n" +
                "                <status>\n" +
                "                    <state>0</state>\n" +
                "                    <error>1</error>\n" +
                "                    <messages>\n" +
                "                        <message>konto.kontotyp=[lastschrift] : Die IBAN=[DE62888888880012345678] ist nicht valide.\n" +
                "                        </message>\n" +
                "                    </messages>\n" +
                "                </status>\n" +
                "                <vertragsid>" + applicationId + "</vertragsid>\n" +
                "            </return>\n" +
                "        </serviceKreditantragResponse>\n" +
                "    </S:Body>\n" +
                "</S:Envelope>";
    }

    public static String get400ForLoanDemand() {
        return "<?xml version='1.0' encoding='UTF-8'?>\n" +
                "<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "    <S:Body>\n" +
                "        <ns0:Fault xmlns:ns0=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns1=\"http://www.w3.org/2003/05/soap-envelope\">\n" +
                "            <faultcode>ns0:Server</faultcode>\n" +
                "            <faultstring>org.xml.sax.SAXParseException; cvc-enumeration-valid: Wert 'zzz' ist nicht Facet-gültig in Bezug auf Enumeration '[modell, angebot, antrag, vertrag, status]'. Er muss ein Wert aus der Enumeration sein.</faultstring>\n" +
                "        </ns0:Fault>\n" +
                "    </S:Body>\n" +
                "</S:Envelope>";
    }
}
