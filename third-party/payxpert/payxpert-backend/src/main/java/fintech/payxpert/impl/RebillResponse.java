package fintech.payxpert.impl;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RebillResponse {

    private String transactionID;
    private String errorCode;
    private String errorMessage;
    private String statementDescriptor;
}
