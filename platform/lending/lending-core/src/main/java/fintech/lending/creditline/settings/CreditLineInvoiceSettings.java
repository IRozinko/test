package fintech.lending.creditline.settings;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreditLineInvoiceSettings {

    private BigDecimal principalAmountPercentage;

    private BigDecimal minAmount;

    private int fixedInvoicingDay;

    private int fixedDueDay;

    private int dueDays;

    private int minPeriodDays;
}
