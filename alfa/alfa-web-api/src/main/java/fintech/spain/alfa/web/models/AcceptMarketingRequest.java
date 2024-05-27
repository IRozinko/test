package fintech.spain.alfa.web.models;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
public class AcceptMarketingRequest {

    @NotNull
    private Boolean acceptMarketing;
    private String source;
}
