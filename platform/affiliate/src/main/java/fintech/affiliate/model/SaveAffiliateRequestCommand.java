package fintech.affiliate.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class SaveAffiliateRequestCommand {
    private String requestType;
    private Long applicationId;
    private Long clientId;
    private Object request;
    private Object response;
}
