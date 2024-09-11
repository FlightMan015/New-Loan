package de.joonko.loan.partner.solaris.mapper;

import de.joonko.loan.acceptoffer.domain.LoanApplicationStatus;
import de.joonko.loan.partner.solaris.model.LoanStatus;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ValueMapping;
import org.mapstruct.ValueMappings;

@Mapper(componentModel = "spring")
public interface SolarisLoanStatusMapper {

    @ValueMappings({
            @ValueMapping(target = "ESIGN_PENDING", source = "ESIGN_PENDING"),
            @ValueMapping(target = "ESIGN_PENDING", source = "pending"),
            @ValueMapping(target = "REJECTED", source = "REJECTED"),
            @ValueMapping(target = "REJECTED", source = "EXPIRED"),
            @ValueMapping(target = "APPROVED", source = "OFFERED"),
            @ValueMapping(target = "APPROVED", source = "APPROVED"),
            @ValueMapping(target = "ACCOUNT_SNAPSHOT_VERIFICATION", source = "ACCOUNT_SNAPSHOT_VERIFICATION"),
            @ValueMapping(target = MappingConstants.NULL, source = "ESIGN_COMPLETE"),
            @ValueMapping(target = MappingConstants.NULL, source = "ESIGN_FAILED"),
            @ValueMapping(target = MappingConstants.NULL, source = "LOAN_CREATED")
    })
    LoanApplicationStatus toLoanApplicationStatus(LoanStatus loanStatus);
}
