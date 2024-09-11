package de.joonko.loan.partner.consors;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.common.domain.LoanProvider;
import de.joonko.loan.common.partner.consors.auth.JwtToken;
import de.joonko.loan.data.support.DataSupportService;
import de.joonko.loan.db.service.LoanApplicationAuditTrailService;
import de.joonko.loan.db.service.LoanDemandRequestService;
import de.joonko.loan.exception.LoanDemandGatewayException;
import de.joonko.loan.offer.api.LoanDemandRequest;
import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.offer.domain.LoanDemandGateway;
import de.joonko.loan.offer.domain.LoanDuration;
import de.joonko.loan.offer.domain.LoanOffer;
import de.joonko.loan.partner.consors.mapper.ConsorsLoanProviderApiMapper;
import de.joonko.loan.partner.consors.model.ConsorsAcceptOfferRequest;
import de.joonko.loan.partner.consors.model.ConsorsAcceptOfferResponse;
import de.joonko.loan.partner.consors.model.FinancialCalculation;
import de.joonko.loan.partner.consors.model.FinancialCalculations;
import de.joonko.loan.partner.consors.model.FinancialCondition;
import de.joonko.loan.partner.consors.model.LinkRelation;
import de.joonko.loan.partner.consors.model.PersonalizedCalculationsResponse;
import de.joonko.loan.partner.consors.model.ValidateSubscriptionRequest;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(
        value = "consors.enabled",
        havingValue = "true",
        matchIfMissing = true)
public class ConsorsLoanDemandGateway implements LoanDemandGateway<ConsorsLoanProviderApiMapper, ValidateSubscriptionRequest, PersonalizedCalculationsResponse> {

    private static final String SUBSCRIPTION_REL = "_finalizesubscription";
    private static final String CANCEL_SUBSCRIPTION_REL = "_cancelSubscriptionDocument";

    private final ConsorsLoanProviderApiMapper consorsLoanProviderApiMapper;

    private final ConsorsClient consorsClient;

    private final ConsorsProfileFilter profileFilter;

    private final ConsorsLinkExtractor consorsLinkExtractor;

    private final LoanApplicationAuditTrailService loanApplicationAuditTrailService;
    private final ConsorsStoreService consorsStoreService;
    private final DataSupportService dataSupportService;
    private final ConsorsFinancialCalculationService financialCalculationService;
    private final ConsorsPrecheckFilter precheckFilter;
    private final LoanDemandRequestService loanDemandRequestService;

    @Override
    public LoanProvider getLoanProvider() {
        return LoanProvider.builder().name(Bank.CONSORS.getLabel()).build();
    }

    @Override
    public ConsorsLoanProviderApiMapper getMapper() {
        return consorsLoanProviderApiMapper;
    }

    @Override
    public Mono<PersonalizedCalculationsResponse> callApi(ValidateSubscriptionRequest validateSubscriptionRequest, String id) {
        loanApplicationAuditTrailService.sendingLoanDemandRequest(id, Bank.CONSORS);
        log.info("Requesting to Consors for applicationId {} and SubscriptionIdentifierExternal id {}", id, validateSubscriptionRequest.getSubscriptionIdentifierExternal());

        return consorsClient.getToken(id)
                .zipWhen(token -> loanDemandRequestService.findLoanDemandRequest(id))
                .flatMap(tuple -> getPersonalizedCalculations(validateSubscriptionRequest, tuple.getT1(), tuple.getT2()))
                .doOnNext(personalizedCalculationsResponse -> log.info("SubscriptionStatus response from Consors {} ", personalizedCalculationsResponse.getFinancialCalculations()))
                .doOnSuccess(personalizedCalculationsResponse -> consorsStoreService.savePersonalizedCalculations(id, personalizedCalculationsResponse))
                .doOnError(throwable -> {
                    log.error("error while receiving response from consors {}", toErrorMessageString(throwable));
                    loanApplicationAuditTrailService.receivedLoanDemandResponseError(id, throwable.getMessage(), Bank.CONSORS);
                });
    }

    @Override
    public Boolean filterGateway(LoanDemand loanDemand) {
        return !precheckFilter.test(loanDemand);
    }

    @Override
    public List<LoanDuration> getDurations(Integer loanAsked) {
        // Return some dummy duration so that the gateway is hit once from loanDemandService and fetch offers for all durations in a single call.
        return List.of(LoanDuration.FORTY_EIGHT);
    }

    private Mono<PersonalizedCalculationsResponse> getPersonalizedCalculations(final ValidateSubscriptionRequest validateSubscriptionRequest, final JwtToken token, final LoanDemandRequest loanDemandRequest) {
        return subscribe(validateSubscriptionRequest, token, loanDemandRequest)
                .flatMap(personalizedCalculations -> filterYellowProfile(validateSubscriptionRequest, token, personalizedCalculations, loanDemandRequest));
    }

