package de.joonko.loan.webhooks.postbank;

import de.joonko.loan.contract.ContractStorageService;
import de.joonko.loan.contract.model.DocumentDetails;
import de.joonko.loan.webhooks.postbank.model.CreditResult;
import de.joonko.loan.webhooks.postbank.model.CreditResultWithContracts;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CreditResultMapper {

    @Mapping(target = "receivedTimeStamp", source = ".", qualifiedByName = "toReceivedTimeStamp")
    CreditResult toCreditResult(CreditResultWithContracts creditResultWithContracts);

    @Named("toReceivedTimeStamp")
    default LocalDateTime toReceivedTimeStamp(CreditResultWithContracts creditResultWithContracts) {
        return LocalDateTime.now();
    }
}
