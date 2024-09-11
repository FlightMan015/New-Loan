package de.joonko.loan.partner.postbank.model.store;

import de.joonko.loan.partner.postbank.model.response.LoanDemandPostbankResponseStatus;
import de.joonko.loan.webhooks.postbank.model.CreditResult;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Set;

import javax.validation.constraints.NotNull;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
@Document
public class PostbankLoanDemandStore {

    @Id
    private String id;

    @NotNull
    private String applicationId;

    private LoanDemandPostbankResponseStatus status;

    @NotNull
    private String contractNumber;

    @Builder.Default
    @NotNull
    private Set<CreditResult> creditResults = Set.of();

    @CreatedDate
    private LocalDateTime insertTs;
}
