package fintech.spain.alfa.web.models;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AcceptMarketingResponse {

    private boolean acceptMarketing;
}
