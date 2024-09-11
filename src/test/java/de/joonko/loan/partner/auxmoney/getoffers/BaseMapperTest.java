package de.joonko.loan.partner.auxmoney.getoffers;


import io.github.benas.randombeans.EnhancedRandomBuilder;
import io.github.benas.randombeans.api.EnhancedRandom;
import io.github.benas.randombeans.randomizers.range.BigDecimalRangeRandomizer;
import io.github.benas.randombeans.randomizers.range.IntegerRangeRandomizer;
import io.github.benas.randombeans.randomizers.range.LongRangeRandomizer;
import io.github.glytching.junit.extension.random.RandomBeansExtension;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;


@ActiveProfiles("integration")
@SpringBootTest
public abstract class BaseMapperTest {

    static EnhancedRandom enhancedRandom = EnhancedRandomBuilder
            .aNewEnhancedRandomBuilder()
            .randomize(Integer.class, IntegerRangeRandomizer.aNewIntegerRangeRandomizer(0, 1000))
            .randomize(Long.class, LongRangeRandomizer.aNewLongRangeRandomizer(0L, 1000L))
            .stringLengthRange(25, 30)
            .collectionSizeRange(1, 1)
            .randomize(BigDecimal.class, BigDecimalRangeRandomizer.aNewBigDecimalRangeRandomizer(0L, 1000L))
            .build();

    @RegisterExtension
    static RandomBeansExtension randomBeansExtension = new RandomBeansExtension(enhancedRandom);
}
