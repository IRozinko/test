package fintech.spain.asnef.commands;

import lombok.Data;

@Data
public class OutputRecordHolder<Output> {

    private Long clientId;

    private Long loanId;

    private String operationIdentifier;

    private String number;

    private Output outputRecord;
}
