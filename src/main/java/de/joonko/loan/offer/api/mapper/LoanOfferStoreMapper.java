package de.joonko.loan.offer.api.mapper;

import de.joonko.loan.offer.api.model.LoanOfferStore;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface LoanOfferStoreMapper {

    @Mapping(target = "isRecommendation", source = "parentApplicationId", qualifiedByName = "getIsRecommendation")
    LoanOfferStore map(final de.joonko.loan.db.vo.LoanOfferStore loanOfferStore);

    @Named("getIsRecommendation")
    default boolean isRecommendation(String parentApplicationId) {
        return parentApplicationId != null;
    }

}
