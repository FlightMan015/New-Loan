package de.joonko.loan.util;

import java.util.function.IntPredicate;
import java.util.function.Predicate;

public class AuxmoneyConstant {
    private AuxmoneyConstant() {

    }

    private static final int MINIMUM_LOAN_AMOUNT = 1000;
    private static final int MAX_LOAN_AMOUNT = 50000;
    public static final IntPredicate MINIMUM_LOAN_AMOUNT_PREDICATE = i -> i >= MINIMUM_LOAN_AMOUNT;
    public static final IntPredicate MAX_LOAN_AMOUNT_PREDICATE = i -> i < MAX_LOAN_AMOUNT;
    public static final IntPredicate INCREMENTS_OF_100 = i -> (i % 100) == 0;
    public static final Predicate<String> AUXMONEY_SUCCESS_RESPONSE = str -> str.equalsIgnoreCase("true");

}
