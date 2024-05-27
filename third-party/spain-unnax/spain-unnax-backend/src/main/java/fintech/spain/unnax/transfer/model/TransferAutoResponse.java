package fintech.spain.unnax.transfer.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Accessors(chain = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class TransferAutoResponse {

    private String destinationAccount;
    private String bankOrderCode;
    private String currency;
    private LocalTime time;
    private LocalDate date;
    private Integer amount;
    private String customerCode;
    private String orderCode;
    private String sourceAccount;

}
