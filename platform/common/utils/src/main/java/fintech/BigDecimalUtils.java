package fintech;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BinaryOperator;

public abstract class BigDecimalUtils {

    public static final BigDecimal ZERO = BigDecimalUtils.amount(BigDecimal.ZERO);
    private static final BigDecimal HUNDRED = BigDecimalUtils.amount(100);

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

    public static boolean between(BigDecimal value, BigDecimal minExclusive, BigDecimal maxInclusive) {
        int compareToMin = value.compareTo(minExclusive);
        int compareToMax = value.compareTo(maxInclusive);
        return compareToMin > 0 && compareToMax <= 0;
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
        return amount(BigDecimal.valueOf(value));
    }

    public static BigDecimal amount(double value) {
        return amount(BigDecimal.valueOf(value));
    }

    public static BigDecimal amount(String value) {
        return amount(new BigDecimal(value));
    }

    public static BigDecimal safeAmount(String value) {
        if (value != null) {
            return amount(new BigDecimal(value));
        } else {
            return null;
        }
    }

    public static BigDecimal amount(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_EVEN);
    }

    public static AmountForPayment amountForPayment(BigDecimal value) {
        BigDecimal amount = value.setScale(2, RoundingMode.DOWN);
        BigDecimal rounded = value.subtract(amount).setScale(4, RoundingMode.DOWN);
        return new AmountForPayment(amount, rounded);
    }

    public static BinaryOperator<BigDecimal> sum() {
        return BigDecimal::add;
    }

    public static BigDecimal max(BigDecimal left, BigDecimal right) {
        return gt(left, right) ? left : right;
    }

    public static BigDecimal min(BigDecimal left, BigDecimal right) {
        return lt(left, right) ? left : right;
    }

    public static BigDecimal percentageOfAmount(BigDecimal percentage, BigDecimal amount) {
        return amount.multiply(percentage.divide(amount(100)));
    }

    public static BigDecimal abs(BigDecimal amount) {
        return amount.abs();
    }

    public static BigDecimal roundDecimals(BigDecimal amount) {
        return amount.divide(amount(10), 0, RoundingMode.HALF_UP).multiply(amount(10));
    }

    public static BigDecimal roundHundreds(BigDecimal amount) {
        return amount.divide(amount(100), 0, RoundingMode.HALF_UP).multiply(amount(100));
    }

    public static int multiplyByHundred(BigDecimal amount) {
        return amount.multiply(HUNDRED).intValue();
    }

    public static BigDecimal divideByHundred(int amount) {
        return BigDecimalUtils.amount(amount)
            .divide(HUNDRED, 2, RoundingMode.HALF_EVEN);
    }

    public static BigDecimal limit(BigDecimal amount, BigDecimal limit) {
        return lt(amount, limit) ? amount : limit;
    }

    public static BigDecimal normalize(BigDecimal limit, BigDecimal step) {
        BigDecimal normalizedStep = step.setScale(0, BigDecimal.ROUND_HALF_DOWN);
        if (normalizedStep.equals(BigDecimal.ZERO)) {
            return BigDecimal.ZERO;
        }
        BigDecimal normalizedLimit = limit.setScale(0, BigDecimal.ROUND_DOWN);
        return normalizedLimit.subtract(normalizedLimit.remainder(normalizedStep));
    }

    public static List<BigDecimal> divideByPayments(BigDecimal amount, int numberOfPayments) {
        List<BigDecimal> result = new ArrayList<>();
        BigDecimal amountPerItem = amount.divide(BigDecimal.valueOf(numberOfPayments), 2, BigDecimal.ROUND_HALF_DOWN);
        for (int i = 0; i < numberOfPayments - 1; i++) {
            result.add(amountPerItem);
        }
        BigDecimal lastAmount = amount.subtract(amountPerItem.multiply(BigDecimal.valueOf(numberOfPayments - 1)));
        result.add(lastAmount);
        return result;
    }
    public static int calculateMaxPenaltyDays(BigDecimal principalDisbursed,BigDecimal penaltyRate, BigDecimal maxPenaltyPrincipal) {
        BigDecimal amountPerDay = principalDisbursed.multiply(penaltyRate).divide(amount(100), 2, RoundingMode.HALF_EVEN);
        BigDecimal maxPenaltyAmount = principalDisbursed.multiply(maxPenaltyPrincipal);
        return maxPenaltyAmount.divide(amountPerDay,0, RoundingMode.HALF_DOWN).intValue();
    }
}
