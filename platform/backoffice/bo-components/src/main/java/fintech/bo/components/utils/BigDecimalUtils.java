package fintech.bo.components.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.BinaryOperator;

public abstract class BigDecimalUtils {

    public static boolean eq(BigDecimal value1, BigDecimal value2) {
        boolean isEqual = value1.compareTo(value2) == 0;
        return isEqual;
    }

    public static boolean loe(BigDecimal left, BigDecimal right) {
        int compareTo = left.compareTo(right);
        return compareTo <= 0;
    }

    public static boolean goe(BigDecimal left, BigDecimal right) {
        int compareTo = left.compareTo(right);
        return compareTo >= 0;
    }

    public static boolean gt(BigDecimal left, BigDecimal right) {
        int compareTo = left.compareTo(right);
        return compareTo > 0;
    }

    public static boolean lt(BigDecimal left, BigDecimal right) {
        int compareTo = left.compareTo(right);
        return compareTo < 0;
    }

    public static boolean isZero(BigDecimal value) {
        return eq(value, BigDecimal.ZERO);
    }

    public static boolean isPositive(BigDecimal value) {
        return gt(value, BigDecimal.ZERO);
    }

    public static boolean isNegative(BigDecimal value) {
        return lt(value, BigDecimal.ZERO);
    }

    public static BigDecimal amount(long value) {
        return amount(new BigDecimal(value));
    }


    public static BigDecimal amount(double value) {
        return amount(new BigDecimal(value));
    }

    public static BigDecimal amount(String value) {
        return amount(new BigDecimal(value));
    }

    public static BigDecimal amount(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_EVEN);
    }

    public static BinaryOperator<BigDecimal> sum() {
        return (a, b) -> a.add(b);
    }

    public static BigDecimal max(BigDecimal left, BigDecimal right) {
        return gt(left, right) ? left : right;
    }

    public static BigDecimal min(BigDecimal left, BigDecimal right) {
        return lt(left, right) ? left : right;
    }

}
