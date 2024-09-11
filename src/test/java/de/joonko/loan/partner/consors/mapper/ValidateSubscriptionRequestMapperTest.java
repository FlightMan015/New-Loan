package de.joonko.loan.partner.consors.mapper;

import de.joonko.loan.offer.domain.DigitalAccountStatements;
import de.joonko.loan.offer.domain.FamilyStatus;
import de.joonko.loan.offer.domain.Finance;
import de.joonko.loan.offer.domain.HousingType;
import de.joonko.loan.offer.domain.LoanCategory;
import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.offer.domain.LoanDuration;
import de.joonko.loan.offer.domain.Nationality;
import de.joonko.loan.offer.domain.PersonalDetails;
import de.joonko.loan.partner.auxmoney.getoffers.BaseMapperTest;
import de.joonko.loan.partner.consors.model.BankAccount;
import de.joonko.loan.partner.consors.model.FamilySituation;
import de.joonko.loan.partner.consors.model.KycPurposeOfLoan;
import de.joonko.loan.partner.consors.model.ValidateSubscriptionRequest;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import io.github.glytching.junit.extension.random.Random;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class ValidateSubscriptionRequestMapperTest extends BaseMapperTest {

    @Autowired
    private ValidateSubscriptionRequestMapper validateSubscriptionRequestMapper;


    @Test
    @DisplayName("Should map digitalAccountStatements to Account")
    void bankAccount(@Random LoanDemand loanDemand) {
        ValidateSubscriptionRequest validateSubscriptionRequest = validateSubscriptionRequestMapper.toLoanProviderRequest(loanDemand);
        DigitalAccountStatements digitalAccountStatements = loanDemand.getDigitalAccountStatements();

        BankAccount bankAccount = validateSubscriptionRequest.getBankAccount();
        assertEquals("MAIN", bankAccount.getOwner()
                .name());
        assertEquals(digitalAccountStatements.getIban(), bankAccount.getIban());
    }

    @Test
    @DisplayName("Should map SubscriptionBasketInfo to null")
    void price(@Random LoanDemand loanDemand) {
        ValidateSubscriptionRequest validateSubscriptionRequest = validateSubscriptionRequestMapper.toLoanProviderRequest(loanDemand);
        assertNull(validateSubscriptionRequest.getSubscriptionBasketInfo());
    }

    @Test
    @DisplayName("Should map isEsigned to null")
    void isEsigned(@Random LoanDemand loanDemand) {
        ValidateSubscriptionRequest validateSubscriptionRequest = validateSubscriptionRequestMapper.toLoanProviderRequest(loanDemand);
        assertNull(validateSubscriptionRequest.getIsEsigned());
    }

    @Test
    @DisplayName("Should generate random subscriptionIdentifierExternal max length 20")
    void subscriptionIdentifierExternal(@Random LoanDemand loanDemand) {

        ValidateSubscriptionRequest validateSubscriptionRequest = validateSubscriptionRequestMapper.toLoanProviderRequest(loanDemand);
        assertNotNull(validateSubscriptionRequest.getSubscriptionIdentifierExternal());
        assertEquals(20, validateSubscriptionRequest.getSubscriptionIdentifierExternal()
                .length());
    }

    @Test
    @DisplayName("Should map loanCategory to kycPurposeOfLoan")
    void kycPurposeOfLoan(@Random Finance finance) {
        LoanDemand loanDemand = new LoanDemand(RandomStringUtils.randomAlphabetic(20), 5000, "car", LoanDuration.TWENTY_FOUR, LoanCategory.CAR_LOAN, PersonalDetails.builder()
                .nationality(Nationality.DE)
                .housingType(HousingType.OWNER)
                .finance(finance)
                .build(), null, null, null, null, null, null, null, null, null, List.of(), null);

        ValidateSubscriptionRequest validateSubscriptionRequest = validateSubscriptionRequestMapper.toLoanProviderRequest(loanDemand);
        assertEquals(KycPurposeOfLoan.AMA, validateSubscriptionRequest.getKycPurposeOfLoan());
    }


    @Test
    @DisplayName("Should map income to Subscriber.income")
    void income(@Random LoanDemand loanDemand) {
        ValidateSubscriptionRequest validateSubscriptionRequest = validateSubscriptionRequestMapper.toLoanProviderRequest(loanDemand);
        assertNotNull(validateSubscriptionRequest.getSubscribers());
        assertNotNull(validateSubscriptionRequest.getSubscribers()
                .get(0));
        assertNotNull(validateSubscriptionRequest.getSubscribers()
                .get(0)
                .getIncome());
    }

    @Test
    @DisplayName("Should map Expense ")
    void expense(@Random LoanDemand loanDemand) {
        ValidateSubscriptionRequest validateSubscriptionRequest = validateSubscriptionRequestMapper.toLoanProviderRequest(loanDemand);
        assertNotNull(validateSubscriptionRequest.getSubscribers());
        assertNotNull(validateSubscriptionRequest.getSubscribers()
                .get(0));
        assertNotNull(validateSubscriptionRequest.getSubscribers()
                .get(0)
                .getExpense());
    }

    @Test
    @DisplayName("Should map personalDetails.address.salutationAsMaleFemale to Subscriber.income")
    void gender(@Random LoanDemand loanDemand) {
        ValidateSubscriptionRequest validateSubscriptionRequest = validateSubscriptionRequestMapper.toLoanProviderRequest(loanDemand);
        assertNotNull(validateSubscriptionRequest.getSubscribers());
        assertNotNull(validateSubscriptionRequest.getSubscribers()
                .get(0));
        assertNotNull(validateSubscriptionRequest.getSubscribers()
                .get(0)
                .getGender());
        assertEquals(loanDemand.getPersonalDetails()
                        .getGender()
                        .name()
                , validateSubscriptionRequest.getSubscribers()
                        .get(0)
                        .getGender()
                        .toString());

    }

    @Test
    @DisplayName("Should map academicTitle  to null")
    void academicTitle(@Random LoanDemand loanDemand) {
        ValidateSubscriptionRequest validateSubscriptionRequest = validateSubscriptionRequestMapper.toLoanProviderRequest(loanDemand);
        assertNotNull(validateSubscriptionRequest.getSubscribers());
        assertNotNull(validateSubscriptionRequest.getSubscribers()
                .get(0));
        assertNull(validateSubscriptionRequest.getSubscribers()
                .get(0)
                .getAcademicTitle());
    }

    @Test
    @DisplayName("Should map firstName  to forename")
    void firstName(@Random LoanDemand loanDemand) {
        ValidateSubscriptionRequest validateSubscriptionRequest = validateSubscriptionRequestMapper.toLoanProviderRequest(loanDemand);
        assertNotNull(validateSubscriptionRequest.getSubscribers());
        assertNotNull(validateSubscriptionRequest.getSubscribers()
                .get(0));
        assertNotNull(validateSubscriptionRequest.getSubscribers()
                .get(0)
                .getFirstName());
        assertEquals(loanDemand.getPersonalDetails()
                .getFirstName(), validateSubscriptionRequest.getSubscribers()
                .get(0)
                .getFirstName());
    }

    @Test
    @DisplayName("Should map lastName to Surname")
    void lastName(@Random LoanDemand loanDemand) {
        ValidateSubscriptionRequest validateSubscriptionRequest = validateSubscriptionRequestMapper.toLoanProviderRequest(loanDemand);
        assertNotNull(validateSubscriptionRequest.getSubscribers());
        assertNotNull(validateSubscriptionRequest.getSubscribers()
                .get(0));
        assertNotNull(validateSubscriptionRequest.getSubscribers()
                .get(0)
                .getLastName());
        assertEquals(loanDemand.getPersonalDetails()
                .getLastName(), validateSubscriptionRequest.getSubscribers()
                .get(0)
                .getLastName());
    }

    @Test
    @DisplayName("Should map nobilityTitle  to null")
    void nobilityTitle(@Random LoanDemand loanDemand) {
        ValidateSubscriptionRequest validateSubscriptionRequest = validateSubscriptionRequestMapper.toLoanProviderRequest(loanDemand);
        assertNotNull(validateSubscriptionRequest.getSubscribers());
        assertNotNull(validateSubscriptionRequest.getSubscribers()
                .get(0));
        assertNull(validateSubscriptionRequest.getSubscribers()
                .get(0)
                .getNobilityTitle());
    }

    @Test
    @DisplayName("Should map bithDate  to dateOfBirth ")
    void dateOfBirth(@Random LoanDemand loanDemand) {
        ValidateSubscriptionRequest validateSubscriptionRequest = validateSubscriptionRequestMapper.toLoanProviderRequest(loanDemand);
        assertNotNull(validateSubscriptionRequest.getSubscribers());
        assertNotNull(validateSubscriptionRequest.getSubscribers()
                .get(0));
        assertNotNull(validateSubscriptionRequest.getSubscribers()
                .get(0)
                .getDateOfBirth());
        assertEquals(loanDemand.getPersonalDetails()
                .getBirthDate(), validateSubscriptionRequest.getSubscribers()
                .get(0)
                .getDateOfBirth());
    }

    @Test
    @DisplayName("Should birth Name to null ")
    void birthName(@Random LoanDemand loanDemand) {
        ValidateSubscriptionRequest validateSubscriptionRequest = validateSubscriptionRequestMapper.toLoanProviderRequest(loanDemand);
        assertNotNull(validateSubscriptionRequest.getSubscribers());
        assertNotNull(validateSubscriptionRequest.getSubscribers()
                .get(0));
        assertNull(validateSubscriptionRequest.getSubscribers()
                .get(0)
                .getBirthName());
    }

    @Test
    @DisplayName("Should map nationality to 276 ")
    void nationality(@Random LoanDemand loanDemand) {
        loanDemand.getPersonalDetails()
                .setNationality(Nationality.DE);
        ValidateSubscriptionRequest validateSubscriptionRequest = validateSubscriptionRequestMapper.toLoanProviderRequest(loanDemand);
        assertNotNull(validateSubscriptionRequest.getSubscribers());
        assertNotNull(validateSubscriptionRequest.getSubscribers()
                .get(0));
        assertNotNull(validateSubscriptionRequest.getSubscribers()
                .get(0)
                .getNationality());
        assertEquals("276", validateSubscriptionRequest.getSubscribers()
                .get(0)
                .getNationality());
    }

    @Test
    @DisplayName("Should map countryOfBirth to 276 ")
    void countryOfBirth(@Random LoanDemand loanDemand) {
        ValidateSubscriptionRequest validateSubscriptionRequest = validateSubscriptionRequestMapper.toLoanProviderRequest(loanDemand);
        assertNotNull(validateSubscriptionRequest.getSubscribers());
        assertNotNull(validateSubscriptionRequest.getSubscribers()
                .get(0));
        assertNotNull(validateSubscriptionRequest.getSubscribers()
                .get(0)
                .getCountryOfBirth());
        assertEquals("276", validateSubscriptionRequest.getSubscribers()
                .get(0)
                .getCountryOfBirth());
    }

    @Test
    void familySituation(@Random LoanDemand loanDemand) {
        loanDemand.getPersonalDetails()
                .setFamilyStatus(FamilyStatus.SINGLE);

        ValidateSubscriptionRequest validateSubscriptionRequest = validateSubscriptionRequestMapper.toLoanProviderRequest(loanDemand);
        FamilySituation familySituation = validateSubscriptionRequest.getSubscribers()
                .get(0)
                .getFamilySituation();
        assertNotNull(familySituation);
        assertEquals(FamilySituation.FREE, familySituation);
    }

    @Nested
    class IgnoredMappingFields {

        @Test
        @DisplayName("Should map consents to null ")
        void consents(@Random LoanDemand loanDemand) {
            ValidateSubscriptionRequest validateSubscriptionRequest = validateSubscriptionRequestMapper.toLoanProviderRequest(loanDemand);
            assertNotNull(validateSubscriptionRequest.getSubscribers());
            assertNotNull(validateSubscriptionRequest.getSubscribers()
                    .get(0));
            assertNotNull(validateSubscriptionRequest.getSubscribers()
                    .get(0)
                    .getConsents());
        }

        @Test
        @DisplayName("Should map identity to null ")
        void identity(@Random LoanDemand loanDemand) {
            ValidateSubscriptionRequest validateSubscriptionRequest = validateSubscriptionRequestMapper.toLoanProviderRequest(loanDemand);
            assertNotNull(validateSubscriptionRequest.getSubscribers());
            assertNotNull(validateSubscriptionRequest.getSubscribers()
                    .get(0));
            assertNull(validateSubscriptionRequest.getSubscribers()
                    .get(0)
                    .getIdentity());
        }

        @Test
        @DisplayName("Should map interestCap to null ")
        void interestCap(@Random LoanDemand loanDemand) {
            ValidateSubscriptionRequest validateSubscriptionRequest = validateSubscriptionRequestMapper.toLoanProviderRequest(loanDemand);
            assertNotNull(validateSubscriptionRequest.getSubscribers());
            assertNotNull(validateSubscriptionRequest.getSubscribers()
                    .get(0));
            assertNull(validateSubscriptionRequest.getSubscribers()
                    .get(0)
                    .getInterestCap());
        }


        @Test
        @DisplayName("Should map housingSituation ")
        void housingSituation(@Random LoanDemand loanDemand) {
            ValidateSubscriptionRequest validateSubscriptionRequest = validateSubscriptionRequestMapper.toLoanProviderRequest(loanDemand);
            assertNotNull(validateSubscriptionRequest.getSubscribers());
            assertNotNull(validateSubscriptionRequest.getSubscribers()
                    .get(0));
            assertNotNull(validateSubscriptionRequest.getSubscribers()
                    .get(0)
                    .getHousingSituation());

        }

        @Test
        @DisplayName("Should map subscriberIdentifierExternal to null ")
        void subscriberIdentifierExternal(@Random LoanDemand loanDemand) {
            ValidateSubscriptionRequest validateSubscriptionRequest = validateSubscriptionRequestMapper.toLoanProviderRequest(loanDemand);
            assertNotNull(validateSubscriptionRequest.getSubscribers());
            assertNotNull(validateSubscriptionRequest.getSubscribers()
                    .get(0));
            assertNull(validateSubscriptionRequest.getSubscribers()
                    .get(0)
                    .getSubscriberIdentifierExternal());
        }

        @Test
        @DisplayName("Should map subscriberIdentifierInternal to null ")
        void subscriberIdentifierInternal(@Random LoanDemand loanDemand) {
            ValidateSubscriptionRequest validateSubscriptionRequest = validateSubscriptionRequestMapper.toLoanProviderRequest(loanDemand);
            assertNotNull(validateSubscriptionRequest.getSubscribers());
            assertNotNull(validateSubscriptionRequest.getSubscribers()
                    .get(0));
            assertNull(validateSubscriptionRequest.getSubscribers()
                    .get(0)
                    .getSubscriberIdentifierInternal());
        }

        @Test
        @DisplayName("Should map financialLimit to null ")
        void financialLimit(@Random LoanDemand loanDemand) {
            ValidateSubscriptionRequest validateSubscriptionRequest = validateSubscriptionRequestMapper.toLoanProviderRequest(loanDemand);
            assertNotNull(validateSubscriptionRequest.getSubscribers());
            assertNotNull(validateSubscriptionRequest.getSubscribers()
                    .get(0));
        }

        @Test
        @DisplayName("Should map customerRating to null ")
        void customerRating(@Random LoanDemand loanDemand) {
            ValidateSubscriptionRequest validateSubscriptionRequest = validateSubscriptionRequestMapper.toLoanProviderRequest(loanDemand);
            assertNotNull(validateSubscriptionRequest.getSubscribers());
            assertNotNull(validateSubscriptionRequest.getSubscribers()
                    .get(0));
            assertNull(validateSubscriptionRequest.getSubscribers()
                    .get(0)
                    .getCustomerRating());
        }
    }


}
