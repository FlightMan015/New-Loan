package de.joonko.loan.partner.consors.mapper;

import de.joonko.loan.acceptoffer.domain.OfferStatus;
import de.joonko.loan.partner.auxmoney.getoffers.BaseMapperTest;
import de.joonko.loan.partner.consors.model.ConsorsAcceptOfferResponse;
import de.joonko.loan.partner.consors.model.Link;
import de.joonko.loan.partner.consors.model.SubscriptionStatus;
import io.github.glytching.junit.extension.random.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ConsorsAcceptLoanOfferMapperTest extends BaseMapperTest {

    private static final String KYC_REL = "_onlineIdent";
    private static final String DOCUMENT_REL = "_downloadSubscriptionDocument";

    @Autowired
    private ConsorsAcceptOfferResponseMapper consorsAcceptOfferResponseMapper;

    private static ConsorsAcceptOfferResponse createData(ConsorsAcceptOfferResponse consorsAcceptOfferResponse) {
        Link link1 = new Link();
        link1.setRel(DOCUMENT_REL);
        link1.setHref("https://dummyDoc.com");

        Link link2 = new Link();
        link2.setRel("_onlineIdent");
        link2.setHref(KYC_REL);

        consorsAcceptOfferResponse.setLinks(Arrays.asList(link1, link2));
        consorsAcceptOfferResponse.setSubscriptionStatus(SubscriptionStatus.APPROVED);
        consorsAcceptOfferResponse.getFinancialCalculation().setDuration(24);
        return consorsAcceptOfferResponse;

    }

    private static ConsorsAcceptOfferResponse emptyData(ConsorsAcceptOfferResponse consorsAcceptOfferResponse) {
        consorsAcceptOfferResponse.setSubscriptionStatus(SubscriptionStatus.APPROVED);
        consorsAcceptOfferResponse.getFinancialCalculation().setDuration(24);
        return consorsAcceptOfferResponse;

    }

    @Test
    @DisplayName("Should map creditAmount to loanAsked")
    void loanAsked(@Random ConsorsAcceptOfferResponse consorsAcceptOfferResponse) {
        OfferStatus offerStatus = consorsAcceptOfferResponseMapper.fromConsorsResponse(createData(consorsAcceptOfferResponse));
        assertEquals(offerStatus.getLoanAsked(), consorsAcceptOfferResponse.getFinancialCalculation().getCreditAmount());
    }

    @Test
    @DisplayName("Should map loanDuration")
    void loanDuration(@Random ConsorsAcceptOfferResponse consorsAcceptOfferResponse) {
        OfferStatus offerStatus = consorsAcceptOfferResponseMapper.fromConsorsResponse(createData(consorsAcceptOfferResponse));
        assertEquals(offerStatus.getDuration().getValue(), consorsAcceptOfferResponse.getFinancialCalculation().getDuration());
    }

    @Test
    @DisplayName("Should map monthlyRate to monnthlyPayment")
    void monthlyRate(@Random ConsorsAcceptOfferResponse consorsAcceptOfferResponse) {
        OfferStatus offerStatus = consorsAcceptOfferResponseMapper.fromConsorsResponse(createData(consorsAcceptOfferResponse));
        assertEquals(offerStatus.getMonthlyPayment(), consorsAcceptOfferResponse.getFinancialCalculation().getMonthlyRate());
    }

    @Test
    @DisplayName("Should map effectiveRate")
    void effectiveRate(@Random ConsorsAcceptOfferResponse consorsAcceptOfferResponse) {
        OfferStatus offerStatus = consorsAcceptOfferResponseMapper.fromConsorsResponse(createData(consorsAcceptOfferResponse));
        assertEquals(offerStatus.getEffectiveRate(), consorsAcceptOfferResponse.getFinancialCalculation().getEffectiveRate());
    }

    @Test
    @DisplayName("Should map status")
    void status(@Random ConsorsAcceptOfferResponse consorsAcceptOfferResponse) {
        OfferStatus offerStatus = consorsAcceptOfferResponseMapper.fromConsorsResponse(createData(consorsAcceptOfferResponse));
        assertEquals(offerStatus.getStatus().toString(), consorsAcceptOfferResponse.getSubscriptionStatus().toString());
    }

    @Test
    @DisplayName("Should map nominalRate")
    void nominalRate(@Random ConsorsAcceptOfferResponse consorsAcceptOfferResponse) {
        OfferStatus offerStatus = consorsAcceptOfferResponseMapper.fromConsorsResponse(createData(consorsAcceptOfferResponse));
        assertEquals(offerStatus.getNominalRate(), consorsAcceptOfferResponse.getFinancialCalculation().getNominalRate());
    }

    @Test
    @DisplayName("Should map totalInterestAmount to totalInterestPayment")
    void mapTotalInterestAmount(@Random ConsorsAcceptOfferResponse consorsAcceptOfferResponse) {
        OfferStatus offerStatus = consorsAcceptOfferResponseMapper.fromConsorsResponse(createData(consorsAcceptOfferResponse));
        assertEquals(offerStatus.getTotalInterestPayment(), consorsAcceptOfferResponse.getFinancialCalculation().getTotalInterestAmount());
    }

    @Test
    @DisplayName("Should map totalPayment")
    void totalPayment(@Random ConsorsAcceptOfferResponse consorsAcceptOfferResponse) {
        OfferStatus offerStatus = consorsAcceptOfferResponseMapper.fromConsorsResponse(createData(consorsAcceptOfferResponse));
        assertEquals(offerStatus.getTotalPayment(), consorsAcceptOfferResponse.getFinancialCalculation().getTotalPayment());
    }

    @Test
    @DisplayName("Should ignore kycUrl")
    void kycUrl(@Random ConsorsAcceptOfferResponse consorsAcceptOfferResponse) {
        OfferStatus offerStatus = consorsAcceptOfferResponseMapper.fromConsorsResponse(createData(consorsAcceptOfferResponse));
        assertEquals(offerStatus.getKycUrl(), null);
    }

    @Test
    @DisplayName("Should map contractDocumentUrl")
    void contractDocumentUrl(@Random ConsorsAcceptOfferResponse consorsAcceptOfferResponse) {
        OfferStatus offerStatus = consorsAcceptOfferResponseMapper.fromConsorsResponse(createData(consorsAcceptOfferResponse));
        assertEquals(offerStatus.getContractDocumentUrl(), consorsAcceptOfferResponse.getLinks()
                .stream()
                .filter(link -> link.getRel().equalsIgnoreCase(DOCUMENT_REL))
                .findFirst()
                .get().getHref());
    }

    @Test
    @DisplayName("Should throw error contractDocumentUrl")
    void contractDocumentUrlError(@Random ConsorsAcceptOfferResponse consorsAcceptOfferResponse) {
        Exception exception = assertThrows(RuntimeException.class, () -> consorsAcceptOfferResponseMapper.fromConsorsResponse(emptyData(consorsAcceptOfferResponse)));
        assertEquals("Unable to find download subscription document", exception.getMessage());
    }
}