    private Mono<String> getProducts(JwtToken token, String id) {
        return consorsClient.getProducts(token, id)
                .doOnNext(s -> log.info("get product method returns {} ", s))
                .doOnError(e -> loanApplicationAuditTrailService.saveApplicationError(id, "Error in getting products " + e.getMessage(), Bank.CONSORS.name()))
                .onErrorMap(e -> new LoanDemandGatewayException("Error in getting products ", e));
    }

    private Mono<String> followLinkToValidationRules(JwtToken token, String getProductsJson, String id) {
        Link link = consorsLinkExtractor.validationRulesLink(getProductsJson)
                .orElseThrow(() -> {
                    loanApplicationAuditTrailService.saveApplicationError(id, "No Suitable Operation available for validation rules", Bank.CONSORS.name());
                    return new LoanDemandGatewayException("No Suitable Operation available for validation rules");
                });

        logAsJson(link, id, "extracting 1st link");

        return consorsClient.validateRules(token, link, id)
                .doOnNext(s -> log.info("calling validation link url result  {}", s));
    }

    private Mono<String> followLinkToValidateSubscription(ValidateSubscriptionRequest validateSubscriptionRequest, JwtToken token, String validationRulesJson, String id) {
        Link link = consorsLinkExtractor.validationSubscriptionLink(validationRulesJson)
                .orElseThrow(() -> {
                    loanApplicationAuditTrailService.saveApplicationError(id, "No Suitable Operation available validate subscription", Bank.CONSORS.name());
                    return new LoanDemandGatewayException("No Suitable Operation available validate subscription");
                });

        logAsJson(link, id, "validate subscription link");
        logAsJson(validateSubscriptionRequest, id, "validate Subscription Request");

        return consorsClient.validateSubscription(token, link, validateSubscriptionRequest, id)
                .doOnNext(s -> logAsJson(s, id, "validateSubscription response"));
    }

    private Mono<PersonalizedCalculationsResponse> followLinkToPersonalizedCalculations(final JwtToken token, final String validateSubscriptionResponse, final LoanDemandRequest loanDemandRequest) {
        Link link = consorsLinkExtractor.personalizedCalculationsLink(validateSubscriptionResponse)
                .orElseThrow(() -> {
                    loanApplicationAuditTrailService.saveApplicationError(loanDemandRequest.getApplicationId(), "No Suitable Operation available personalized calculations", Bank.CONSORS.name());
                    return new LoanDemandGatewayException("No Suitable Operation available personalized calculations");
                });
        logAsJson(link, loanDemandRequest.getApplicationId(), "extracting 2nd link");
        return consorsClient.getPersonalizedCalculations(token, link, loanDemandRequest.getApplicationId())
                .doOnNext(personalizedCalculationsResponse -> {
                    logAsJson(personalizedCalculationsResponse, loanDemandRequest.getApplicationId(), "received personalizedCalculationsResponse");
                })
                .flatMap(response -> financialCalculationService.removeNotValidOffers(response, loanDemandRequest));
    }

    private Mono<PersonalizedCalculationsResponse> filterYellowProfile(ValidateSubscriptionRequest validateSubscriptionRequest, JwtToken token, PersonalizedCalculationsResponse personalizedCalculationsResponse, final LoanDemandRequest loanDemandRequest) {
        FinancialCalculations financialCalculations = personalizedCalculationsResponse.getFinancialCalculations();

        if (profileFilter.isRed(financialCalculations.getFinancialCalculation())) {
            log.info("application {} received red offers", loanDemandRequest.getApplicationId());
            loanApplicationAuditTrailService.receivedAsRedProfileConsors(loanDemandRequest.getApplicationId());
            dataSupportService.pushRedOffersReceivedTopic(loanDemandRequest.getApplicationId(), Bank.CONSORS.toString(), financialCalculations.getSubscriptionStatus(), financialCalculations.getRefusalCategory(), financialCalculations.toString());
            return Mono.just(personalizedCalculationsResponse);
        }
        log.info("filtering yellow offers for application {}", loanDemandRequest.getApplicationId());
        LinkRelation link = personalizedCalculationsResponse.getFinancialCalculations()
                .getLinks()
                .stream()
                .filter(linkRelation -> linkRelation.getRel()
                        .equals(SUBSCRIPTION_REL))
                .findFirst()
                .get();
        ConsorsAcceptOfferRequest acceptOfferRequest = getAcceptOfferRequest(financialCalculations.getFinancialCalculation().get(0));

        logAsJson(link, loanDemandRequest.getApplicationId(), "accept offer url");
        logAsJson(acceptOfferRequest, loanDemandRequest.getApplicationId(), "accept offer request");

        return consorsClient.finalizeSubscription(token, link, acceptOfferRequest, loanDemandRequest.getApplicationId())
                .doOnNext(consorsAcceptOfferResponse -> logAsJson(consorsAcceptOfferResponse, loanDemandRequest.getApplicationId(), "consors offers for application"))
                .zipWhen(acceptOfferResponse -> cancelSubscription(acceptOfferResponse, token, loanDemandRequest.getApplicationId()))
                .filter(tuple -> isGreenProfile(tuple.getT1(), loanDemandRequest.getApplicationId(), personalizedCalculationsResponse))
                .flatMap(tuple -> subscribe(validateSubscriptionRequest, token, loanDemandRequest));
    }

