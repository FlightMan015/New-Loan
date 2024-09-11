package de.joonko.loan.partner.auxmoney.mapper;

import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.partner.auxmoney.AuxmoneyDefaults;
import de.joonko.loan.partner.auxmoney.model.AuxmoneyGetOffersRequest;
import de.joonko.loan.partner.auxmoney.model.DigitalAccountStatements;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        imports = Collections.class,
        componentModel = "spring",
        uses = {AuxmoneyIncomeMapper.class,
                AuxmoneyPersonalDataMapper.class,
                AuxmoneyContactDataMapper.class,
                AuxmoneyExpenseMapper.class,
                AuxmoneyDigitalAccountStatementsMapper.class,
                AuxmoneyLoanCategoryMapper.class,
                AuxmoneyLoanDurationMapper.class,
                AuxmoneyEmployerDataMapper.class,
                AuxmoneyBankDataMapper.class})
abstract class AuxmoneyGetOffersRequestMapper {

    @Autowired
    AuxmoneyDigitalAccountStatementsMapper digitalAccountStatementsMapper;


    @Mapping(target = "externalId", expression = "java(de.joonko.loan.db.vo.ExternalIdentifiers.auxmoneyExternalIdentifierFromApplicationId(loanDemand.getLoanApplicationId()))")
    @Mapping(source = "category", target = "category")
    @Mapping(source = "loanAsked", target = "loanAsked")
    @Mapping(source = "duration", target = "duration")
    @Mapping(target = "collectionDay", expression = "java(de.joonko.loan.partner.auxmoney.model.PaymentCollectionDay.FIRST_OF_MONTH)")
    @Mapping(target = "acceptedSolvencyRetrieval", constant = AuxmoneyDefaults.ACCEPTED_SOLVENCY_RETRIEVAL)
    @Mapping(target = "acceptedTermsOfService", constant = AuxmoneyDefaults.ACCEPTED_TERMS_OF_SERVICE)
    @Mapping(source = "personalDetails", target = "personalData")
    @Mapping(source = "contactData", target = "contactData")
    @Mapping(source = "personalDetails.finance.expenses", target = "expenses")
    @Mapping(source = "personalDetails.finance.income", target = "income")
    @Mapping(source = "digitalAccountStatements", target = "digitalAccountStatements")
    @Mapping(target = "employerData", source = "employmentDetails")
    @Mapping(target = "bankData", source = "digitalAccountStatements")
    @Mapping(target = "household", ignore = true)
    @Mapping(target = "secondBorrower", ignore = true)
    @Mapping(target = "partnerBroker", ignore = true)
    @Mapping(target = "rsv", ignore = true)

    abstract AuxmoneyGetOffersRequest toAuxmoneyRequest(LoanDemand loanDemand);

    List<DigitalAccountStatements> map(de.joonko.loan.offer.domain.DigitalAccountStatements digitalAccountStatements) {
        if (digitalAccountStatements == null) {
            return Collections.emptyList();
        }
        return Collections.singletonList(digitalAccountStatementsMapper.toAuxmoneyDigitalAccountStatements(digitalAccountStatements));
    }

}

