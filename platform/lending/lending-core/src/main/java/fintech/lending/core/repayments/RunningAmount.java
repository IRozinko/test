package fintech.lending.core.repayments;

import lombok.Getter;

import java.math.BigDecimal;

import static fintech.BigDecimalUtils.*;

@Getter
public class RunningAmount {

    private BigDecimal paymentAmountLeft;
    private BigDecimal paymentAmountUsed = amount(0);
    private BigDecimal overpaymentAmountLeft;
    private BigDecimal overpaymentAmountUsed = amount(0);

    public RunningAmount(BigDecimal paymentAmount, BigDecimal overpaymentAmount) {
        this.paymentAmountLeft = paymentAmount;
        this.overpaymentAmountLeft = overpaymentAmount;
    }

    public BigDecimal take(BigDecimal amount) {
        BigDecimal paymentAmountUsed = takePayment(amount);
        BigDecimal amountLeft = amount.subtract(paymentAmountUsed);

        BigDecimal overpaymentAmountUsed = takeOverpayment(amountLeft);
        return paymentAmountUsed.add(overpaymentAmountUsed);
    }

    public BigDecimal takePayment(BigDecimal amount) {
        BigDecimal paymentAmountToUse = min(paymentAmountLeft, amount);
        paymentAmountLeft = paymentAmountLeft.subtract(paymentAmountToUse);
        paymentAmountUsed = paymentAmountUsed.add(paymentAmountToUse);

        return paymentAmountToUse;
    }

    public BigDecimal takeOverpayment(BigDecimal amount) {
        BigDecimal overpaymentAmountToUse = min(overpaymentAmountLeft, amount);
        overpaymentAmountLeft = overpaymentAmountLeft.subtract(overpaymentAmountToUse);
        overpaymentAmountUsed = overpaymentAmountUsed.add(overpaymentAmountToUse);

        return overpaymentAmountToUse;
    }

    public boolean isPaymentAmountLeft() {
        return isPositive(paymentAmountLeft);
    }

    public boolean isAmountLeft() {
        return isPositive(getAmountLeft());
    }

    public BigDecimal getAmountLeft() {
        return paymentAmountLeft.add(overpaymentAmountLeft);
    }

    public void resetAmountUsed() {
        paymentAmountUsed = amount(0);
        overpaymentAmountUsed = amount(0);
    }
}
