package fintech.crm.logins;

import lombok.Data;

@Data
public class GenerateTokenCommand {
    private final long clientId;
    private final int validityInHours;

    public GenerateTokenCommand(long clientId, int validityInHours) {
        this.clientId = clientId;
        this.validityInHours = validityInHours;
    }
}
