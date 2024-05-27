package fintech.spain.unnax.model;

import fintech.spain.unnax.db.TransferAutoStatus;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class TransferAutoQuery {

    private String orderCode;
    private LocalDateTime processedFromDate;
    private LocalDateTime processedToDate;
    private TransferAutoStatus status;

    public static TransferAutoQuery byOrderCode(String orderCode) {
        return new TransferAutoQuery()
            .setOrderCode(orderCode);
    }
}
