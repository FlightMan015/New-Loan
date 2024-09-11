package de.joonko.loan.partner.creditPlus.mapper.model;

import de.joonko.loan.partner.creditPlus.stub.EfinComparerServiceStub;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@Document
public class CreditPlusAcceptedOffer {

    @Id
    private String id;
    private String applicationId;
    @CreatedDate
    private LocalDateTime createdAt;
    private EfinComparerServiceStub.Contract creditOffer;
}
