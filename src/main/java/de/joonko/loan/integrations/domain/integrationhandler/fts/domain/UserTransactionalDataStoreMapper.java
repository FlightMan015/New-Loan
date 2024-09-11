package de.joonko.loan.integrations.domain.integrationhandler.fts.domain;

import de.joonko.loan.avro.dto.dac.DacAccountSnapshot;
import de.joonko.loan.integrations.domain.enhancers.KycRelatedPersonalDetails;
import de.joonko.loan.offer.api.*;

import de.joonko.loan.user.service.UserPersonalInformationStore;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Arrays;

import static java.util.Optional.ofNullable;

@Mapper(componentModel = "spring", uses = {AccountDetailsMapper.class, CustomDacPersonalDetailsMapper.class})
public interface UserTransactionalDataStoreMapper {

    @Mapping(target = "accountDetails", source = "digitalAccountStatement")
    @Mapping(target = "userUUID", source = "userUUID")
    @Mapping(target = "customDacPersonalDetails", source = "personalDetails")
    UserTransactionalDataStore toTransactionalDataStore(DacAccountSnapshot dacModel);

    @Mapping(target = "nameOnAccount", source = "userPersonalInformationStore", qualifiedByName = "mapAccountName")
    KycRelatedPersonalDetails mapToKycRelatedPersonalDetails(AccountDetails accountDetails, UserPersonalInformationStore userPersonalInformationStore);

    @Named("mapAccountName")
    default String mapAccountName(UserPersonalInformationStore userPersonalInformationStore) {
        return ofNullable(userPersonalInformationStore)
                .map(personal -> StringUtils.join(Arrays.asList(personal.getLastName(), personal.getFirstName()), ", "))
                .orElse(null);
    }

    @Named("extractEmploymentType")
    default EmploymentType extractEmploymentType(UserTransactionalDataStore userTransactionalDataStore) {
        boolean hasEmployer = StringUtils.isNotBlank(userTransactionalDataStore.getCustomDacPersonalDetails().getEmployerName());
        return hasEmployer ? EmploymentType.REGULAR_EMPLOYED : EmploymentType.OTHER;
    }

    default EmploymentDetails customMapping(EmploymentDetails employmentDetails, CustomDacPersonalDetails customPersonalDACData, CustomDACData customDACData) {
        if (employmentDetails != null && employmentDetails.getEmploymentType() != null) {
            return employmentDetails;
        }

        var hasSalary = customDACData.getHasSalary() != null && customDACData.getHasSalary();
        var employmentType = hasSalary ? EmploymentType.REGULAR_EMPLOYED : EmploymentType.OTHER;
        var employerName = customPersonalDACData != null ? customPersonalDACData.getEmployerName() : null;

        return EmploymentDetails.builder().employmentType(employmentType).employerName(employerName).build();
    }
}
