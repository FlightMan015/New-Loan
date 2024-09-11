package de.joonko.loan.db.vo;

import de.joonko.loan.contract.model.DocumentDetails;
import de.joonko.loan.identification.model.IdentificationProvider;
import de.joonko.loan.offer.api.LoanOffer;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.keyvalue.annotation.KeySpace;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

import lombok.Builder;
import lombok.Data;

import static java.util.Objects.nonNull;

@KeySpace("loanOfferStore")
@Data
@Builder(toBuilder = true)
@Document("loanOfferStore")
public class LoanOfferStore {

    @Id
    private String loanOfferId;
    @Indexed
    private String userUUID;
    private String applicationId;
    private String parentApplicationId;
    private String kycUrl;
    private String offerStatus;
    private OffsetDateTime statusLastUpdateDate;
    private OffsetDateTime acceptedDate;
    private OffsetDateTime kycStartedDate;
    private OffsetDateTime kycStatusLastUpdateDate;
    private String kycStatus;
    private IdentificationProvider kycProvider;
    private LoanOffer offer;
    @Indexed
    private Boolean isAccepted;
    private OfferAcceptedEnum acceptedBy;
    private String loanProviderReferenceNumber;
    private List<DocumentDetails> contracts;
    @Indexed
    private Boolean deleted;
    @Indexed
    @CreatedDate
    private LocalDateTime insertTS;
    @Indexed
    @LastModifiedDate
    private LocalDateTime lastModifiedTS;

    public void setOfferStatus(final String offerStatus) {
        this.offerStatus = offerStatus;
        this.statusLastUpdateDate = OffsetDateTime.now();
    }

    public void setIsAccepted(final boolean accepted) {
        this.isAccepted = accepted;
        this.acceptedDate = OffsetDateTime.now();
    }

    public void setKycStatus(final String newKycStatus) {
        if (Objects.isNull(this.kycStatus)) {
            this.kycStartedDate = OffsetDateTime.now();
        }
        this.kycStatus = newKycStatus;
        this.kycStatusLastUpdateDate = OffsetDateTime.now();
    }

    public boolean identificationAlreadyPassedAndContractsAvailable() {
        return nonNull(this.getKycUrl()) && nonNull(contracts) && !contracts.isEmpty();
    }
}
