package fintech.nordigen.model;

import fintech.nordigen.json.NordigenJson;
import lombok.Data;
import lombok.ToString;

@ToString(exclude = {"json", "responseBody"})
@Data
public class NordigenResult {

    private Long id;
    private Long clientId;
    private Long applicationId;
    private Long loanId;
    private NordigenStatus status;
    private String error;
    private String responseBody;
    private NordigenJson json;
    private Long instantorResponseId;
}
