package de.joonko.loan.partner.consors.mapper;

import de.joonko.loan.offer.domain.LoanCategory;
import de.joonko.loan.partner.consors.model.KycPurposeOfLoan;
import org.mapstruct.Mapper;
import org.mapstruct.ValueMapping;
import org.mapstruct.ValueMappings;

@Mapper(componentModel = "spring")
public interface ConsorsKycPurposeOfLoanMapper {


    @ValueMappings({
            @ValueMapping(source = "CAR_LOAN", target = "AMA"),
            @ValueMapping(source = "FURNITURE_RENOVATION_MOVE", target = "ERM"),
            @ValueMapping(source = "VACATION", target = "RUE"),
            @ValueMapping(source = "OTHER", target = "FSH"),
            @ValueMapping(source = "ELECTRONICS", target = "ELK")
    })
    KycPurposeOfLoan toProvidersKycPurposeOfLoan(LoanCategory loanCategory);
}
