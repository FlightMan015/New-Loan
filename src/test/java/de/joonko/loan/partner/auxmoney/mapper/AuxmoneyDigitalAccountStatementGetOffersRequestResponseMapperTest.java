package de.joonko.loan.partner.auxmoney.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.joonko.loan.offer.domain.*;
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
import java.util.List;


class AuxmoneyDigitalAccountStatementGetOffersRequestResponseMapperTest extends BaseMapperTest {

    @Random
    private DigitalAccountStatements digitalAccountStatement;

    @Autowired
    private AuxmoneyDigitalAccountStatementsMapper mapper;

    @Test
    void toAuxmoneyDigitalAccountStatements() {
        de.joonko.loan.partner.auxmoney.model.DigitalAccountStatements auxmoneyDigitalAccountStatements = mapper.toAuxmoneyDigitalAccountStatements(this.digitalAccountStatement);
        Assert.assertEquals(digitalAccountStatement.getBankAccountName(), auxmoneyDigitalAccountStatements.getName());
        Assert.assertEquals(digitalAccountStatement.getOwner(), auxmoneyDigitalAccountStatements.getOwner());
        Assert.assertEquals(digitalAccountStatement.getDacSource(), auxmoneyDigitalAccountStatements.getDacSource());
        Assert.assertEquals(digitalAccountStatement.getCategory().getValue(), auxmoneyDigitalAccountStatements.getCategory().getValue());
        Assert.assertEquals(digitalAccountStatement.getIban(), auxmoneyDigitalAccountStatements.getIban());
        Assert.assertEquals(digitalAccountStatement.getBic(), auxmoneyDigitalAccountStatements.getBic());
        Assert.assertEquals(digitalAccountStatement.getBankAccountType(), auxmoneyDigitalAccountStatements.getType());
        Assert.assertEquals(digitalAccountStatement.getBalance(), auxmoneyDigitalAccountStatements.getBalance());
        Assert.assertEquals(digitalAccountStatement.getCurrency(), auxmoneyDigitalAccountStatements.getCurrency());
    }

    @Test
    void mapTransactions() {
        de.joonko.loan.partner.auxmoney.model.DigitalAccountStatements auxmoneyDigitalAccountStatements = mapper.toAuxmoneyDigitalAccountStatements(this.digitalAccountStatement);

        Assert.assertEquals(digitalAccountStatement.getTransactions().size(), auxmoneyDigitalAccountStatements.getTransactions().size());
    }

    @Test
    @DisplayName("should convert to correct json format ")
    void testGeneratedJson() throws JsonProcessingException, JSONException {
        String expectedJson = "{\n" +
                "\"name\": \"Personal Loan Account\",\n" +
                "\"owner\": \"Nicolai Nussbeck\",\n" +
                "\"dac_source\": \"FINLEAP_CONNECT\",\n" +
                "\"category\": \"private\",\n" +
                "\"iban\": \"DE12347588880012345678\",\n" +
                "\"bic\": \"MALADE23III\",\n" +
                "\"type\": \"Loan account\",\n" +
                "\"balance\": 200.03,\n" +
                "\"dispo_limit\": -1000,\n" +
                "\"balance_date\": \"2018-06-03\",\n" +
                "\"currency\": \"EUR\",\n" +
                "\"transactions\": [\n" +
                "{\n" +
                "\"name\": \"Alexandra Harter\",\n" +
                "\"amount\": -74.86,\n" +
                "\"iban\": \"DE62888888880012345678\",\n" +
                "\"bic\": \"TESTDE88XXX\",\n" +
                "\"bank_name\": \"Testbank\",\n" +
                "\"currency\": \"EUR\",\n" +
                "\"booking_date\": \"2018-06-03\",\n" +
                "\"purpose\": \"CASH CashMgmtTransakt Kredit\",\n" +
                "\"type\": \"\",\n" +
                "\"booked\": 1,\n" +
                "\"visited\": 1,\n" +
                "\"classification_bank\": \"classification by bank\",\n" +
                "\"classification_own\": \"classification by ourself\",\n" +
                "\"classification_provider\": \"classification by provider\",\n" +
                "\"booking_text\": \"this is the booking text\"\n" +
                "},\n" +
                "{\n" +
                "\"name\": \"Giuliano Gross\",\n" +
                "\"amount\": -74.86,\n" +
                "\"iban\": \"DE62888888880012345678\",\n" +
                "\"bic\": \"TESTDE88XXX\",\n" +
                "\"bank_name\": \"Testbank\",\n" +
                "\"currency\": \"EUR\",\n" +
                "\"booking_date\": \"2018-06-03\",\n" +
                "\"purpose\": \"CASH CashMgmtTransakt Kredit\",\n" +
                "\"type\": \"\",\n" +
                "\"booked\": 1,\n" +
                "\"visited\": 1,\n" +
                "\"classification_bank\": \"classification by bank\",\n" +
                "\"classification_own\": \"classification by ourself\",\n" +
                "\"classification_provider\": \"classification by provider\",\n" +
                "\"booking_text\": \"this is the booking text\"\n" +
                "}\n" +
                "]\n" +
                "}";

        Transaction transaction1 = Transaction.builder()
                .nameOfPartyInvolvedInTransaction("Alexandra Harter")
                .amount(-74.86d)
                .iban("DE62888888880012345678")
                .bic("TESTDE88XXX")
                .bookingDate(LocalDate.of(2018, 06, 03))
                .purpose("CASH CashMgmtTransakt Kredit")
                .transactionType("")
                .booked(de.joonko.loan.partner.auxmoney.model.TransactionBooked.BOOKED)
                .transactionMarkedForPosting(de.joonko.loan.partner.auxmoney.model.TransactionMarkedForPosting.ONE)
                .bookingText("this is the booking text")
                .build();

        Transaction transaction2 = Transaction.builder()
                .nameOfPartyInvolvedInTransaction("Giuliano Gross")
                .amount(-74.86d)
                .iban("DE62888888880012345678")
                .bic("TESTDE88XXX")
                .bookingDate(LocalDate.of(2018, 06, 03))
                .purpose("CASH CashMgmtTransakt Kredit")
                .transactionType("")
                .booked(de.joonko.loan.partner.auxmoney.model.TransactionBooked.BOOKED)
                .transactionMarkedForPosting(de.joonko.loan.partner.auxmoney.model.TransactionMarkedForPosting.ONE)
                .bookingText("this is the booking text")
                .build();

        List<Transaction> transactions = List.of(transaction1, transaction2);

        DigitalAccountStatements build = DigitalAccountStatements.builder()
                .bankAccountName("Personal Loan Account")
                .owner("Nicolai Nussbeck")
                .dacSource(ClassificationProvider.FINLEAP_CONNECT)
                .category(BankAccountCategory.PRIVATE)
                .iban("DE12347588880012345678")
                .bic("MALADE23III")
                .bankAccountType("Loan account")
                .balance(200.03)
                .balanceDate(LocalDate.of(2018, 06, 03))
                .currency(Currency.EUR)
                .transactions(transactions)
                .build();
        de.joonko.loan.partner.auxmoney.model.DigitalAccountStatements auxmoneyDigitalAccountStatements = mapper.toAuxmoneyDigitalAccountStatements(build);

        String auxmoneyRequestAsJson = JsonUtil.getObjectAsJsonString(auxmoneyDigitalAccountStatements);
        JSONAssert.assertEquals(auxmoneyRequestAsJson, expectedJson, JSONCompareMode.LENIENT);

    }
}
