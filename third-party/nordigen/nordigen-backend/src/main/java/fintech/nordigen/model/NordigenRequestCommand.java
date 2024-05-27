package fintech.nordigen.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(exclude = "requestBody")
public class NordigenRequestCommand {

    private Long clientId;
    private Long applicationId;
    private Long loanId;
    private Long instantorResponseId;
    private NordigenRequestBody requestBody;
}
