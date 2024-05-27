package fintech.spain.unnax.callback.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PaymentWithTransferCompletedData implements CallbackData {

    private String responseId;
    private String customerCode;
    private String orderCode;
    private String bankOrderCode;
    private Integer amount;
    private String date;
    private Boolean success;
    private String signature;
    private Boolean result;
    private String accountNumber;
    private String status;
    private String service;

}
