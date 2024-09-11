package de.joonko.loan.offer.api;

import de.joonko.loan.avro.dto.dac.DigitalAccountStatement;
import de.joonko.loan.partner.auxmoney.getoffers.BaseMapperTest;
import io.github.glytching.junit.extension.random.Random;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AccountDetailsMapperTest extends BaseMapperTest {

    @Autowired
    private AccountDetailsMapper mapper;

    @Test
    void mapToAccountDetails(@Random DigitalAccountStatement digitalAccountStatement) {
        // given
        digitalAccountStatement.setCreatedAt("2022-03-08T00:00:00.000");
        digitalAccountStatement.setBalanceDate("2022-03-08");
        digitalAccountStatement.setTransactions(List.of());

        // when
        final var actualAccountDetails = mapper.toAccountDetails(digitalAccountStatement);

        // then
        assertAll(
                () -> assertEquals(digitalAccountStatement.getBankAccountName(), actualAccountDetails.getNameOnAccount()),
                () -> assertEquals(LocalDate.of(2022, 3, 8), actualAccountDetails.getBalanceDate()),
                () -> assertEquals(digitalAccountStatement.getIban(), actualAccountDetails.getIban()),
                () -> assertEquals(digitalAccountStatement.getBic(), actualAccountDetails.getBic()),
                () -> assertEquals(digitalAccountStatement.getBalance(), actualAccountDetails.getBalance()),
                () -> assertEquals(digitalAccountStatement.getLimit(), actualAccountDetails.getLimit()),
                () -> assertEquals(digitalAccountStatement.getCurrency(), actualAccountDetails.getCurrency()),
                () -> assertEquals(digitalAccountStatement.getTransactions(), actualAccountDetails.getTransactions()),
                () -> assertEquals(LocalDateTime.of(2022, 3, 8, 0, 0), actualAccountDetails.getCreatedAt()),
                () -> assertEquals(digitalAccountStatement.getIsJointlyManaged(), actualAccountDetails.getIsJointlyManaged()),
                () -> assertEquals(digitalAccountStatement.getBankName(), actualAccountDetails.getBankName())
        );
    }
}
