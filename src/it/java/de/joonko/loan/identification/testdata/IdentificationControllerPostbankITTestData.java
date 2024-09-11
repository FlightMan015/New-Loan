package de.joonko.loan.identification.testdata;

import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.db.vo.LoanOfferStore;
import de.joonko.loan.identification.model.Document;
import de.joonko.loan.identification.model.IdentificationLink;
import de.joonko.loan.identification.model.IdentificationProvider;
import de.joonko.loan.identification.model.idnow.CreateIdentRequest;
import de.joonko.loan.offer.api.LoanDuration;
import de.joonko.loan.offer.api.LoanOffer;
import de.joonko.loan.offer.api.LoanProvider;
import de.joonko.loan.partner.aion.model.CreditApplicationResponseStore;
import de.joonko.loan.partner.postbank.model.store.PostbankLoanDemandStore;
import de.joonko.loan.user.service.UserAdditionalInformationStore;
import de.joonko.loan.user.service.UserPersonalInformationStore;
import de.joonko.loan.webhooks.postbank.model.ContractState;
import de.joonko.loan.webhooks.postbank.model.CreditResult;
import de.joonko.loan.webhooks.postbank.model.DebtorInformation;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Set;

public class IdentificationControllerPostbankITTestData {

    private static final Bank bank = Bank.POSTBANK;

    public LoanOfferStore getEditedLoanOfferStore(LoanOfferStore loanOfferStore, String applicationId, String loanOfferId, String userUuid) {
        loanOfferStore.setApplicationId(applicationId);
        loanOfferStore.setLoanOfferId(loanOfferId);
        loanOfferStore.setUserUUID(userUuid);
        loanOfferStore.setIsAccepted(true);
        loanOfferStore.setKycStatus(null);
        loanOfferStore.setKycUrl(null);
        loanOfferStore.setContracts(null);
        loanOfferStore.setOffer(LoanOffer.builder()
                .loanProvider(new LoanProvider(bank.getLabel()))
                .amount(123)
                .durationInMonth(LoanDuration.SIX.getValue())
                .monthlyRate(BigDecimal.ONE)
                .totalPayment(BigDecimal.TEN)
                .build());

        return loanOfferStore;
    }

    public LoanOfferStore getCancelledKycLoanOfferStore(LoanOfferStore loanOfferStore, String applicationId, String loanOfferId, String userUuid) {
        loanOfferStore.setApplicationId(applicationId);
        loanOfferStore.setLoanOfferId(loanOfferId);
        loanOfferStore.setUserUUID(userUuid);
        loanOfferStore.setIsAccepted(true);
        loanOfferStore.setKycStatus("CANCELLED");
        loanOfferStore.setOffer(LoanOffer.builder()
                .loanProvider(new LoanProvider(bank.getLabel()))
                .amount(123)
                .durationInMonth(LoanDuration.SIX.getValue())
                .monthlyRate(BigDecimal.ONE)
                .totalPayment(BigDecimal.TEN)
                .build());

        return loanOfferStore;
    }

    public UserPersonalInformationStore getUserPersonalInformationStore(String userUuid) {
        var userPersonalInformationStore = new UserPersonalInformationStore();
        userPersonalInformationStore.setUserUUID(userUuid);
        userPersonalInformationStore.setFirstName("John");

        return userPersonalInformationStore;
    }

    public UserAdditionalInformationStore getUserAdditionalInformationStore(String userUuid) {
        var userAdditionalInformationStore = new UserAdditionalInformationStore();
        userAdditionalInformationStore.setUserUUID(userUuid);

        return userAdditionalInformationStore;
    }

    public CreateIdentRequest getCreateIdentRequest() {
        var creditIdentRequest = new CreateIdentRequest();

        creditIdentRequest.setBirthday("");
        creditIdentRequest.setCountry("DE");
        creditIdentRequest.setCustom1("7450c385-017a-46cf-a3c7-597b951d051e");
        creditIdentRequest.setMobilePhone("");
        creditIdentRequest.setEmail("");

        return creditIdentRequest;
    }

    public PostbankLoanDemandStore getPostbankLoanDemandStore(String applicationId, String kycUrl) {
        return PostbankLoanDemandStore.builder()
                .applicationId(applicationId)
                .creditResults(
                        Set.of(CreditResult.builder()
                                        .partnerContractNumber(applicationId)
                                        .contractState(ContractState.ONLINE_GENEHMIGT_24)
                                        .debtorInformation(DebtorInformation.builder()
                                                .digitaleSignaturUrl(kycUrl)
                                                .build())
                                        .build(),
                                CreditResult.builder()
                                        .partnerContractNumber(applicationId)
                                        .contractState(ContractState.UNTERLAGEN_EINGEGANGEN_25)
                                        .build()
                        )
                ).build();
    }

    public IdentificationLink getIdentificationLink(String applicationId, String offerId, String kycUrl) {
        return IdentificationLink.builder()
                .applicationId(applicationId)
                .offerId(offerId)
                .loanProvider("POSTBANK")
                .externalIdentId(applicationId)
                .identProvider(IdentificationProvider.WEB_ID)
                .kycUrl(kycUrl)
                .build();
    }
}
