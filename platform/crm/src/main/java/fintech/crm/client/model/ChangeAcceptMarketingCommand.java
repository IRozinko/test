package fintech.crm.client.model;

import lombok.AllArgsConstructor;
import lombok.Builder;


@Builder
@AllArgsConstructor
public class ChangeAcceptMarketingCommand {

    public final Long clientId;
    public final String source;
    public final String note;
    public final Long emailActivityId;
    public final Boolean newValue;

    public ChangeAcceptMarketingCommand(Long clientId, Boolean newValue) {
        this(clientId, newValue, "OTHER");
    }

    public ChangeAcceptMarketingCommand(Long clientId, Boolean newValue, String source) {
        this.clientId = clientId;
        this.newValue = newValue;
        this.source = source;
        this.note = "";
        this.emailActivityId = null;
    }
}
