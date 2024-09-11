package de.joonko.loan.offer.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PersonalDetailTopicTest {
    @Nested
    class RealEstate {
        @DisplayName("Should return false if do not have rental income and housingType is renter")
        @Test
        void hasRealEstate() {
            PersonalDetails personalDetail = PersonalDetails.builder()
                    .housingType(HousingType.RENT)
                    .finance(new Finance(Income.builder()
                            .rentalIncome(BigDecimal.ZERO)
                            .build(),
                            Expenses.builder().build(),
                            BigDecimal.TEN))
                    .build();
            assertFalse(personalDetail.hasRealEstate());
        }

        @DisplayName("Should return true if  rental income is more than 0 euro")
        @Test
        void hasRentalIncome() {
            PersonalDetails personalDetail = PersonalDetails.builder()
                    .housingType(HousingType.RENT)
                    .finance(new Finance(Income.builder()
                            .rentalIncome(BigDecimal.ONE)
                            .build(), Expenses.builder()
                            .build()
                            , BigDecimal.TEN))
                    .build();
            assertTrue(personalDetail.hasRealEstate());
        }

        @DisplayName("Should return true if  rental income is not more 0 euro but housing type is owner  ")
        @Test
        void housingTypeIsOwner() {
            PersonalDetails personalDetail = PersonalDetails.builder()
                    .housingType(HousingType.OWNER)
                    .finance(new Finance(Income.builder()
                            .rentalIncome(BigDecimal.ZERO)
                            .build(), Expenses.builder()
                            .build(), BigDecimal.TEN))
                    .build();
            assertTrue(personalDetail.hasRealEstate());
        }

    }
}
