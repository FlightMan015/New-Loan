package de.joonko.loan.partner.santander.mapper;

import de.joonko.loan.offer.domain.HousingType;
import de.joonko.loan.offer.domain.PersonalDetails;
import de.joonko.loan.partner.auxmoney.getoffers.BaseMapperTest;
import de.joonko.loan.partner.santander.stub.ScbCapsBcoWSStub;
import io.github.glytching.junit.extension.random.Random;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static de.joonko.loan.offer.domain.HousingType.OWNER;
import static de.joonko.loan.offer.domain.HousingType.RENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class SantanderGetOfferIncomeExpenseRequestMapperTest extends BaseMapperTest {

    @Autowired
    SantanderGetOfferIncomeExpenseRequestMapper mapper;


    @Nested
    class Income {
        @Test
        void net_income(@Random PersonalDetails personalDetails) {
            ScbCapsBcoWSStub.EinnahmenAusgabenXO einnahmenAusgabenXO = mapper.toIncomesAndExpenses(personalDetails);
            assertEquals(personalDetails.getFinance().getIncome().getNetIncome(), einnahmenAusgabenXO.getNettoEinkommen());
        }

        @Test
        void child_benefit(@Random PersonalDetails personalDetails) {
            ScbCapsBcoWSStub.EinnahmenAusgabenXO einnahmenAusgabenXO = mapper.toIncomesAndExpenses(personalDetails);
            assertEquals(personalDetails.getFinance().getIncome().getChildBenefits(), einnahmenAusgabenXO.getKindergeld());
        }

        @Test
        void pension_benefit(@Random PersonalDetails personalDetails) {
            ScbCapsBcoWSStub.EinnahmenAusgabenXO einnahmenAusgabenXO = mapper.toIncomesAndExpenses(personalDetails);
            assertEquals(personalDetails.getFinance().getIncome().getPensionBenefits(), einnahmenAusgabenXO.getRentenbezuege());
        }

        @Test
        void other_income(@Random PersonalDetails personalDetails) {
            ScbCapsBcoWSStub.EinnahmenAusgabenXO einnahmenAusgabenXO = mapper.toIncomesAndExpenses(personalDetails);
            assertEquals(personalDetails.getFinance().getIncome().getOtherRevenue(), einnahmenAusgabenXO.getSonstigeEinnahmen());
        }

        @Test
        void rental_income(@Random PersonalDetails personalDetails) {
            ScbCapsBcoWSStub.EinnahmenAusgabenXO einnahmenAusgabenXO = mapper.toIncomesAndExpenses(personalDetails);
            assertEquals(personalDetails.getFinance().getIncome().getRentalIncome(), einnahmenAusgabenXO.getMietEinnahmen());
        }

        @Test
        void alimony_payments(@Random PersonalDetails personalDetails) {
            ScbCapsBcoWSStub.EinnahmenAusgabenXO einnahmenAusgabenXO = mapper.toIncomesAndExpenses(personalDetails);
            assertEquals(personalDetails.getFinance().getIncome().getAlimonyPayments(), einnahmenAusgabenXO.getUnterhaltEingang());
        }
    }

    @Nested
    class Expenses {

        @Test
        void mortagages_when_0(@Random PersonalDetails personalDetails) {
            // given
            personalDetails.getFinance().getExpenses().setMortgages(BigDecimal.ZERO);

            // when
            ScbCapsBcoWSStub.EinnahmenAusgabenXO einnahmenAusgabenXO = mapper.toIncomesAndExpenses(personalDetails);

            // then
            assertNull(einnahmenAusgabenXO.getHypothek());
        }

        @Test
        void mortagages_when_null(@Random PersonalDetails personalDetails) {
            // given
            personalDetails.getFinance().getExpenses().setMortgages(null);

            // when
            ScbCapsBcoWSStub.EinnahmenAusgabenXO einnahmenAusgabenXO = mapper.toIncomesAndExpenses(personalDetails);

            // then
            assertNull(einnahmenAusgabenXO.getHypothek());
        }

        @Test
        void mortagages_when_exist(@Random PersonalDetails personalDetails) {
            // given
            personalDetails.getFinance().getExpenses().setMortgages(BigDecimal.TEN);

            // when
            ScbCapsBcoWSStub.EinnahmenAusgabenXO einnahmenAusgabenXO = mapper.toIncomesAndExpenses(personalDetails);

            // then
            assertEquals(personalDetails.getFinance().getExpenses().getMortgages(), einnahmenAusgabenXO.getHypothek());
        }

        @Test
        void rent_when_user_is_renter(@Random PersonalDetails personalDetails) {
            // given
            personalDetails.setHousingType(HousingType.RENT);
            personalDetails.getFinance().getExpenses().setAcknowledgedRent(BigDecimal.TEN);

            // when
            ScbCapsBcoWSStub.EinnahmenAusgabenXO einnahmenAusgabenXO = mapper.toIncomesAndExpenses(personalDetails);

            // then
            assertEquals(personalDetails.getFinance().getExpenses().getAcknowledgedRent(), einnahmenAusgabenXO.getWarmmiete());
        }

        @Test
        void rent_when_user_is_not_renter(@Random PersonalDetails personalDetails) {
            // given
            personalDetails.setHousingType(HousingType.OWNER);
            personalDetails.getFinance().getExpenses().setAcknowledgedRent(BigDecimal.TEN);

            // when
            ScbCapsBcoWSStub.EinnahmenAusgabenXO einnahmenAusgabenXO = mapper.toIncomesAndExpenses(personalDetails);

            // then
            assertNull(einnahmenAusgabenXO.getWarmmiete());
        }

        @Test
        void insurance_and_savings(@Random PersonalDetails personalDetails) {
            ScbCapsBcoWSStub.EinnahmenAusgabenXO einnahmenAusgabenXO = mapper.toIncomesAndExpenses(personalDetails);
            assertNull(einnahmenAusgabenXO.getLebensversicherung());
        }

        @Test
        void alimony(@Random PersonalDetails personalDetails) {
            ScbCapsBcoWSStub.EinnahmenAusgabenXO einnahmenAusgabenXO = mapper.toIncomesAndExpenses(personalDetails);
            assertEquals(personalDetails.getFinance().getExpenses().getAlimony(), einnahmenAusgabenXO.getUnterhaltZahlung());
        }

        @Test
        void toHousingType_willMapRent(@Random PersonalDetails personalDetails) {
            // given
            personalDetails.setHousingType(RENT);

            // when
            final var mapped = mapper.toIncomesAndExpenses(personalDetails);

            // then
            assertEquals(ScbCapsBcoWSStub.WohnartType.MIETWOHNUNG, mapped.getWohnart());
        }

        @Test
        void toHousingType_willMapOtherToNull(@Random PersonalDetails personalDetails) {
            // given
            personalDetails.setHousingType(OWNER);

            // when
            final var mapped = mapper.toIncomesAndExpenses(personalDetails);

            // then
            assertNull(mapped.getWohnart());
        }
    }
}