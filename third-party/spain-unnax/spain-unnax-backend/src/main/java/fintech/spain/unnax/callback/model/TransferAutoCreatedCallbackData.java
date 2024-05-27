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
public class TransferAutoCreatedCallbackData implements CallbackData {

    private String orderId;
    private String customerAccount;
    private String sourceAccount;
    private String currency;
    private String customerId;
    private LocalDate date;
    private LocalTime time;
    private Integer amount;

}
