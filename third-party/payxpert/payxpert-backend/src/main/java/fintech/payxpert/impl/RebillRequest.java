package fintech.payxpert.impl;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RebillRequest {

    private String transactionID;
    private Long amount;
}
