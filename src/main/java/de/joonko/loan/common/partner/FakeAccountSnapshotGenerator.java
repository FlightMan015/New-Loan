package de.joonko.loan.common.partner;

import de.joonko.loan.partner.solaris.model.Account;
import de.joonko.loan.partner.solaris.model.AccountSnapshot;
import de.joonko.loan.partner.solaris.model.Balance;
import de.joonko.loan.partner.solaris.model.Turnovers;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
public class FakeAccountSnapshotGenerator {

    private static Turnovers buildTurnover(int count) {
        ZonedDateTime zonedDateTime = LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault());
        return Turnovers.builder().purpose(List.of("SEPA-GEHALTSGUTSCHRIFT FinTecSystems GmbH LOHN / GEHALT 09/16 KUNDENREFERENZ 7226505940-0 001008LG0000"))
                .amount(Double.valueOf("3000"))
                .bookingDate(Date.from(zonedDateTime.minusDays(zonedDateTime.getDayOfMonth() - 1).minusMonths(count).toInstant()))
                .counterBic("")
                .counterHolder("FinTecSystems GmbH")
                .counterIban("")
                .currency("EUR")
                .preBooked(false)
                .tags(List.of("income", "revenue"))
                .build();
    }


    public static AccountSnapshot getFakeSolarisSnapshot() {
        List<Turnovers> turnovers = new ArrayList();
        for (int i = 9; i >= 0; i--) {
            turnovers.add(buildTurnover(i));
        }

        return AccountSnapshot.builder().account(Account.builder().bankName("TestBank")
                .bic("TESTDE88XXX")
                .countryId("DE")
                .description("Girokonto")
                .holder("MUSTERMANN, HARTMUT")
                .iban("DE92370601930002130041")
                .jointAccount(false)
                .build())
                .turnovers(turnovers)
                .balance(Balance.builder().available(Double.valueOf("2123"))
                        .currency("EUR")
                        .balance(Double.valueOf("20000"))
                        .limit(Double.valueOf("10000"))
                        .date(new Date())
                        .available(Double.valueOf("8000"))
                        .build())
                .days(365)
                .date(new Date())
                .build();
    }
}
