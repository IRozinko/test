package fintech.spain.alfa.product.loc;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class LocBatchQuery {

    private LocBatchStatus status;
    private Long clientId;
    private Integer maxClients;
    private Long batchNumber;
}
