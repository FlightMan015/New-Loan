package de.joonko.loan.webhooks.postbank;

public class PostbankWebhookFixtures {

    public String getCreditResultOfferWebhook() {
        return "<?xml version='1.0' encoding='UTF-8'?>\n" +
                "<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "    <S:Header>\n" +
                "        <WorkContext xmlns=\"http://oracle.com/weblogic/soap/workarea/\">rO0Ax8AAAAAgAAAAAAAAA=</WorkContext>\n" +
                "    </S:Header>\n" +
                "    <S:Body>\n" +
                "        <ns0:update xmlns:ns0=\"http://privatkredit.postbank.de/creditResult/types/v2\">\n" +
                "            <arg0>\n" +
                "                <creditResultAuthentication>\n" +
                "                    <login>pb_test</login>\n" +
                "                    <password>pb_pass</password>\n" +
                "                </creditResultAuthentication>\n" +
                "                <creditResult>\n" +
                "                    <contract>VGhpcyBpcyB0aGUgbWFpbiBjb250cmFjdA==</contract>\n" +
                "                    <contractShort>VGhpcyBpcyB0aGUgc2hvcnQgY29udHJhY3Q=</contractShort>\n" +
                "                    <contractNumber>5716082</contractNumber>\n" +
                "                    <contractState>ONLINE_GENEHMIGT_24</contractState>\n" +
                "                    <dateOfFirstRate>2021-11-15T00:00:00.000+01:00</dateOfFirstRate>\n" +
                "                    <dateOfLastRate>2023-10-15T00:00:00.000+02:00</dateOfLastRate>\n" +
                "                    <decisionText>Fraud-Verdacht!</decisionText>\n" +
                "                    <duration>24</duration>\n" +
                "                    <effectiveInterest>3.08</effectiveInterest>\n" +
                "                    <freeIncome>1928.0</freeIncome>\n" +
                "                    <interestRate>339.47</interestRate>\n" +
                "                    <schufaInformations>\n" +
                "                        <debtor>1</debtor>\n" +
                "                        <riskCategory>1</riskCategory>\n" +
                "                    </schufaInformations>\n" +
                "                    <lastRate>457.42</lastRate>\n" +
                "                    <loanAmount>10000</loanAmount>\n" +
                "                    <loanAmountTotal>11014</loanAmountTotal>\n" +
                "                    <monthlyRate>459.0</monthlyRate>\n" +
                "                    <nominalInterest>3.024</nominalInterest>\n" +
                "                    <partnerContractNumber>6206867949933e389f2deb9d</partnerContractNumber>\n" +
                "                    <residualDebtAmount>674.95</residualDebtAmount>\n" +
                "                    <serviceFee>0.0</serviceFee>\n" +
                "                    <alternativeOffer>false</alternativeOffer>\n" +
                "                    <insurance>ALO</insurance>\n" +
                "                    <score>361</score>\n" +
                "                    <rapClass>B+</rapClass>\n" +
                "                    <debtorInformation>\n" +
                "                        <debtor>1</debtor>\n" +
                "                        <knownDebtor>true</knownDebtor>\n" +
                "                        <digitaleSignaturUrl>https://test.webid-solutions.de/service/index/ti/28520359491263745101x95246427/cn/000350/act/sig/versionpb/237589181</digitaleSignaturUrl>\n" +
                "                    </debtorInformation>\n" +
                "                </creditResult>\n" +
                "            </arg0>\n" +
                "    </ns0:update>\n" +
                "</S:Body>\n" +
                "</S:Envelope>";
    }

    public String getCreditDocumentsReceivedWebhook() {
        return "<?xml version='1.0' encoding='UTF-8'?>\n" +
                "<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "    <S:Header>\n" +
                "        <WorkContext xmlns=\"http://oracle.com/weblogic/soap/workarea/\">rO0Ax8AAAAAgAAAAAAAAA=</WorkContext>\n" +
                "    </S:Header>\n" +
                "    <S:Body>\n" +
                "        <ns0:update xmlns:ns0=\"http://privatkredit.postbank.de/creditResult/types/v2\">\n" +
                "            <arg0>\n" +
                "      <creditResultAuthentication>\n" +
                "         <login>login</login>\n" +
                "         <password>password</password>\n" +
                "      </creditResultAuthentication>\n" +
                "      <creditResult>\n" +
                "         <contractNumber>5716082</contractNumber>\n" +
                "         <contractState>UNTERLAGEN_EINGEGANGEN_25</contractState>\n" +
                "         <partnerContractNumber>6206867949933e389f2deb9d</partnerContractNumber>\n" +
                "         <alternativeOffer>false</alternativeOffer>\n" +
                "      </creditResult>\n" +

                "            </arg0>\n" +
                "    </ns0:update>\n" +
                "</S:Body>\n" +
                "</S:Envelope>";
    }

}
