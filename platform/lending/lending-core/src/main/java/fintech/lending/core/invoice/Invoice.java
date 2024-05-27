package fintech.lending.core.invoice;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static fintech.BigDecimalUtils.amount;

@Data
public class Invoice {

    private Long id;

    private Long productId;

    private Long clientId;

    private Long loanId;

    private Long fileId;

    private String fileName;

    private String number;

    private InvoiceStatus status;

    private InvoiceStatusDetail statusDetail;

    private LocalDate periodFrom;

    private LocalDate periodTo;

    private LocalDate invoiceDate;

    private LocalDate dueDate;

    private LocalDate closeDate;

    private String closeReason;

    private boolean voided;

    private BigDecimal total = amount(0);

    private BigDecimal totalPaid = amount(0);

    private BigDecimal totalDue = amount(0);

    private BigDecimal interestDue = amount(0);

    private BigDecimal principalDue = amount(0);

    private BigDecimal feeDue = amount(0);

    private BigDecimal penaltyDue = amount(0);

    private List<InvoiceItem> items = newArrayList();

    private boolean generateFile;

    private boolean sendFile;

    private LocalDateTime sentAt;

    private Boolean membershipLevelChanged;

    private boolean manual;

}
