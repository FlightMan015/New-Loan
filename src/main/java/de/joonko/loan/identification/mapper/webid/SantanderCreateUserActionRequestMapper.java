package de.joonko.loan.identification.mapper.webid;

import de.joonko.loan.db.service.LoanOfferStoreService;
import de.joonko.loan.db.vo.LoanOfferStore;
import de.joonko.loan.identification.config.WebIdPropConfig;
import de.joonko.loan.identification.model.CreateIdentRequest;
import de.joonko.loan.identification.model.webid.useractionrequest.CreateUserActionRequest;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import static de.joonko.loan.common.AppConstants.CLOSURE_PAGE;

@Mapper(componentModel = "spring")
public abstract class SantanderCreateUserActionRequestMapper {

    @Autowired
    WebIdPropConfig webIdPropConfig;

    @Autowired
    LoanOfferStoreService loanOfferStoreService;

    @Mapping(target = "transactionId", source = "applicationId")
    @Mapping(target = "preferredLanguage", source = "language")
    @Mapping(target = "actionType", constant = "sig")
    @Mapping(target = "identMode", constant = "video_ident")
    @Mapping(target = "termsAndConditionsConfirmed", constant = "true")

    @Mapping(target = "user.sex", source = "gender")
    @Mapping(target = "user.firstname", source = "firstName")
    @Mapping(target = "user.lastname", source = "lastName")
    @Mapping(target = "user.dateOfBirth", source = "birthday")

    @Mapping(target = "user.address.street", source = "street")
    @Mapping(target = "user.address.streetNo", source = "houseNumber")
    @Mapping(target = "user.address.zip", source = "zipCode")
    @Mapping(target = "user.address.city", source = "city")
    @Mapping(target = "user.address.country", source = "country")

    @Mapping(target = "user.contact.email", source = "email")
    @Mapping(target = "user.contact.cell", source = "mobilePhone")

    @Mapping(target = "device.userIp", constant = "::1")

    @Mapping(target = "processParameters.redirectUrl", source = "createIdentRequest.applicationId", qualifiedByName = "getClosurePage")
    @Mapping(target = "processParameters.redirectDeclineUrl", source = "createIdentRequest.applicationId", qualifiedByName = "getKycNotRetryablePage")
    @Mapping(target = "processParameters.redirectSkipQesUrl", source = "createIdentRequest.applicationId", qualifiedByName = "getKycRetryablePage")
    @Mapping(target = "processParameters.redirectCancelIdentUrl", source = "createIdentRequest.applicationId", qualifiedByName = "getKycRetryablePage")
    @Mapping(target = "processParameters.redirectTime", constant = "10")
    @Mapping(target = "processParameters.productType", constant = "Loan Application")

    @Mapping(target = "customParameters.md", source = ".", qualifiedByName = "getMdParam")
    @Mapping(target = "customParameters.mdTi", source = ".", qualifiedByName = "getMdTiParam")

    public abstract CreateUserActionRequest toCreateUserActionRequest(CreateIdentRequest createIdentRequest);

    @Named("getClosurePage")
    String getClosurePage(String applicationId) {
        return webIdPropConfig.getFrontendHost() + CLOSURE_PAGE + applicationId;
    }

    @Named("getKycRetryablePage")
    String getKycRetryablePage(String applicationId) {
        return webIdPropConfig.getFrontendHost() + CLOSURE_PAGE + applicationId + "&r=true";
    }

    @Named("getKycNotRetryablePage")
    String getKycNotRetryablePage(String applicationId) {
        return webIdPropConfig.getFrontendHost() + CLOSURE_PAGE + applicationId + "&r=false";
    }

    @Named("getMdParam")
    String getMdParam(CreateIdentRequest createIdentRequest) {
        return webIdPropConfig.getSantanderMd();
    }

    @Named("getMdTiParam")
    String getMdTiParam(CreateIdentRequest createIdentRequest) {
        LoanOfferStore santanderAcceptedOffer = loanOfferStoreService.findByLoanOfferId(createIdentRequest.getLoanOfferId());
        // https://bonify.atlassian.net/browse/B2B-771
        return "bc_" + santanderAcceptedOffer.getLoanProviderReferenceNumber();
    }

}
