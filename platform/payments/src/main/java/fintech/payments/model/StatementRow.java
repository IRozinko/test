package fintech.payments.model;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Data
public class StatementRow {

    private Long paymentId;

    private String accountNumber;

    @NotNull
    private LocalDate date;

    @NotNull
    private LocalDate valueDate;

    @NotNull
    private String transactionCode;

    @NotNull
    private String counterpartyName;

    @NotNull
    private String counterpartyAccount;

    private String counterpartyAddress;

    @NotNull
    private String description;

    @NotNull
    private String reference;

    @NotNull
    private BigDecimal amount;

    @NotNull
    private String currency;

    @NotNull
    private StatementRowStatus status = StatementRowStatus.NEW;

    private BigDecimal balance;

    private String portfolio;
    private String dni;

    @NotNull
    private String uniqueKey;

    private String suggestedTransactionSubType;

    private String sourceJson;

    private Map<String, String> attributes = new HashMap<>();
}
