package de.joonko.loan.identification.service;

import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.identification.model.CreateIdentRequest;
import de.joonko.loan.identification.model.CreateIdentResponse;
import de.joonko.loan.identification.model.IdentificationAuditTrail;
import de.joonko.loan.identification.model.IdentificationProvider;
import de.joonko.loan.identification.model.KycAuditStatus;
import de.joonko.loan.metric.kyc.KycMetric;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IdentificationAuditService {

    private final IdentificationAuditTrailRepository identificationAuditTrailRepository;
    private final KycMetric metric;

    public void contractUploadFail(CreateIdentRequest createIdentRequest, String message, IdentificationProvider identificationProvider) {
        IdentificationAuditTrail identificationAuditTrail = IdentificationAuditTrail.builder()
                .applicationId(createIdentRequest.getApplicationId())
                .loanProvider(createIdentRequest.getLoanProvider())
                .status(KycAuditStatus.CONTRACT_UPLOAD_ERROR.name())
                .remark(message)
                .build();
        identificationAuditTrailRepository.save(identificationAuditTrail);
        metric.incrementKycCounter(KycMetric.KYC_FAILED, createIdentRequest.getLoanProvider(), identificationProvider);
    }

    public void contractUploadSuccess(CreateIdentRequest createIdentRequest) {
        IdentificationAuditTrail identificationAuditTrail = IdentificationAuditTrail.builder()
                .applicationId(createIdentRequest.getApplicationId())
                .loanProvider(createIdentRequest.getLoanProvider())
                .status(KycAuditStatus.CONTRACT_UPLOADED.name())
                .build();
        identificationAuditTrailRepository.save(identificationAuditTrail);
    }

    public void identCreatedSuccess(String identId, CreateIdentRequest createIdentRequest, IdentificationProvider provider) {

        IdentificationAuditTrail identificationAuditTrail = IdentificationAuditTrail.builder()
                .applicationId(createIdentRequest.getApplicationId())
                .loanProvider(createIdentRequest.getLoanProvider())
                .status(KycAuditStatus.IDENT_CREATED.name())
                .remark("Ident Created with ident ID: " + identId)
                .build();
        identificationAuditTrailRepository.save(identificationAuditTrail);
        metric.incrementKycCounter(KycMetric.KYC_INIT, createIdentRequest.getLoanProvider(), provider);
    }

    public void identCreationFailure(Throwable e, CreateIdentRequest createIdentRequest, IdentificationProvider identificationProvider) {
        IdentificationAuditTrail identificationAuditTrail = IdentificationAuditTrail.builder()
                .applicationId(createIdentRequest.getApplicationId())
                .loanProvider(createIdentRequest.getLoanProvider())
                .status(KycAuditStatus.IDENT_CREATION_ERROR.name())
                .remark(e.getMessage())
                .build();
        identificationAuditTrailRepository.save(identificationAuditTrail);
        metric.incrementKycCounter(KycMetric.KYC_FAILED, createIdentRequest.getLoanProvider(), identificationProvider);
    }

    public void kycLinkCreated(CreateIdentResponse createIdentResponse, CreateIdentRequest createIdentRequest) {
        IdentificationAuditTrail identificationAuditTrail = IdentificationAuditTrail.builder()
                .loanProvider(createIdentRequest.getLoanProvider())
                .applicationId(createIdentRequest.getApplicationId())
                .status(KycAuditStatus.KYC_LINK_CREATED.name())
                .remark(createIdentResponse.getKycUrl())
                .build();
        identificationAuditTrailRepository.save(identificationAuditTrail);
    }

    public void kycLinkCreatedSolaris(String applicationId, String kycUrl) {
        IdentificationAuditTrail identificationAuditTrail = IdentificationAuditTrail.builder()
                .loanProvider(Bank.DEUTSCHE_FINANZ_SOZIETÄT.getLabel())
                .applicationId(applicationId)
                .status(KycAuditStatus.KYC_LINK_CREATED.name())
                .remark(kycUrl)
                .build();
        identificationAuditTrailRepository.save(identificationAuditTrail);
    }

    public void kycInitiated(final String loanProvider, final String applicationId) {
        IdentificationAuditTrail identificationAuditTrail = IdentificationAuditTrail.builder()
                .loanProvider(loanProvider)
                .applicationId(applicationId)
                .status(KycAuditStatus.KYC_INITIATED.name())
                .build();
        identificationAuditTrailRepository.save(identificationAuditTrail);
    }

    public void notificationSent(CreateIdentRequest createIdentRequest, String identId) {
        IdentificationAuditTrail identificationAuditTrail = IdentificationAuditTrail.builder()
                .loanProvider(createIdentRequest.getLoanProvider())
                .remark("Notification sent for ident Id " + identId)
                .applicationId(createIdentRequest.getApplicationId())
                .status(KycAuditStatus.PARTNER_NOTIFICATION_SENT.name())
                .build();
        identificationAuditTrailRepository.save(identificationAuditTrail);
    }

    public void notificationSentError(CreateIdentRequest createIdentRequest, String identId, String message) {
        IdentificationAuditTrail identificationAuditTrail = IdentificationAuditTrail.builder()
                .loanProvider(createIdentRequest.getLoanProvider())
                .remark("Error While sending Notification ident Id " + identId + "Error : " + message)
                .applicationId(createIdentRequest.getApplicationId())
                .status(KycAuditStatus.PARTNER_NOTIFICATION_SENT_ERROR.name())
                .build();
        identificationAuditTrailRepository.save(identificationAuditTrail);
    }

    public String getKycUrl(String applicationId) {
        return identificationAuditTrailRepository.findByApplicationId(applicationId)
                .stream()
                .filter(identificationAuditTrail -> identificationAuditTrail.getStatus().equalsIgnoreCase(KycAuditStatus.KYC_LINK_CREATED.name()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Unable to find kycAudit information for applicationId " + applicationId))
                .getRemark();
    }

    public String getKycUrlForSolaris(String applicationId) {
        return identificationAuditTrailRepository.findByRemarkContains(applicationId)
                .stream()
                .filter(identificationAuditTrail -> identificationAuditTrail.getStatus().equalsIgnoreCase(KycAuditStatus.KYC_LINK_CREATED.name()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Unable to find kycAudit information for applicationId for Solaris" + applicationId))
                .getRemark();
    }
}
