package de.joonko.loan.reporting.api.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Builder
@Slf4j
public class GetOfferStatusResponse implements Exportable {

    private String userUUID;
    private String distributionChannelUUID;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private OffsetDateTime bankAccountAddedAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private OffsetDateTime personalDataAddedAt;

    private String purpose;
    private Integer loanAmountRequested;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private OffsetDateTime loanAmountRequestedAt;

    private String offerProvider;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private OffsetDateTime offersReceivedAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private OffsetDateTime offerAcceptedAt;

    private String kycStatus;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private OffsetDateTime kycStatusLastUpdatedAt;

    private String offerStatus;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private OffsetDateTime offerStatusLastUpdatedAt;

    @JsonIgnore
    @Override
    public LinkedHashMap<String, Method> getFieldsForExport() {
        final LinkedHashMap<String, Method> map = new LinkedHashMap<>();
        try {
            map.put("User ID", new PropertyDescriptor("userUUID", GetOfferStatusResponse.class).getReadMethod());
            map.put("Distribution Channel", new PropertyDescriptor("distributionChannelUUID", GetOfferStatusResponse.class).getReadMethod());
            map.put("Loan requested amount", new PropertyDescriptor("loanAmountRequested", GetOfferStatusResponse.class).getReadMethod());
            map.put("Loan purpose", new PropertyDescriptor("purpose", GetOfferStatusResponse.class).getReadMethod());
            map.put("Loan provider", new PropertyDescriptor("offerProvider", GetOfferStatusResponse.class).getReadMethod());
            map.put("amountEnteredAt", new PropertyDescriptor("loanAmountRequestedAt", GetOfferStatusResponse.class).getReadMethod());
            map.put("bankConnectedAt", new PropertyDescriptor("bankAccountAddedAt", GetOfferStatusResponse.class).getReadMethod());
            map.put("personalDataEnteredAt", new PropertyDescriptor("personalDataAddedAt", GetOfferStatusResponse.class).getReadMethod());
            map.put("offersReceivedAt", new PropertyDescriptor("offersReceivedAt", GetOfferStatusResponse.class).getReadMethod());
            map.put("offerAcceptedAt", new PropertyDescriptor("offerAcceptedAt", GetOfferStatusResponse.class).getReadMethod());
            map.put("kycLastUpdatedAt", new PropertyDescriptor("kycStatusLastUpdatedAt", GetOfferStatusResponse.class).getReadMethod());
            map.put("Kyc status", new PropertyDescriptor("kycStatus", GetOfferStatusResponse.class).getReadMethod());
            map.put("Current loan status", new PropertyDescriptor("offerStatus", GetOfferStatusResponse.class).getReadMethod());
            map.put("offerLastStatusUpdatedAt", new PropertyDescriptor("offerStatusLastUpdatedAt", GetOfferStatusResponse.class).getReadMethod());
        } catch (final Exception ex) {
            log.error("Exception occurred while trying to get a getter method for one of the fields for class {}, exception message - {}, exception cause - {}", "GetOfferStatusResponse", ex.getMessage(), ex.getCause());
        }
        return map;
    }
}
