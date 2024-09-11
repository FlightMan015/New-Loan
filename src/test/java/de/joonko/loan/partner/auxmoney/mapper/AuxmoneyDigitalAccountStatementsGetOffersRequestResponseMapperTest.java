package de.joonko.loan.partner.auxmoney.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.joonko.loan.offer.domain.BankAccountCategory;
import de.joonko.loan.offer.domain.ClassificationProvider;
import de.joonko.loan.offer.domain.Currency;
import de.joonko.loan.offer.domain.DigitalAccountStatements;
import de.joonko.loan.partner.auxmoney.getoffers.BaseMapperTest;
import de.joonko.loan.util.JsonUtil;
import io.github.glytching.junit.extension.random.Random;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;


class AuxmoneyDigitalAccountStatementsGetOffersRequestResponseMapperTest extends BaseMapperTest {

    @Random
    private DigitalAccountStatements digitalAccountStatements;

    @Autowired
    AuxmoneyDigitalAccountStatementsMapper mapper;


    @Test
    void toAuxmoneyDigitalAccountStatements() {
        de.joonko.loan.partner.auxmoney.model.DigitalAccountStatements auxmoneyDigitalAccountStatements = mapper.toAuxmoneyDigitalAccountStatements(this.digitalAccountStatements);
        Assert.assertEquals(digitalAccountStatements.getBankAccountName(), auxmoneyDigitalAccountStatements.getName());
        Assert.assertEquals(digitalAccountStatements.getOwner(), auxmoneyDigitalAccountStatements.getOwner());
        Assert.assertEquals(digitalAccountStatements.getDacSource(), auxmoneyDigitalAccountStatements.getDacSource());
        Assert.assertEquals(digitalAccountStatements.getCategory().getValue(), auxmoneyDigitalAccountStatements.getCategory().getValue());
        Assert.assertEquals(digitalAccountStatements.getIban(), auxmoneyDigitalAccountStatements.getIban());
        Assert.assertEquals(digitalAccountStatements.getBic(), auxmoneyDigitalAccountStatements.getBic());
        Assert.assertEquals(digitalAccountStatements.getBankAccountType(), auxmoneyDigitalAccountStatements.getType());
        Assert.assertEquals(digitalAccountStatements.getBalance(), auxmoneyDigitalAccountStatements.getBalance());
        //Assert.assertEquals(digitalAccountStatements.getDispoLimit(),auxmoneyDigitalAccountStatements.getDispoLimit());
        Assert.assertEquals(digitalAccountStatements.getCurrency(), auxmoneyDigitalAccountStatements.getCurrency());
    }

    @Test
    @DisplayName("should convert to correct json format ")
    void testGeneratedJson() throws JsonProcessingException, JSONException {
        String expectedJson = "{ \n" +
                "   \"name\":\"Personal Loan Account\",\n" +
                "   \"owner\":\"Nicolai Nussbeck\",\n" +
                "   \"dac_source\":\"FINLEAP_CONNECT\",\n" +
                "   \"category\":\"private\",\n" +
                "   \"iban\":\"DE12347588880012345678\",\n" +
                "   \"bic\":\"MALADE23III\",\n" +
                "   \"type\":\"loan account\",\n" +
                "   \"balance\":200.03,\n" +
                "   \"dispo_limit\":-1000,\n" +
                "   \"balance_date\":\"2018-06-02\",\n" +
                "   \"currency\":\"EUR\"\n" +
                "}";
        DigitalAccountStatements build = DigitalAccountStatements.builder()
                .bankAccountName("Personal Loan Account")
                .owner("Nicolai Nussbeck")
                .dacSource(ClassificationProvider.FINLEAP_CONNECT)
                .category(BankAccountCategory.PRIVATE)
                .iban("DE12347588880012345678")
                .bic("MALADE23III")
                .bankAccountType("loan account")
                .balance(200.03)
                .balanceDate(LocalDate.of(2018, 06, 02))
                .currency(Currency.EUR)
                .build();
        de.joonko.loan.partner.auxmoney.model.DigitalAccountStatements auxmoneyDigitalAccountStatements = mapper.toAuxmoneyDigitalAccountStatements(build);


        String auxmoneyRequestAsJson = JsonUtil.getObjectAsJsonString(auxmoneyDigitalAccountStatements);
        JSONAssert.assertEquals(auxmoneyRequestAsJson, expectedJson, JSONCompareMode.LENIENT);

    }
}