    private void sendYellowOffersToDataAnalytics(List<LoanOffer> loanOfferList, String loanApplicationId, String remark) {
        dataSupportService.pushYellowOffersReceivedTopic(loanOfferList, loanApplicationId, remark);
    }

    private ConsorsAcceptOfferRequest getAcceptOfferRequest(FinancialCalculation financialCalculation) {
        FinancialCondition financialCondition = FinancialCondition.builder()
                .creditAmount(financialCalculation.getCreditAmount())
                .duration(financialCalculation.getDuration())
                .build();
        return ConsorsAcceptOfferRequest.builder()
                .financialCondition(financialCondition)
                .paymentDay(1).build();

    }

    public boolean isGreenProfile(ConsorsAcceptOfferResponse acceptOfferResponse, String id, PersonalizedCalculationsResponse personalizedCalculationsResponse) {
        boolean isGreenProfile = profileFilter.isGreen(acceptOfferResponse);

        if (!isGreenProfile) {
            log.info("no green offers found for application {}", id);
            loanApplicationAuditTrailService.receivedAsYellowProfileConsors(id, acceptOfferResponse.getSupportingDocumentsRequired());
            sendYellowOffersToDataAnalytics(consorsLoanProviderApiMapper.fromLoanProviderResponse(personalizedCalculationsResponse), id, getRemark(acceptOfferResponse));
        } else {
            log.info("application {} received green offers", id);
            loanApplicationAuditTrailService.receivedAsGreenProfileConsors(id, personalizedCalculationsResponse.getFinancialCalculations().getSubscriptionStatus());
        }
        return isGreenProfile;
    }

    private String getRemark(ConsorsAcceptOfferResponse acceptOfferResponse) {
        return "Supporting documents required : " + acceptOfferResponse.getSupportingDocumentsRequired() + " and subscription status :" + acceptOfferResponse.getSubscriptionStatus();
    }

    private Mono<PersonalizedCalculationsResponse> subscribe(ValidateSubscriptionRequest validateSubscriptionRequest, JwtToken token, final LoanDemandRequest loanDemandRequest) {
        return getProducts(token, loanDemandRequest.getApplicationId())
                .flatMap(getProductsJson -> followLinkToValidationRules(token, getProductsJson, loanDemandRequest.getApplicationId()))
                .flatMap(validationRulesJson -> followLinkToValidateSubscription(validateSubscriptionRequest, token, validationRulesJson, loanDemandRequest.getApplicationId()))
                .flatMap(validateSubscriptionJson -> followLinkToPersonalizedCalculations(token, validateSubscriptionJson, loanDemandRequest));
    }

    private Mono<ResponseEntity<Void>> cancelSubscription(ConsorsAcceptOfferResponse acceptOfferResponse, JwtToken token, String applicationId) {
        logAsJson(acceptOfferResponse, acceptOfferResponse.getContractIdentifier(), "accept offer response from Consors while doing cancelSubscription");

        Optional<de.joonko.loan.partner.consors.model.Link> cancelLinkMaybe = Optional.ofNullable(acceptOfferResponse.getLinks()).orElse(new ArrayList<>()).stream()
                .filter(linkRelation -> linkRelation.getRel().equals(CANCEL_SUBSCRIPTION_REL)).findFirst();
        log.info("cancel link is presented {}", cancelLinkMaybe.isPresent());
        if (cancelLinkMaybe.isPresent()) {
            de.joonko.loan.partner.consors.model.Link cancelLink = cancelLinkMaybe.get();

            logAsJson(cancelLink, acceptOfferResponse.getContractIdentifier(), "accept offer response from Consors cancel link");
            return consorsClient.cancelSubscription(token, cancelLink, applicationId)
                    .doOnSuccess(any -> log.info("Cancelled subscription for contractIdentifier: {}", acceptOfferResponse.getContractIdentifier()))
                    .doOnError(ex -> log.error(getMessage(ex), ex));
        } else {
            return Mono.just(ResponseEntity.ok().build());
        }

    }

    private String getMessage(Throwable ex) {
        String message = "cancel subscription of consors failed (%s)";
        if (ex instanceof WebClientResponseException) {
            String responseBodyAsString = ((WebClientResponseException) ex).getResponseBodyAsString();
            return String.format("%s - Response body: %s", message, responseBodyAsString);
        } else {
            return String.format(message, this.getClass());
        }
    }

    private Object toErrorMessageString(Throwable ex) {
        if (null != ex) {
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            return errors.toString();
        }
        return null;
    }

    private void logAsJson(Object object, String identifier, String logMessage) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            log.debug("{} , identifier {}, object is {}", logMessage, identifier, objectMapper.writeValueAsString(object));
        } catch (Exception e) {
            log.error("unable to log as json {}, identifier {}", logMessage, identifier, e);
        }
    }

}
