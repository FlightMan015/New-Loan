package de.joonko.loan.integrations.domain.integrationhandler.userpersonal.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static de.joonko.loan.offer.domain.IncomeModel.income;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class IncomeMapperTest {

    private IncomeMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new IncomeMapperImpl();
    }

    @Test
    void map_maps_from_domain_model_to_api_model_and_back_correctly() {
        final var domainIncome = income();

        final var apiIncome = mapper.map(domainIncome);
        final var domainIncomeMappedBack = mapper.map(apiIncome);
        assertAll (
                () -> assertEquals(domainIncome.getNetIncome().doubleValue(), apiIncome.getNetIncome()),
                () -> assertEquals(domainIncome.getRentalIncome().doubleValue(), apiIncome.getRentalIncome()),
                () -> assertEquals(domainIncome.getOtherRevenue().doubleValue(), apiIncome.getOtherRevenue()),
                () -> assertEquals(domainIncome.getPensionBenefits().doubleValue(), apiIncome.getPensionBenefits()),
                () -> assertEquals(domainIncome.getChildBenefits().doubleValue(), apiIncome.getChildBenefits()),
                () -> assertEquals(domainIncome.getAlimonyPayments().doubleValue(), apiIncome.getAlimonyPayments())
        );

        assertAll (
                () -> assertEquals(domainIncome.getNetIncome().doubleValue(), domainIncomeMappedBack.getNetIncome().doubleValue()),
                () -> assertEquals(domainIncome.getRentalIncome().doubleValue(), domainIncomeMappedBack.getRentalIncome().doubleValue()),
                () -> assertEquals(domainIncome.getOtherRevenue().doubleValue(), domainIncomeMappedBack.getOtherRevenue().doubleValue()),
                () -> assertEquals(domainIncome.getPensionBenefits().doubleValue(), domainIncomeMappedBack.getPensionBenefits().doubleValue()),
                () -> assertEquals(domainIncome.getChildBenefits().doubleValue(), domainIncomeMappedBack.getChildBenefits().doubleValue()),
                () -> assertEquals(domainIncome.getAlimonyPayments().doubleValue(), domainIncomeMappedBack.getAlimonyPayments().doubleValue())
        );
    }
}
