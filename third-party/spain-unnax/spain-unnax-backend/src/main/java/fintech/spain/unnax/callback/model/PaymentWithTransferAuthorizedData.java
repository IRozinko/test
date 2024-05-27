package fintech.spain.unnax.callback.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PaymentWithTransferAuthorizedData implements CallbackData {

    private String orderCode;
    private String bankOrderCode;
    private Integer amount;
    private String currency;
    private String customerCode;
    private String customerNames;
    private String service;
    private String status;
    private Boolean success;
    private String errorMessages;

}
