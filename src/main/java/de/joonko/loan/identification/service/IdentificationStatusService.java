package de.joonko.loan.identification.service;

import de.joonko.loan.util.EncrDecrService;
import de.joonko.loan.webhooks.idnow.model.Identification;
import de.joonko.loan.webhooks.idnow.model.UserAttribute;
import de.joonko.loan.webhooks.idnow.repositores.IdentificationWebHookRepository;
import de.joonko.loan.webhooks.solaris.model.SolarisIdNowWebhookRequest;
import de.joonko.loan.webhooks.solaris.repositores.SolarisIdNowWebHookRepository;
import de.joonko.loan.webhooks.webid.model.request.Ident;
import de.joonko.loan.webhooks.webid.repositores.WebidIdentWebHookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class IdentificationStatusService {

    private final IdentificationWebHookRepository identificationWebHookRepository;
    private final SolarisIdNowWebHookRepository solarisIdNowWebHookRepository;
    private final WebidIdentWebHookRepository webIdIdentWebHookRepository;
    private final EncrDecrService encrDecrService;

    // Remove the method after migration
    public void gpdrMigration() {
        identificationWebHookRepository.findAll().forEach(record -> {
            if (null != record.getContactData()) {
                String email = record.getContactData().getEmail();

                if (email.contains("@")) {
                    record.getContactData().setEmail(encrDecrService.anonymize(email));

                    String mobilePhone = record.getContactData().getMobilePhone();
                    record.getContactData().setMobilePhone(encrDecrService.anonymize(mobilePhone));

                    if (null != record.getUserData()) {
                        UserAttribute<String> gender = record.getUserData().getGender();
                        UserAttribute<String> firstName = record.getUserData().getFirstName();
                        UserAttribute<String> lastName = record.getUserData().getLastName();

                        if (null != gender && !StringUtils.isEmpty(gender.getValue()) && !gender.getValue().contains(":"))
                            record.getUserData().getGender().setValue(encrDecrService.anonymize(gender.getValue()));

                        if (null != firstName && !StringUtils.isEmpty(firstName.getValue()) && !firstName.getValue().contains(":"))
                            record.getUserData().getFirstName().setValue(encrDecrService.anonymize(firstName.getValue()));

                        if (null != lastName && !StringUtils.isEmpty(lastName.getValue()) && !lastName.getValue().contains(":"))
                            record.getUserData().getLastName().setValue(encrDecrService.anonymize(lastName.getValue()));
                    }
                }
            }

            identificationWebHookRepository.save(record);
        });
    }

    public void saveWebhookNotification(Identification identification) {
        log.info("Saving idnow webhook notification for applicationId {} ", identification.getIdentificationProcess().getTransactionNumber());
        identificationWebHookRepository.save(applyAnonymization(identification));
    }

    public void saveSolarisWebhookNotification(SolarisIdNowWebhookRequest solarisIdNowWebhookRequest, String loanApplicationId) {

        solarisIdNowWebhookRequest.setLoanApplicationId(loanApplicationId);
        log.info("Saving idnow webhook notification for identificationId {} ", solarisIdNowWebhookRequest.getIdentificationId());
        solarisIdNowWebHookRepository.save(solarisIdNowWebhookRequest);
    }

    public void saveWebIdIdentWebhookNotification(Ident ident) {
        log.info("Saving webid webhook notification for transactionId {} ", ident.getTransactionId());
        webIdIdentWebHookRepository.save(ident);
    }

    private Identification applyAnonymization(Identification identification) {
        if (null != identification.getContactData()) {
            String email = identification.getContactData().getEmail();
            String mobilePhone = identification.getContactData().getMobilePhone();

            if (!StringUtils.isEmpty(email))
                identification.getContactData().setEmail(encrDecrService.anonymize(email));

            if (!StringUtils.isEmpty(mobilePhone))
                identification.getContactData().setMobilePhone(encrDecrService.anonymize(mobilePhone));
        }

        if (null != identification.getUserData()) {
            UserAttribute<String> gender = identification.getUserData().getGender();
            UserAttribute<String> firstName = identification.getUserData().getFirstName();
            UserAttribute<String> lastName = identification.getUserData().getLastName();

            if (null != gender && !StringUtils.isEmpty(gender.getValue()))
                identification.getUserData().getGender().setValue(encrDecrService.anonymize(gender.getValue()));

            if (null != firstName && !StringUtils.isEmpty(firstName.getValue()))
                identification.getUserData().getFirstName().setValue(encrDecrService.anonymize(firstName.getValue()));

            if (null != lastName && !StringUtils.isEmpty(lastName.getValue()))
                identification.getUserData().getLastName().setValue(encrDecrService.anonymize(lastName.getValue()));
        }

        return identification;
    }
}
