package fintech.spain.alfa.product.lending.impl;

import fintech.BigDecimalUtils;

import java.math.BigDecimal;

import static fintech.BigDecimalUtils.amount;

public class AprCalculator {

    private static final BigDecimal MAX_APR = amount("999999999.9");

    public static BigDecimal calculate(BigDecimal principal, BigDecimal interest, Long days) {
        double irr = irr(new double[]{ principal.negate().doubleValue(),  interest.add(principal).doubleValue() }, 0.1d);
        return irrYearly(irr, 365.0d / days);
    }

    private static BigDecimal irrYearly(double irr, double numberOfParts) {
        BigDecimal annualPercentageRate = new BigDecimal(Math.pow((1 + irr), numberOfParts)).subtract(BigDecimal.ONE);
        BigDecimal apr = annualPercentageRate.multiply(amount(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
        return BigDecimalUtils.gt(apr, MAX_APR) ? MAX_APR : apr;
    }

    /**
     * Computes the internal rate of return using an estimated irr of 10 percent.
     */
    private static double irr(double[] income) {
        return irr(income, 0.1d);
    }

    /**
     * @see http://svn.apache.org/repos/asf/poi/trunk/src/java/org/apache/poi/ss/formula/functions/Irr.java
     * @see http://en.wikipedia.org/wiki/Internal_rate_of_return#Numerical_solution
     */
    private static double irr(double[] values, double guess) {
        int maxIterationCount = 20;
        double absoluteAccuracy = 1E-7;
        double x0 = guess;
        double x1;
        int i = 0;

        while (i < maxIterationCount) {
            // the value of the function (NPV) and its derivate can be calculated in the same loop
            double fValue = 0;
            double fDerivative = 0;
            for (int k = 0; k < values.length; k++) {
                fValue += values[k] / Math.pow(1.0 + x0, k);
                fDerivative += -k * values[k] / Math.pow(1.0 + x0, k + 1);
            }

            // the essense of the Newton-Raphson Method
            x1 = x0 - fValue / fDerivative;
            if (Math.abs(x1 - x0) <= absoluteAccuracy) {
                return x1;
            }
            x0 = x1;
            ++i;
        }
        // maximum number of iterations is exceeded
        return Double.NaN;
    }

}
