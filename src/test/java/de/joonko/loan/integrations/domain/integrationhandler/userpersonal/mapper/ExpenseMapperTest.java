package de.joonko.loan.integrations.domain.integrationhandler.userpersonal.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static de.joonko.loan.offer.domain.ExpenseModel.expense;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExpenseMapperTest {

    private ExpenseMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ExpenseMapperImpl();
    }

    @Test
    void map_maps_from_api_model_to_domain_correctly() {
        final var domainExpense = expense();

        final var apiExpense = mapper.map(domainExpense);
        final var domainExpenseMappedBack = mapper.map(apiExpense);
        assertAll (
                () -> assertEquals(domainExpense.getMortgages().doubleValue(), apiExpense.getMortgages()),
                () -> assertEquals(domainExpense.getRent().doubleValue(), apiExpense.getRent()),
                () -> assertEquals(domainExpense.getAlimony().doubleValue(), apiExpense.getAlimony()),
                () -> assertEquals(domainExpense.getInsuranceAndSavings().doubleValue(), apiExpense.getInsuranceAndSavings()),
                () -> assertEquals(domainExpense.getPrivateHealthInsurance().doubleValue(), apiExpense.getPrivateHealthInsurance()),
                () -> assertEquals(domainExpense.getVehicleInsurance().doubleValue(), apiExpense.getVehicleInsurance()),
                () -> assertEquals(domainExpense.getLoanInstalments().doubleValue(), apiExpense.getLoanInstalments())
        );

        assertAll (
                () -> assertEquals(domainExpense.getMortgages().doubleValue(), domainExpenseMappedBack.getMortgages().doubleValue()),
                () -> assertEquals(domainExpense.getRent().doubleValue(), domainExpenseMappedBack.getRent().doubleValue()),
                () -> assertEquals(domainExpense.getAlimony().doubleValue(), domainExpenseMappedBack.getAlimony().doubleValue()),
                () -> assertEquals(domainExpense.getInsuranceAndSavings().doubleValue(), domainExpenseMappedBack.getInsuranceAndSavings().doubleValue()),
                () -> assertEquals(domainExpense.getPrivateHealthInsurance().doubleValue(), domainExpenseMappedBack.getPrivateHealthInsurance().doubleValue()),
                () -> assertEquals(domainExpense.getVehicleInsurance().doubleValue(), domainExpenseMappedBack.getVehicleInsurance().doubleValue()),
                () -> assertEquals(domainExpense.getLoanInstalments().doubleValue(), domainExpenseMappedBack.getLoanInstalments().doubleValue())
        );
    }

}
