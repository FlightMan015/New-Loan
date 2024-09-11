package de.joonko.loan.partner.swk.mapper;

import de.joonko.loan.offer.domain.DomainDefault;
import de.joonko.loan.offer.domain.Gender;
import de.joonko.loan.offer.domain.LoanDemand;
import de.joonko.loan.partner.auxmoney.getoffers.BaseMapperTest;
import de.joonko.loan.partner.swk.SwkDefaults;
import de.joonko.loan.partner.swk.stub.CreditApplicationServiceStub;
import io.github.glytching.junit.extension.random.Random;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class SwkApplicationRequestMapperTest extends BaseMapperTest {

    @Autowired
    SwkApplicationRequestMapper applicationRequestMapper;

    @Random
    private LoanDemand loanDemand;


    @Test
    void clientIdentification() {
        CreditApplicationServiceStub.ApplicationRequest applicationRequest = applicationRequestMapper.toApplicationRequest(loanDemand);
        assertNotNull(applicationRequest.getClientIdentification());
        assertNotNull(applicationRequest.getClientIdentification()
                .getPartnerId());
    }

    @Test
    void collectionAccount() {
        CreditApplicationServiceStub.ApplicationRequest applicationRequest = applicationRequestMapper.toApplicationRequest(loanDemand);
        assertNotNull(applicationRequest.getCollectionAccount());
        assertNotNull(applicationRequest.getCollectionAccount().getAccountHolder());
        assertNotNull(applicationRequest.getCollectionAccount().getAccountNumber());
    }

    @Test
    void paymentAccount() {
        CreditApplicationServiceStub.ApplicationRequest applicationRequest = applicationRequestMapper.toApplicationRequest(loanDemand);
        assertNotNull(applicationRequest.getPaymentAccount());
        assertNotNull(applicationRequest.getPaymentAccount().getAccountHolder());
        assertNotNull(applicationRequest.getPaymentAccount().getAccountNumber());
    }

    @Test
    void amount() {
        CreditApplicationServiceStub.ApplicationRequest applicationRequest = applicationRequestMapper.toApplicationRequest(loanDemand);
        assertNotNull(applicationRequest.getAmount());
        assertEquals(loanDemand.getLoanAsked(), (int) applicationRequest.getAmount());
    }

    @Test
    void balloonInstallment() {
        CreditApplicationServiceStub.ApplicationRequest applicationRequest = applicationRequestMapper.toApplicationRequest(loanDemand);
        assertNotNull(applicationRequest.getBalloonInstallment());
    }

    @Test
    void calculationMode() {
        CreditApplicationServiceStub.ApplicationRequest applicationRequest = applicationRequestMapper.toApplicationRequest(loanDemand);
        assertNotNull(applicationRequest.getCalculationMode());
    }

    @Test
    void collectionDayOfMonth() {
        CreditApplicationServiceStub.ApplicationRequest applicationRequest = applicationRequestMapper.toApplicationRequest(loanDemand);
        assertEquals(Integer.valueOf(DomainDefault.COLLECTION_DAY_OF_MONTH), applicationRequest.getCollectionDayOfMonth());
    }

    @Test
    void creditUsers() {
        CreditApplicationServiceStub.ApplicationRequest applicationRequest = applicationRequestMapper.toApplicationRequest(loanDemand);
        assertNotNull(applicationRequest.getCreditUsers());
        assertTrue(applicationRequest.getCreditUsers()[0].getAgreedToSchufaRequest());
    }

    @Test
    void currency() {
        CreditApplicationServiceStub.ApplicationRequest applicationRequest = applicationRequestMapper.toApplicationRequest(loanDemand);
        assertEquals("EUR", applicationRequest.getCurrency());
    }

    @Test
    void birthDate() {
        CreditApplicationServiceStub.ApplicationRequest applicationRequest = applicationRequestMapper.toApplicationRequest(loanDemand);
        LocalDate localDateBdate = loanDemand.getPersonalDetails()
                .getBirthDate();
        Calendar calendarBdate = Calendar.getInstance();
        calendarBdate.clear();
        calendarBdate.set(localDateBdate.getYear(), localDateBdate.getMonthValue() - 1, localDateBdate.getDayOfMonth());
        assertEquals(applicationRequest.getDateOfBirth(), calendarBdate);
    }

    @Test
    void disagioPercentage() {
        CreditApplicationServiceStub.ApplicationRequest applicationRequest = applicationRequestMapper.toApplicationRequest(loanDemand);
        assertEquals(0, applicationRequest.getDisagioPercentage());
    }

    @Test
    void duration() {
        CreditApplicationServiceStub.ApplicationRequest applicationRequest = applicationRequestMapper.toApplicationRequest(loanDemand);
        assertEquals(Integer.valueOf(SwkDefaults.SWK_CREDIT_DURATION), applicationRequest.getDuration());
    }

    @Test
    void intendedUse() {
        CreditApplicationServiceStub.ApplicationRequest applicationRequest = applicationRequestMapper.toApplicationRequest(loanDemand);
        assertEquals(Integer.valueOf(SwkDefaults.SONSTIGES), applicationRequest.getIntendedUse());
    }

    @Test
    void extraInfoArray() {
        CreditApplicationServiceStub.ApplicationRequest applicationRequest = applicationRequestMapper.toApplicationRequest(loanDemand);
        assertEquals("transactionId", applicationRequest.getExtraInfo()[0].getName());
        assertEquals(loanDemand.getFtsTransactionId(), applicationRequest.getExtraInfo()[0].getValue());
    }

    @ParameterizedTest(name = "{0} maps to {1}")
    @MethodSource("genderProvider")
    void toGender(Gender gender, String swkMap) {
        loanDemand.getPersonalDetails()
                .setGender(gender);
        CreditApplicationServiceStub.ApplicationRequest applicationRequest = applicationRequestMapper.toApplicationRequest(loanDemand);
        assertEquals(swkMap, applicationRequest.getGender());
    }

    static Stream<Arguments> genderProvider() {
        return Stream.of(
                arguments(Gender.MALE, "M"),
                arguments(Gender.FEMALE, "W")
        );
    }

}
