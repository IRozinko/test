package fintech.spain.unnax.callback.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Accessors(chain = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class TransferAutoProcessedCallbackData implements CallbackData {

    private boolean success;
    private String product;
    private String orderId;
    private String bankOrderId;
    private LocalDate date;
    private LocalTime time;
    private Integer amount;
    private String currency;
    private String customerId;
    private String customerAccount;
    private String sourceAccount;

    /**
     * The available balance for the source account is available here
     */
    private Integer srcAccountBalance;
    private boolean cancelled;
    private Long sourceBankId;
    private String errorCode;
    private String errorMessage;

}
