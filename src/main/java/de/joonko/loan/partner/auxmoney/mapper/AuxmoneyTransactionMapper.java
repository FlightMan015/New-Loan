package de.joonko.loan.partner.auxmoney.mapper;


import de.joonko.loan.offer.domain.Transaction;
import de.joonko.loan.partner.auxmoney.AuxmoneyDefaults;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
interface AuxmoneyTransactionMapper {

    List<de.joonko.loan.partner.auxmoney.model.Transaction> dtoTransactionsToTransactions(List<Transaction> transactions);

    @Mapping(source = "nameOfPartyInvolvedInTransaction", target = "name")
    @Mapping(source = "amount", target = "amount")
    @Mapping(source = "iban", target = "iban")
    @Mapping(source = "bic", target = "bic")
    @Mapping(target = "bankName", ignore = true)
    @Mapping(target = "currency", constant = AuxmoneyDefaults.CURRENCY)
    @Mapping(source = "bookingDate", target = "bookingDate")
    @Mapping(target = "classificationProvider", ignore = true)
    @Mapping(target = "classificationOwn", ignore = true)
    @Mapping(target = "classificationBank", ignore = true)
    @Mapping(source = "bookingText", target = "bookingText")
    @Mapping(target = "saldo", ignore = true)
    @Mapping(source = "purpose", target = "purpose")
    @Mapping(source = "transactionType", target = "type")
    @Mapping(source = "booked", target = "booked")
    @Mapping(source = "transactionMarkedForPosting", target = "visited")
    de.joonko.loan.partner.auxmoney.model.Transaction dtoTransactionToTransaction(Transaction transaction);
}
