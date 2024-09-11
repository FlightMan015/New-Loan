package de.joonko.loan.partner.consors.mapper;

import com.google.common.base.Strings;

import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.offer.domain.LoanDuration;
import de.joonko.loan.offer.domain.LoanOffer;
import de.joonko.loan.offer.domain.LoanProviderApiMapper;
import de.joonko.loan.partner.consors.model.PersonalizedCalculationsResponse;
import de.joonko.loan.partner.consors.model.ValidateSubscriptionRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class ConsorsLoanProviderApiMapper implements LoanProviderApiMapper<ValidateSubscriptionRequest, PersonalizedCalculationsResponse> {

    private final ConsorsPersonalizedCalculationsResponseMapper consorsResponseMapper;

    private final ValidateSubscriptionRequestMapper validateSubscriptionRequestMapper;

    @Value("${TWEAKED_IBAN_IN_LOAN_DEMAND_REQUEST:#{NULL}}")
    private String tweakedIBAN;

    @Override
    public ValidateSubscriptionRequest toLoanProviderRequest(LoanDemand loanDemand, LoanDuration loanDuration) {
        ValidateSubscriptionRequest validateSubscriptionRequest = validateSubscriptionRequestMapper.toLoanProviderRequest(loanDemand);
        if (!Strings.isNullOrEmpty(tweakedIBAN)) {
            log.info("Env variable TWEAKED_IBAN_IN_LOAN_DEMAND_REQUEST is only meant for test environments. Modifying IBAN for consors with {}", tweakedIBAN);
            validateSubscriptionRequest.getBankAccount().setIban(tweakedIBAN);
        }
        return validateSubscriptionRequest;
    }


    @Override
    public List<LoanOffer> fromLoanProviderResponse(PersonalizedCalculationsResponse response) {
        return consorsResponseMapper.fromLoanProviderResponse(response);
    }
}
