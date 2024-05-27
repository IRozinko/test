package fintech.spain.unnax.event;

import lombok.Getter;

@Getter
public class BankStatementReceivedEvent {

    private final Long statementId;
    private final byte[] rawPdf;

    public BankStatementReceivedEvent(Long statementId, byte[] rawPdf) {
        this.statementId = statementId;
        this.rawPdf = rawPdf;
    }

}
