package de.joonko.loan.dac.fts;

import de.joonko.loan.dac.fts.api.WizardSessionDetails;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
public class FTSWizardSessionDetailsController {

    private final FTSWizardSessionDetailsService ftsWizardSessionDetailsService;

    @PostMapping(value = "api/v1/loan/dac/sessionDetails", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean postWizardSessionDetails(@RequestBody WizardSessionDetails wizardSessionDetails) {

        try {
            log.info("saving wizard session details for the wizard session key : {}", wizardSessionDetails.getWizardSessionKey());
            ftsWizardSessionDetailsService.saveWizardSessionDetails(FTSWizardSessionDetailsStore.builder()
                    .wizardSessionKey(wizardSessionDetails.getWizardSessionKey())
                    .bankCode(wizardSessionDetails.getBankCode())
                    .bankName(wizardSessionDetails.getBankName())
                    .transactionId(wizardSessionDetails.getTransactionId())
                    .status(wizardSessionDetails.getStatus())
                    .error(wizardSessionDetails.getError())
                    .errorCode(wizardSessionDetails.getErrorCode())
                    .recoverable(wizardSessionDetails.getRecoverable())
                    .build());
            log.info("saving wizard session details success for the wizard session key : {}", wizardSessionDetails.getWizardSessionKey());
        } catch (Exception exc) {
            log.info("saving wizard session details failed for the wizard session key : {} with error {}", wizardSessionDetails.getWizardSessionKey(), exc.getMessage());
        }
        return true;
    }

}
