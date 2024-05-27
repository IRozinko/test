package fintech.crm.marketing.event;

import fintech.TimeMachine;
import fintech.crm.client.model.ChangeAcceptMarketingCommand;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ClientMarketingConsentChanged {

    private final long clientId;
    private final Boolean newValue;
    private final LocalDateTime when;
    private final String source;
    private final String note;
    private final Long emailActivityId;

    public ClientMarketingConsentChanged(ChangeAcceptMarketingCommand cmd) {
        this.clientId = cmd.clientId;
        this.newValue = cmd.newValue;
        this.when = TimeMachine.now();
        this.source = cmd.source;
        this.note = cmd.note;
        this.emailActivityId = cmd.emailActivityId;
    }
}
