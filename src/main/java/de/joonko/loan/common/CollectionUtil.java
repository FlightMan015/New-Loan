package de.joonko.loan.common;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public class CollectionUtil {

    public static <T, S> Function<Collection<T>, List<S>> mapList(Function<T, S> mapFunction) {
        return list -> list.stream().map(mapFunction).collect(toList());
    }

    public static <T, S> Function<Collection<T>, Set<S>> mapSet(Function<T, S> mapFunction) {
        return set -> set.stream().map(mapFunction).collect(toSet());
    }

    public static <T, S> Function<Collection<T>, Stream<S>> mapStream(Function<T, S> mapFunction) {
        return collection -> collection.stream().map(mapFunction);
    }
}
