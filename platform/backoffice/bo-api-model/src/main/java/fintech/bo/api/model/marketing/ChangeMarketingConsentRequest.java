package fintech.bo.api.model.marketing;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class ChangeMarketingConsentRequest {

    @NotNull
    private Long clientId;

    @NotNull
    private String source;

    @NotNull
    private Boolean newValue;

    private String note;
    private Long emailActivityId;

    public ChangeMarketingConsentRequest(Long clientId, String source, Boolean newValue) {
        this.clientId = clientId;
        this.source = source;
        this.newValue = newValue;
    }
}
