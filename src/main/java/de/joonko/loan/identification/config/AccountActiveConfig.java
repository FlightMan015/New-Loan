package de.joonko.loan.identification.config;

import de.joonko.loan.identification.model.idnow.IdNowAccount;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

import lombok.Builder;

@Component
public class AccountActiveConfig {

    @Value("${auxmoney.enabled:false}")
    private Boolean auxmoneyEnabled;

    @Value("${consors.enabled:false}")
    private Boolean consorsEnabled;

    @Builder.Default
    @Value("${consors.webidEnabled:false}")
    private Boolean webidEnabled = false;

    @Value("${swk.enabled:false}")
    private Boolean swkEnabled;

    @Value("${creditplus.enabled:false}")
    private Boolean creditPlusEnabled;

    @Value("${aion.enabled:false}")
    private Boolean aionEnabled;

    public Set<IdNowAccount> getActiveIdNowAccounts() {
        Set<IdNowAccount> result = new HashSet<>();
        if (auxmoneyEnabled) {
            result.add(IdNowAccount.AUXMONEY);
        }
        if (consorsEnabled && !webidEnabled) {
            result.add(IdNowAccount.CONSORS);
        }
        if (swkEnabled) {
            result.add(IdNowAccount.SWK);
        }
        if (creditPlusEnabled) {
            result.add(IdNowAccount.CREDITPLUS);
        }
        if (aionEnabled) {
            result.add(IdNowAccount.AION);
        }
        return result;
    }
}
