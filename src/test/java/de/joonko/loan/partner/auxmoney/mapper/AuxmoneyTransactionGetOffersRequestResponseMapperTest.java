package de.joonko.loan.partner.auxmoney.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.joonko.loan.offer.domain.Transaction;
import de.joonko.loan.partner.auxmoney.getoffers.BaseMapperTest;
import de.joonko.loan.partner.auxmoney.model.TransactionBooked;
import de.joonko.loan.partner.auxmoney.model.TransactionMarkedForPosting;
import de.joonko.loan.util.JsonUtil;
import io.github.glytching.junit.extension.random.Random;
import org.json.JSONException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AuxmoneyTransactionGetOffersRequestResponseMapperTest extends BaseMapperTest {

    @Random
    private Transaction transaction;

    @Autowired
    private AuxmoneyTransactionMapper mapper;

    @Test
    @DisplayName("maps name of party involved")
    void mapPartyInvolved() {
        de.joonko.loan.partner.auxmoney.model.Transaction transaction = mapper.dtoTransactionToTransaction(this.transaction);
        assertEquals(this.transaction.getNameOfPartyInvolvedInTransaction(), transaction.getName());
    }

    @Test
    @DisplayName("maps amount")
    void mapAmount() {
        de.joonko.loan.partner.auxmoney.model.Transaction transaction = mapper.dtoTransactionToTransaction(this.transaction);
        assertEquals(this.transaction.getAmount(), transaction.getAmount());
    }

    @Test
    @DisplayName("maps iban")
    void mapIban() {
        de.joonko.loan.partner.auxmoney.model.Transaction transaction = mapper.dtoTransactionToTransaction(this.transaction);
        assertEquals(this.transaction.getIban(), transaction.getIban());
    }

    @Test
    @DisplayName("maps bic")
    void mapBic() {
        de.joonko.loan.partner.auxmoney.model.Transaction transaction = mapper.dtoTransactionToTransaction(this.transaction);
        assertEquals(this.transaction.getBic(), transaction.getBic());
    }

    @Test
    @DisplayName("maps booking date to string")
    void mapBookingDate() {
        de.joonko.loan.partner.auxmoney.model.Transaction transaction = mapper.dtoTransactionToTransaction(this.transaction);
        assertEquals(this.transaction.getBookingDate(), transaction.getBookingDate());
    }

    @Test
    @DisplayName("maps booking text")
    void mapBookingText() {
        de.joonko.loan.partner.auxmoney.model.Transaction transaction = mapper.dtoTransactionToTransaction(this.transaction);
        assertEquals(this.transaction.getBookingText(), transaction.getBookingText());
    }

    @Test
    @DisplayName("maps purpose")
    void mapPurpose() {
        de.joonko.loan.partner.auxmoney.model.Transaction transaction = mapper.dtoTransactionToTransaction(this.transaction);
        assertEquals(this.transaction.getPurpose(), transaction.getPurpose());
    }

    @Test
    @DisplayName("maps transaction type")
    void mapTransactionType() {
        de.joonko.loan.partner.auxmoney.model.Transaction transaction = mapper.dtoTransactionToTransaction(this.transaction);
        assertEquals(this.transaction.getTransactionType(), transaction.getType());
    }

    @Test
    @DisplayName("maps transaction booked")
    void mapTransactionBooked() {
        de.joonko.loan.partner.auxmoney.model.Transaction transaction = mapper.dtoTransactionToTransaction(this.transaction);
        assertEquals(this.transaction.getBooked(), transaction.getBooked());
    }

    @Test
    @DisplayName("maps transaction marked for posting")
    void mapTransactionMarkedForPosting() {
        de.joonko.loan.partner.auxmoney.model.Transaction transaction = mapper.dtoTransactionToTransaction(this.transaction);
        assertEquals(this.transaction.getTransactionMarkedForPosting(), transaction.getVisited());
    }


    @Test
    @DisplayName("should serialize transaction object")
    void serializeTransaction() throws JSONException, JsonProcessingException {

        // Given
        String expected = "{\n" +
                "\"name\": \"Alexandra Harter\",\n" +
                "\"amount\": -74.86,\n" +
                "\"iban\": \"DE62888888880012345678\",\n" +
                "\"bic\": \"TESTDE88XXX\",\n" +
                "\"currency\": \"EUR\",\n" +
                "\"booking_date\": \"2018-06-03\",\n" +
                "\"purpose\": \"CASH CashMgmtTransakt Kredit\",\n" +
                "\"type\": \"\",\n" +
                "\"booked\": 1,\n" +
                "\"visited\": 1,\n" +
                "\"booking_text\": \"this is the booking text\"\n" +
                "}";
        Transaction transactionDto = de.joonko.loan.offer.domain.Transaction.builder()
                .nameOfPartyInvolvedInTransaction("Alexandra Harter")
                .amount(-74.86d)
                .iban("DE62888888880012345678")
                .bic("TESTDE88XXX")
                .bookingDate(LocalDate.of(2018, 06, 03))
                .purpose("CASH CashMgmtTransakt Kredit")
                .transactionType("")
                .booked(TransactionBooked.BOOKED)
                .transactionMarkedForPosting(TransactionMarkedForPosting.ONE)
                .bookingText("this is the booking text")
                .build();

        // When
        de.joonko.loan.partner.auxmoney.model.Transaction transaction = mapper.dtoTransactionToTransaction(transactionDto);
        String objectAsJsonString = JsonUtil.getObjectAsJsonString(transaction);
        JSONAssert.assertEquals(expected, objectAsJsonString, JSONCompareMode.LENIENT);
    }


}
