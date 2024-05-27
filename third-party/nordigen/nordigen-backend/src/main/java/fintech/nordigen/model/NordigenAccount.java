package fintech.nordigen.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "holder_name",
    "currency",
    "period_start",
    "period_end",
    "bank_name",
    "account_nr",
    "start_balance",
    "end_balance",
    "debit_turnover",
    "credit_turnover",
    "transaction_list"
})
@Data
public class NordigenAccount {

    @JsonProperty("holder_name")
    private String holderName;
    @JsonProperty("currency")
    private String currency;
    @JsonProperty("period_start")
    private LocalDate periodStart;
    @JsonProperty("period_end")
    private LocalDate periodEnd;
    @JsonProperty("bank_name")
    private String bankName;
    @JsonProperty("account_nr")
    private String accountNumber;
    @JsonProperty("start_balance")
    private BigDecimal startBalance;
    @JsonProperty("end_balance")
    private BigDecimal endBalance;
    @JsonProperty("debit_turnover")
    private BigDecimal debitTurnover;
    @JsonProperty("credit_turnover")
    private Integer creditTurnover;
    @JsonProperty("transaction_list")
    private List<NordigenAccountTransaction> transactions;
}
