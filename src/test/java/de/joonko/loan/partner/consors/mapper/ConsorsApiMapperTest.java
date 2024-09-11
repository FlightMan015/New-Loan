package de.joonko.loan.partner.consors.mapper;

import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.offer.domain.LoanDuration;
import de.joonko.loan.partner.auxmoney.getoffers.BaseMapperTest;
import de.joonko.loan.partner.consors.model.BankAccount;
import de.joonko.loan.partner.consors.model.ValidateSubscriptionRequest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

import io.github.glytching.junit.extension.random.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@TestPropertySource(properties = {"TWEAKED_IBAN_IN_LOAN_DEMAND_REQUEST=DE123TWEAKEDIBAN987"})
public class ConsorsApiMapperTest extends BaseMapperTest {

    @Autowired
    ConsorsLoanProviderApiMapper consorsLoanProviderApiMapper;


    @Test
    @DisplayName("Should map to tweaked Iban in test env")
    void tweakBankAccount(@Random LoanDemand loanDemand) {
        ValidateSubscriptionRequest validateSubscriptionRequest = consorsLoanProviderApiMapper.toLoanProviderRequest(loanDemand, LoanDuration.TWENTY_FOUR);

        BankAccount bankAccount = validateSubscriptionRequest.getBankAccount();
        assertAll(
                () -> assertThat(bankAccount.getIban()).isEqualTo("DE123TWEAKEDIBAN987")
        );
    }
}
