package de.joonko.loan.partner.postbank;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import de.joonko.loan.partner.postbank.model.request.LoanDemandPostbankRequest;
import de.joonko.loan.partner.postbank.model.request.LoanDemandPostbankRequestContract;
import de.joonko.loan.partner.postbank.model.request.LoanDemandPostbankRequestCredit;
import de.joonko.loan.partner.postbank.model.request.LoanDemandPostbankRequestSoapBody;
import de.joonko.loan.partner.postbank.model.request.LoanDemandPostbankRequestSoapEnvelope;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class PostbankModelSerializerTest {

    @Test
    void whenJavaSerializedToXmlStr_thenCorrect() throws JsonProcessingException {
        final var envelope = LoanDemandPostbankRequestSoapEnvelope.builder()
                .schema("http://schemas.xmlsoap.org/soap/envelope/")
                .types("http://privatkredit.postbank.de/types")
                .body(LoanDemandPostbankRequestSoapBody.builder()
                        .contract(LoanDemandPostbankRequestContract.builder()
                                .credit(LoanDemandPostbankRequestCredit.builder()
                                        .companyId("bonify")
                                        .password("zzz")
                                        .version("1.0")
                                        .request(LoanDemandPostbankRequest.builder()
                                                .type("antrag")
                                                .build())
                                        .build())
                                .build())
                        .build())
                .build();
        XmlMapper xmlMapper = new XmlMapper();
        String xml = xmlMapper.writeValueAsString(envelope);
        assertNotNull(xml);
    }
}
