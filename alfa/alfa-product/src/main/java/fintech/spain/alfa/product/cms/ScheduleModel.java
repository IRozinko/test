package fintech.spain.alfa.product.cms;

import fintech.lending.core.loan.InstallmentStatus;
import fintech.lending.core.loan.InstallmentStatusDetail;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static fintech.BigDecimalUtils.amount;

@Data
@Accessors(chain = true)
public class ScheduleModel {

    private LocalDate startDate;
    private BigDecimal totalScheduled  = amount(0);
    private BigDecimal totalDue  = amount(0);
    private BigDecimal totalPaid  = amount(0);
    private BigDecimal feeScheduled  = amount(0);

    private List<InstallmentModel> installments = new ArrayList<>();

    @Data
    @Accessors(chain = true)
    public static class InstallmentModel {
        private InstallmentStatus status;
        private InstallmentStatusDetail statusDetail;
        private LocalDate periodFrom;
        private LocalDate periodTo;
        private LocalDate dueDate;
        private LocalDate closeDate;
        private LocalDate generateInvoiceOnDate;
        private Long installmentSequence;
        private String installmentNumber;
        private BigDecimal totalScheduled = amount(0);
        private BigDecimal totalPaid = amount(0);
        private BigDecimal totalDue = amount(0);
        private BigDecimal principalScheduled  = amount(0);
        private BigDecimal principalPaid  = amount(0);
        private BigDecimal interestScheduled  = amount(0);
        private BigDecimal interestPaid  = amount(0);
        private BigDecimal penaltyScheduled  = amount(0);
        private BigDecimal penaltyPaid  = amount(0);
        private BigDecimal feeScheduled  = amount(0);
        private BigDecimal feePaid  = amount(0);
    }
}
