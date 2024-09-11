package de.joonko.loan.webhooks.webid.model.request;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "webidWebHookNotification")
public class Ident {
    public String responseType;
    public String identMode;
    public String actionType;
    public String actionId;
    public String transactionId;
    public boolean success;
    public String finishedOn;
    public String identifiedOn;
    public String rejectionReason;
    public int agentId;
    public boolean mismatch;
    public CustomParameters customParameters;
    public ProductInfos productInfos;
    public JointUserActionStatus jointUserActionStatus;
    public QesContinue qesContinue;

    public UserDocumentDownload userDocumentDownload; // part of Qes notification.

    public String expireType; // part of Expiry notification.
    public String expiredOn; // part of Expiry notification.
}