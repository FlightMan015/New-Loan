package de.joonko.loan.offer.domain;

import de.joonko.loan.offer.api.DacTransaction;
import de.joonko.loan.partner.auxmoney.model.TransactionBooked;
import de.joonko.loan.partner.auxmoney.model.TransactionMarkedForPosting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    private String nameOfPartyInvolvedInTransaction;
    private Double amount;
    private String iban;
    private String bic;
    private LocalDate bookingDate;
    private String bookingText;
    private String purpose;
    private String transactionType;
    private TransactionBooked booked;
    private TransactionMarkedForPosting transactionMarkedForPosting;
    private String categoryId;
    private String counterHolder;

    static Transaction fakeValue(DacTransaction dacTransaction) {
        return Transaction.builder()
                .amount(dacTransaction.getAmount())
                .bic(dacTransaction.getBic())
                .booked(Boolean.TRUE.equals(dacTransaction.getIsPreBooked()) ? TransactionBooked.BOOKED : TransactionBooked.NOT_BOOKED)
                .bookingDate(dacTransaction.getBookingDate())
                .bookingText("Booking text") // TODO Hard Code
                .iban(dacTransaction.getIban())
                .categoryId(dacTransaction.getCategoryId())
                .counterHolder(dacTransaction.getCounterHolder())
                .nameOfPartyInvolvedInTransaction("John Doe") // TODO Hard Code
                .purpose("CASH CashMgmtTransakt Kredit") // TODO Hard Code
                .transactionMarkedForPosting(TransactionMarkedForPosting.ONE) // TODO Hard Code
                .transactionType("transfer") // TODO Hard Code
                .build();
    }
}
