package fintech.bo.api.model.payments;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class AddPaymentRequest {

    private Long accountId;
    private String paymentType;

    private LocalDate valueDate;
    private BigDecimal amount;
    private String details;
    private String reference;
    private String key;

    private String counterpartyName;
    private String counterpartyAccount;
    private String counterpartyAddress;

}
