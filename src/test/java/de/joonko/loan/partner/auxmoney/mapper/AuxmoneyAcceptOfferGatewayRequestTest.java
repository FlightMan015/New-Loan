//package de.joonko.loan.partner.auxmoney.mapper;
//
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import de.joonko.loan.partner.auxmoney.getoffers.BaseMapperTest;
//import de.joonko.loan.partner.auxmoney.model.AuxmoneyAcceptOfferRequest;
//import de.joonko.loan.util.JsonUtil;
//import io.github.glytching.junit.extension.random.Random;
//import org.json.JSONException;
//import org.junit.Assert;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.skyscreamer.jsonassert.JSONAssert;
//import org.skyscreamer.jsonassert.JSONCompareMode;
//import org.springframework.beans.factory.annotation.Autowired;
//
//class AuxmoneyAcceptOfferGatewayRequestTest extends BaseMapperTest {
//
//    @Random
//    private AcceptOfferRequest acceptOfferRequest;
//
//    @Random
//    private BankData bankDataRequest;
//
//    @Autowired
//    private AuxmoneyAcceptOfferRequestMapper requestMapper;
//
//    @Autowired
//    private AuxmoneyBankDataMapper bankDataMapper;
//
//
//    @Test
//    @DisplayName("Should Convert credit Id")
//    void convertCreditId() {
//        AuxmoneyAcceptOfferRequest auxMoneyAcceptOfferRequest = requestMapper.toAuxmoneyRequest(acceptOfferRequest);
//        Assert.assertEquals(acceptOfferRequest.getCreditId(), auxMoneyAcceptOfferRequest.getCreditId());
//    }
//
//    @Test
//    @DisplayName("Should Convert price Id")
//    void convertPriceId() {
//        AuxmoneyAcceptOfferRequest auxMoneyAcceptOfferRequest = requestMapper.toAuxmoneyRequest(acceptOfferRequest);
//        Assert.assertEquals(acceptOfferRequest.getPriceId(), auxMoneyAcceptOfferRequest.getPriceId());
//    }
//
//    @Test
//    @DisplayName("Should Convert user Id")
//    void convertUserId() {
//        AuxmoneyAcceptOfferRequest auxMoneyAcceptOfferRequest = requestMapper.toAuxmoneyRequest(acceptOfferRequest);
//        Assert.assertEquals(acceptOfferRequest.getUserId(), auxMoneyAcceptOfferRequest.getUserId());
//    }
//
//    @Test
//    @DisplayName("Should Convert BankData ")
//    void shouldConvertBankData() {
//        de.joonko.loan.partner.auxmoney.model.BankData bankData = bankDataMapper.toAuxmoneyBankData(bankDataRequest);
//
//        Assert.assertEquals(bankData.getBic(), bankDataRequest.getBic());
//        Assert.assertEquals(bankData.getIban(), bankDataRequest.getIban());
//    }
//
//
//    @Test
//    @DisplayName("Should Generate Json data with correct Format, No Nested Elements ")
//    void testFormatOfConvertedValues() throws JsonProcessingException, JSONException {
//        String expected = "{\"user_id\":1111,\n" +
//                "   \"credit_id\":2222,\n" +
//                "   \"price_id\":3333,\n" +
//                "   \"bank_data\":{\"iban\":\"DE1111\",\n" +
//                "   \"bic\":\"CODABEFF\"}\n" +
//                "}";
//        AcceptOfferRequest acceptOfferRequest = AcceptOfferRequest.builder().creditId(2222)
//                .userId(1111)
//                .priceId(3333)
//                .bankData(BankData.builder().bic("CODABEFF").iban("DE1111").build())
//                .build();
//
//
//        AuxmoneyAcceptOfferRequest auxMoneyAcceptOfferRequest = requestMapper.toAuxmoneyRequest(acceptOfferRequest);
//
//        String auxmoneyRequestAsJson = JsonUtil.getObjectAsJsonString(auxMoneyAcceptOfferRequest);
//
//        JSONAssert.assertEquals(auxmoneyRequestAsJson, expected, JSONCompareMode.LENIENT);
//    }
//
//
//}
