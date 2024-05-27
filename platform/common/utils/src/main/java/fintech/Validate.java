package fintech;


import java.math.BigDecimal;

public class Validate extends org.apache.commons.lang3.Validate {

    public static void isPositive(final BigDecimal value, final String message, final Object... values) {
        if (!BigDecimalUtils.gt(value, BigDecimal.ZERO)) {
            throw new IllegalArgumentException(String.format(message, values));
        }
    }

    public static void isNegative(final BigDecimal value, final String message, final Object... values) {
        if (!BigDecimalUtils.lt(value, BigDecimal.ZERO)) {
            throw new IllegalArgumentException(String.format(message, values));
        }
    }

    public static void isZeroOrPositive(final BigDecimal value, final String message, final Object... values) {
        if (BigDecimalUtils.lt(value, BigDecimal.ZERO)) {
            throw new IllegalArgumentException(String.format(message, values));
        }
    }

    public static void isZeroOrNegative(final BigDecimal value, final String message, final Object... values) {
        if (BigDecimalUtils.gt(value, BigDecimal.ZERO)) {
            throw new IllegalArgumentException(String.format(message, values));
        }
    }

    public static void isZero(final BigDecimal value, final String message, final Object... values) {
        if (!BigDecimalUtils.isZero(value)) {
            throw new IllegalArgumentException(String.format(message, values));
        }
    }

    public static void isNotZero(final BigDecimal value, final String message, final Object... values) {
        if (BigDecimalUtils.isZero(value)) {
            throw new IllegalArgumentException(String.format(message, values));
        }
    }

    public static void isEqual(final BigDecimal first, final BigDecimal second, final String message, final Object... values) {
        if (!BigDecimalUtils.eq(first, second)) {
            throw new IllegalArgumentException(String.format(message, errorMessagesArgs(first, second, values)));
        }
    }

    public static void isLoe(final BigDecimal first, final BigDecimal second, final String message, final Object... values) {
        if (!BigDecimalUtils.loe(first, second)) {
            throw new IllegalArgumentException(String.format(message, errorMessagesArgs(first, second, values)));
        }
    }

    public static void isGoe(final BigDecimal first, final BigDecimal second, final String message, final Object... values) {
        if (!BigDecimalUtils.goe(first, second)) {
            throw new IllegalArgumentException(String.format(message, errorMessagesArgs(first, second, values)));
        }
    }

    private static Object[] errorMessagesArgs(final BigDecimal first, final BigDecimal second, final Object... values) {
        return values.length > 0 ? values : new Object[] { first, second };
    }

}
