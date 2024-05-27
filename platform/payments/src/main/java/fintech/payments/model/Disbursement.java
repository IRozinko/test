package fintech.payments.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Disbursement {

    private Long id;
    private String disbursementType;
    private Long clientId;
    private Long loanId;
    private Long applicationId;
    private Long institutionId;
    private Long institutionAccountId;
    private BigDecimal amount;
    private LocalDate valueDate;
    private String reference;
    private DisbursementStatus status;
    private DisbursementStatusDetail statusDetail;
    private String error;
    private String exportedFileName;
    private Long exportedCloudFileId;
    private LocalDateTime exportedAt;
    private boolean apiExport;
}
