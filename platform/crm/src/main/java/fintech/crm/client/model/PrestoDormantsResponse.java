package fintech.crm.client.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PrestoDormantsResponse {

    private Long clientId;
    private String link;
    private String token;
    private Long loanId;
}
