package de.joonko.loan.partner.creditPlus.mapper.model;

import de.joonko.loan.partner.creditPlus.stub.EfinComparerServiceStub;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@Document
public class CreditPlusOffer {

    @Id
    private String creditPlusOfferId;
    private String applicationId;
    private String dealerOrderNumber;
    private List<Integer> contractState;
    private Boolean applicationStatusUpdated;
    @CreatedDate
    private LocalDateTime createdAt;
    private EfinComparerServiceStub.Contract creditOffer;
    private LocalDateTime updatedAt;
}
