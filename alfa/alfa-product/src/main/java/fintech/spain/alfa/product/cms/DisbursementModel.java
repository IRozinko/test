package fintech.spain.alfa.product.cms;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DisbursementModel {

    private Long disbursementId;
    private String reference;
    private String status;
    private String loanNumber;
    private String paymentOrigin;
    private String recipient;
    private String iban;
    private BigDecimal amount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
