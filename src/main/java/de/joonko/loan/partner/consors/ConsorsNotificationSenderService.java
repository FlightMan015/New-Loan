package de.joonko.loan.partner.consors;

import de.joonko.loan.config.MailConfig;
import de.joonko.loan.dac.fts.FTSAccountSnapshotGateway;
import de.joonko.loan.db.service.LoanApplicationAuditTrailService;
import de.joonko.loan.db.service.LoanDemandStoreService;
import de.joonko.loan.db.vo.LoanDemandStore;
import de.joonko.loan.email.MailClientGateway;
import de.joonko.loan.email.model.Email;
import de.joonko.loan.email.model.EmailAttachments;
import de.joonko.loan.email.util.ConsorsDacPDFHelper;
import de.joonko.loan.offer.domain.DomainDefault;
import de.joonko.loan.user.service.UserPersonalInformationRepository;
import de.joonko.loan.user.service.UserPersonalInformationStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ConsorsNotificationSenderService {

    private final ConsorsPropertiesConfig consorsPropertiesConfig;
    private final LoanDemandStoreService loanDemandStoreService;
    private final FTSAccountSnapshotGateway ftsAccountSnapshotGateway;
    private final LoanApplicationAuditTrailService loanApplicationAuditTrailService;
    private final MailClientGateway mailClientGateway;
    private final MailConfig mailConfig;

    private static final String EMAIL_SUBJECT_FRAGMENT = "Digital Account Check PDF";

    private final UserPersonalInformationRepository userPersonalInformationRepository;

    public void sendEmailWithAccountSnapshot(String contractIdentifier, String id) {
        log.info("Sending email for application id {}", id);
        try {
            mailClientGateway.sendEmailWithAttachment(getEmailModel(contractIdentifier, id));
            loanApplicationAuditTrailService.dacEmailNotificationToConsorsSuccess(id, contractIdentifier);
        } catch (Exception exc) {
            log.error("Sending Email to Consors failed: " + exc);
            loanApplicationAuditTrailService.dacEmailNotificationToConsorsError(id, contractIdentifier, exc.getMessage());
        }
    }

    private Email getEmailModel(String contractIdentifier, String id) {

        LoanDemandStore loanDemandStore = getLoanDemandStore(id);
        UserPersonalInformationStore userPersonalInformationStore = userPersonalInformationRepository.findById(loanDemandStore.getUserUUID())
                .orElseThrow();
        String customerName = String.format("%s %s", userPersonalInformationStore.getFirstName(), userPersonalInformationStore.getLastName());
        EmailAttachments emailAttachments = EmailAttachments.builder()
                .content(getConsorsAccountSnapshotPdf(loanDemandStore.getFtsTransactionId(), customerName, contractIdentifier))
                .fileName("digitalAccountCheck_" + contractIdentifier + ".pdf")
                .encoding("base64")
                .build();

        return Email.builder()
                .attachments(List.of(emailAttachments))
                .subject(contractIdentifier + " " + EMAIL_SUBJECT_FRAGMENT)
                .fromAddress(mailConfig.getFromAddress())
                .toAddress(consorsPropertiesConfig.getDacEmail())
                .text("")
                .build();
    }

    private LoanDemandStore getLoanDemandStore(String id) {
        return loanDemandStoreService.findById(id).orElseThrow(() -> new RuntimeException("applicationId not found " + id));
    }


    private String getConsorsAccountSnapshotPdf(String transactionId, String customerName, String contractIdentifier) {
        try {
            try (InputStream accountSnapshot = ftsAccountSnapshotGateway.getAccountSnapshot(transactionId, DomainDefault.FTS_QUERY_PARAM_VALUE_PDF)) {
                return Base64.getEncoder().encodeToString(IOUtils.toByteArray(ConsorsDacPDFHelper.addCoverLetterToPdf(accountSnapshot, customerName, contractIdentifier)));
            }
        } catch (IOException exc) {
            throw new RuntimeException("Failed to Add cover letter to Consors DAC Pdf:" + exc);
        }
    }
}
