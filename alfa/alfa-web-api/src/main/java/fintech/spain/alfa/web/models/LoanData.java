package fintech.spain.alfa.web.models;

import fintech.lending.core.loan.LoanStatus;
import fintech.lending.core.loan.LoanStatusDetail;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static fintech.BigDecimalUtils.amount;

@Data
@Accessors(chain = true)
public class LoanData {

    private LoanStatus status;
    private LoanStatusDetail statusDetail;
    private String number;
    private LocalDate issueDate;
    private LocalDate closeDate;
    private LocalDate maturityDate;
    private long overdueDays;
    private LocalDate brokenDate;
    private BigDecimal principalDisbursed = amount(0);
    private BigDecimal interestDue = amount(0);
    private BigDecimal interestApplied = amount(0);
    private BigDecimal totalDue = amount(0);
    private BigDecimal totalPaid = amount(0);
    private BigDecimal totalOutstanding = amount(0);
    private BigDecimal penaltyDue = amount(0);
    private BigDecimal feeDue = amount(0);
    private BigDecimal principalOutstanding = amount(0);
    private BigDecimal penaltyOutstanding = amount(0);
    private BigDecimal interestOutstanding = amount(0);

    private AttachmentData loanAgreementAttachment;
    private AttachmentData reschedulingAgreementAttachment;
    private AttachmentData standardInformationAttachment;

    private List<ExtensionData> extensionOptions = new ArrayList<>();
    private ExtensionStatus extensionStatus;
    private List<InstallmentInfo> installments = new ArrayList<>();

    private String debtPortfolio;
    private String debtStatus;

    @Data
    @Accessors(chain = true)
    public static class ExtensionData {
        private BigDecimal price;
        private long extendByDays;
        private LocalDate extendedMaturityDate;
        private BigDecimal discountPct;
        private BigDecimal discountPrice;
    }
}
