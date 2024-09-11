package de.joonko.loan.data.support.mapper;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;

import io.github.benas.randombeans.EnhancedRandomBuilder;
import io.github.benas.randombeans.api.EnhancedRandom;
import io.github.benas.randombeans.randomizers.range.BigDecimalRangeRandomizer;
import io.github.benas.randombeans.randomizers.range.IntegerRangeRandomizer;
import io.github.benas.randombeans.randomizers.range.LongRangeRandomizer;
import io.github.glytching.junit.extension.random.RandomBeansExtension;

@ExtendWith(SpringExtension.class)
public abstract class MapstructBaseTest {

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