package de.joonko.loan.identification.testdata;

import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.db.vo.LoanOfferStore;
import de.joonko.loan.identification.model.Document;
import de.joonko.loan.identification.model.idnow.CreateIdentRequest;
import de.joonko.loan.offer.api.LoanDuration;
import de.joonko.loan.offer.api.LoanOffer;
import de.joonko.loan.offer.api.LoanProvider;
import de.joonko.loan.partner.aion.model.CreditApplicationResponseStore;
import de.joonko.loan.user.service.UserAdditionalInformationStore;
import de.joonko.loan.user.service.UserPersonalInformationStore;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class IdentificationControllerAionITTestData {

    private static final Bank bank = Bank.AION;

    public LoanOfferStore getEditedLoanOfferStore(LoanOfferStore loanOfferStore, String applicationId, String loanOfferId, String userUuid) {
        loanOfferStore.setApplicationId(applicationId);
        loanOfferStore.setLoanOfferId(loanOfferId);
        loanOfferStore.setUserUUID(userUuid);
        loanOfferStore.setIsAccepted(true);
        loanOfferStore.setKycStatus(null);
        loanOfferStore.setKycUrl(null);
        loanOfferStore.setKycProvider(null);
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

    public UserPersonalInformationStore getUserPersonalInformationStore(String userUuid) {
        var userPersonalInformationStore = new UserPersonalInformationStore();
        userPersonalInformationStore.setUserUUID(userUuid);

        return userPersonalInformationStore;
    }

    public UserAdditionalInformationStore getUserAdditionalInformationStore(String userUuid) {
        var userAdditionalInformationStore = new UserAdditionalInformationStore();
        userAdditionalInformationStore.setUserUUID(userUuid);

        return userAdditionalInformationStore;
    }

    public CreditApplicationResponseStore getEditedCreditApplicationResponseStore(CreditApplicationResponseStore creditApplicationResponseStore, String applicationId) {
        creditApplicationResponseStore.setApplicationId(applicationId);
        creditApplicationResponseStore.setRepresentativeId("7450c385-017a-46cf-a3c7-597b951d051e");

        return creditApplicationResponseStore;
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

    public Document getDocument(String documentId, String content) {
        return Document.builder()
                .documentId(documentId)
                .content(Base64.getDecoder().decode(content.getBytes(StandardCharsets.UTF_8)))
                .build();
    }
}
