package fintech.spain.unnax.transfer.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class TransferAutoDetails {

    private String orderCode;
    private String bankOrderCode;
    private Integer amount;
    private String currency;
    private String concept;
    private String sourceIp;
    private String customerCode;
    private String customerNames;
    private String sourceAccount;
    private String destinationAccount;
    private String callbackUrl;
    private String state;

}
