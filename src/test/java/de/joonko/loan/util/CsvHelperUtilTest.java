package de.joonko.loan.util;

import de.joonko.loan.reporting.api.model.GetOffersStatusResponse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.core.io.InputStreamResource;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;
import lombok.SneakyThrows;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(RandomBeansExtension.class)
class CsvHelperUtilTest {

    @Test
    @SneakyThrows
    void constructCsvWithObjects(@Random GetOffersStatusResponse getOffersStatusResponse) {
        // given
        final var headers = List.of("User ID", "Distribution Channel", "Loan requested amount", "Loan purpose",
                "Loan provider", "amountEnteredAt", "bankConnectedAt", "personalDataEnteredAt", "offersReceivedAt", "offerAcceptedAt",
                "kycLastUpdatedAt", "Kyc status", "Current loan status", "offerLastStatusUpdatedAt");

        // when
        InputStreamResource inputStreamResource = CsvHelperUtil.constructCsv(getOffersStatusResponse.getOffers());

        // then
        assertNotNull(inputStreamResource);

        List<String> output = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStreamResource.getInputStream()));

        while (reader.ready()) {
            String line = reader.readLine();
            output.add(line);
        }

        assertAll(
                () -> assertEquals(getOffersStatusResponse.getOffers().size() + 1, output.size()),
                () -> assertTrue(Arrays.asList(output.get(0).split(",")).containsAll(headers))
        );
    }

}