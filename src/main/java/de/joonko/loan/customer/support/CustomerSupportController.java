package de.joonko.loan.customer.support;

import de.joonko.loan.customer.support.api.model.AuditTrail;
import de.joonko.loan.db.repositories.LoanApplicationAuditTrailRepository;
import de.joonko.loan.db.service.LoanOfferStoreService;
import de.joonko.loan.db.vo.LoanApplicationAuditTrail;
import de.joonko.loan.exception.GenericExceptionHandler;
import de.joonko.loan.offer.api.CustomerSupportOffersRequest;
import de.joonko.loan.offer.api.LoanOfferResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequiredArgsConstructor
@GenericExceptionHandler
public class CustomerSupportController {

    private final LoanOfferStoreService loanOfferStoreService;

    private final LoanApplicationAuditTrailRepository loanApplicationAuditTrailRepository;

    @PostMapping(value = "loan/customer-support/offers", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<LoanOfferResponse> getOffers(@RequestBody CustomerSupportOffersRequest customerSupportOffersRequest) {
        return loanOfferStoreService.findAllByLoanApplicationId(customerSupportOffersRequest.getLoanApplicationId())
                .stream()
                .map(loanOfferStore -> LoanOfferResponse.builder()
                        .loanOfferId(loanOfferStore.getLoanOfferId())
                        .loanApplicationId(loanOfferStore.getApplicationId())
                        .offer(loanOfferStore.getOffer())
                        .build())
                .collect(Collectors.toList());
    }

    @PostMapping(value = "loan/customer-support/applicationAudit", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AuditTrail> getLoanApplicationStatus(@RequestBody CustomerSupportOffersRequest customerSupportOffersRequest) {
        List<LoanApplicationAuditTrail> loanApplicationAuditTrailList = loanApplicationAuditTrailRepository.findAllByApplicationIdOrderByInsertTs(customerSupportOffersRequest.getLoanApplicationId());
        List<AuditTrail> auditTrailList = loanApplicationAuditTrailList.stream().map(loanApplicationAuditTrail -> AuditTrail.builder()
                .error(loanApplicationAuditTrail.getError())
                .loanApplicationId(loanApplicationAuditTrail.getApplicationId())
                .loanProvider(loanApplicationAuditTrail.getLoanProvider())
                .remark(loanApplicationAuditTrail.getRemark())
                .status(loanApplicationAuditTrail.getStatus())
                .build())
                .collect(Collectors.toList());
        return auditTrailList;
    }

}
