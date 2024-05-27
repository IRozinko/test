package fintech.iovation.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class SaveBlackboxCommand {

    private Long clientId;
    private Long loanApplicationId;
    private String ipAddress;
    private String blackBox;
}
