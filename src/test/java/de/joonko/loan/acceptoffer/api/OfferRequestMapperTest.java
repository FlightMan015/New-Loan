package de.joonko.loan.acceptoffer.api;

import de.joonko.loan.acceptoffer.domain.OfferRequest;
import de.joonko.loan.acceptoffer.domain.OfferStatus;
import de.joonko.loan.common.domain.Bank;
import de.joonko.loan.db.vo.LoanOfferStore;
import de.joonko.loan.offer.api.LoanOffer;
import de.joonko.loan.offer.api.LoanProvider;
import de.joonko.loan.offer.domain.LoanDuration;
import de.joonko.loan.partner.auxmoney.getoffers.BaseMapperTest;

import org.junit.Assert;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import io.github.glytching.junit.extension.random.Random;

class OfferRequestMapperTest extends BaseMapperTest {

    @Autowired
    private OfferRequestMapper offerRequestMapper;

    @Nested
    class RequestMapperTest {

        @Test
        void should_map_loan_application_id_from_request_to_offer_request(@Random LoanOfferStore loanOfferStore) {
            OfferRequest offerRequest = offerRequestMapper.fromRequest(loanOfferStore);
            Assert.assertEquals(loanOfferStore.getApplicationId(), offerRequest.getApplicationId());
        }

        @Test
        void should_map_loan_asked_from_request_to_offer_request(@Random LoanOfferStore loanOfferStore) {
            OfferRequest offerRequest = offerRequestMapper.fromRequest(loanOfferStore);
            Assert.assertEquals(loanOfferStore.getOffer().getAmount(), offerRequest.getLoanAsked().intValue());
        }

        @Test
        void should_map_duration_from_request_to_offer_request(@Random LoanOfferStore loanOfferStore) {
            loanOfferStore.setOffer(LoanOffer.builder().durationInMonth(24).build());
            OfferRequest offerRequest = offerRequestMapper.fromRequest(loanOfferStore);
            Assert.assertEquals(24, offerRequest.getDuration().getValue());
        }

        @Test
        void should_map_loan_provider_from_request_to_offer_request(@Random LoanOfferStore loanOfferStore) {
            LoanOffer build = LoanOffer.builder().loanProvider(new LoanProvider(Bank.CONSORS.label)).build();
            loanOfferStore.setOffer(build);
            OfferRequest offerRequest = offerRequestMapper.fromRequest(loanOfferStore);
            Assert.assertEquals(loanOfferStore.getOffer().getLoanProvider().getName(), offerRequest.getLoanProvider());
        }

        @Test
        void should_map_loan_offer_id_from_request_to_offer_request(@Random LoanOfferStore loanOfferStore) {
            OfferRequest offerRequest = offerRequestMapper.fromRequest(loanOfferStore);
            Assert.assertEquals(loanOfferStore.getLoanOfferId(), offerRequest.getLoanOfferId());
        }

    }

    @Nested
    class ResponseMapperTest {

        @Test
        void should_map_loan_asked_from_offer_status_to_response(@Random OfferStatus offerStatus) {
            AcceptOfferResponse acceptOfferResponse = offerRequestMapper.toResponse(offerStatus);
            Assert.assertEquals(offerStatus.getLoanAsked(), acceptOfferResponse.getLoanAsked());
        }

        @Test
        void should_map_duration_from_offer_status_to_response(@Random OfferStatus offerStatus) {
            offerStatus.setDuration(LoanDuration.FORTY_EIGHT);
            AcceptOfferResponse acceptOfferResponse = offerRequestMapper.toResponse(offerStatus);
            Assert.assertEquals(offerStatus.getDuration().getValue(), acceptOfferResponse.getDuration().intValue());
        }

        @Test
        void should_map_monthly_payment_from_offer_status_to_response(@Random OfferStatus offerStatus) {
            AcceptOfferResponse acceptOfferResponse = offerRequestMapper.toResponse(offerStatus);
            Assert.assertEquals(offerStatus.getMonthlyPayment(), acceptOfferResponse.getMonthlyPayment());
        }

        @Test
        void should_map_effective_rate_from_offer_status_to_response(@Random OfferStatus offerStatus) {
            AcceptOfferResponse acceptOfferResponse = offerRequestMapper.toResponse(offerStatus);
            Assert.assertEquals(offerStatus.getEffectiveRate(), acceptOfferResponse.getEffectiveRate());
        }

        @Test
        void should_map_nominal_rate_from_offer_status_to_response(@Random OfferStatus offerStatus) {
            AcceptOfferResponse acceptOfferResponse = offerRequestMapper.toResponse(offerStatus);
            Assert.assertEquals(offerStatus.getNominalRate(), acceptOfferResponse.getNominalRate());
        }

        @Test
        void should_map_total_interest_payment_from_offer_status_to_response(@Random OfferStatus offerStatus) {
            AcceptOfferResponse acceptOfferResponse = offerRequestMapper.toResponse(offerStatus);
            Assert.assertEquals(offerStatus.getTotalInterestPayment(), acceptOfferResponse.getTotalInterestPayment());
        }

        @Test
        void should_map_total_payment_from_offer_status_to_response(@Random OfferStatus offerStatus) {
            AcceptOfferResponse acceptOfferResponse = offerRequestMapper.toResponse(offerStatus);
            Assert.assertEquals(offerStatus.getTotalPayment(), acceptOfferResponse.getTotalPayment());
        }

        @Test
        void should_map_kyc_url_from_offer_status_to_response(@Random OfferStatus offerStatus) {
            AcceptOfferResponse acceptOfferResponse = offerRequestMapper.toResponse(offerStatus);
            Assert.assertEquals(offerStatus.getKycUrl(), acceptOfferResponse.getKycUrl());
        }

        @Test
        void should_map_contract_document_url_from_offer_status_to_response(@Random OfferStatus offerStatus) {
            AcceptOfferResponse acceptOfferResponse = offerRequestMapper.toResponse(offerStatus);
            Assert.assertEquals(offerStatus.getContractDocumentUrl(), acceptOfferResponse.getContractDocumentUrl());
        }

        @Test
        void should_map_loan_application_status_from_offer_status_to_response(@Random OfferStatus offerStatus) {
            AcceptOfferResponse acceptOfferResponse = offerRequestMapper.toResponse(offerStatus);
            Assert.assertEquals(offerStatus.getStatus(), acceptOfferResponse.getStatus());
        }

        @Test
        void should_map_errors_from_offer_status_to_response(@Random OfferStatus offerStatus) {
            AcceptOfferResponse acceptOfferResponse = offerRequestMapper.toResponse(offerStatus);
            Assert.assertEquals(offerStatus.getErrors(), acceptOfferResponse.getErrors());
        }
    }
}
