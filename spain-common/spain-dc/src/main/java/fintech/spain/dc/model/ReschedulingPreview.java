package fintech.spain.dc.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static fintech.BigDecimalUtils.amount;

@Data
@Accessors(chain = true)
public class ReschedulingPreview {

    private List<Item> items = new ArrayList<>();
    private LocalDate periodFrom;
    private LocalDate periodTo;

    @Data
    @Accessors(chain = true)
    public static class Item {

        private Long installmentSequence;
        private LocalDate periodFrom;
        private LocalDate periodTo;
        private LocalDate dueDate;
        private LocalDate generateInvoiceOnDate;
        private Long gracePeriodInDays = 0L;
        private boolean applyPenalty;

        private BigDecimal principalScheduled = amount(0);
        private BigDecimal interestScheduled = amount(0);
        private BigDecimal penaltyScheduled = amount(0);
        private List<FeeItem> feeItems;
        private BigDecimal totalScheduled = amount(0);
    }

    @Data
    @Accessors(chain = true)
    public static class FeeItem {
        private String type;
        private BigDecimal amountApplied = amount(0);
        private BigDecimal amountScheduled = amount(0);
    }
}
