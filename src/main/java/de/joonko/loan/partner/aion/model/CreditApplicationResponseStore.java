package de.joonko.loan.partner.aion.model;

import de.joonko.loan.offer.domain.BestLoanOffer;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder(toBuilder = true)
@Document
public class CreditApplicationResponseStore {

    @Id
    private String id;

    @NotNull
    private String applicationId;

    @NotNull
    private String processId;

    private String representativeId;

    private List<Variable> variables;

    @Builder.Default
    private List<BestLoanOffer> offersToBeat = List.of();

    @Builder.Default
    private List<BestOfferValue> offersProvided = List.of();

    @Data
    @Builder
    public static class Variable {

        private AionResponseValueType name;
        private String value;
    }

    @CreatedDate
    private LocalDateTime insertTs;
}
