package fintech.spain.asnef;

import lombok.Data;

@Data
public class LogRow {

    private Long id;

    private LogRowStatus status;

    private Long clientId;

    private Long loanId;

    private String operationIdentifier;

    private String number;

    private String outgoingRow;

    private String outgoingHolderRow;

    private String outgoingAddressRow;

    private String incomingRow;
}
