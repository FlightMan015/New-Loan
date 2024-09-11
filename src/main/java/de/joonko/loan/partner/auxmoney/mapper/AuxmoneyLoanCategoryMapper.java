package de.joonko.loan.partner.auxmoney.mapper;

import de.joonko.loan.partner.auxmoney.model.LoanCategory;
import org.mapstruct.Mapper;
import org.mapstruct.ValueMapping;
import org.mapstruct.ValueMappings;

@Mapper(componentModel = "spring")
public interface AuxmoneyLoanCategoryMapper {


    @ValueMappings({
            @ValueMapping(source = "CAR_LOAN", target = "CAR_LOAN"),
            @ValueMapping(source = "FURNITURE_RENOVATION_MOVE", target = "FURNITURE_RENOVATION_MOVE"),
            @ValueMapping(source = "VACATION", target = "VACATION"),
            @ValueMapping(source = "OTHER", target = "OTHER"),
            @ValueMapping(source = "ELECTRONICS", target = "PC_HIFI_TV_VIDEO")
    })
    LoanCategory toProviderLoanCategory(de.joonko.loan.offer.domain.LoanCategory loanCategory);
}
