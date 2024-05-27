package fintech.bo.api.model.dc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(chain = true)
public class ReschedulingPreviewResponse {

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

        private BigDecimal principalScheduled;
        private BigDecimal interestApplied;
        private BigDecimal interestScheduled;
        private BigDecimal penaltyScheduled;
        private List<FeeItem> feeItems = new ArrayList<>();

        private BigDecimal totalScheduled;
    }

    @Data
    @AllArgsConstructor
    public static class FeeItem {
        private String type;
        private BigDecimal amountApplied;
        private BigDecimal amountScheduled;
    }
}
