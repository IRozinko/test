package fintech.spain.alfa.product.affiliate;

import com.fasterxml.jackson.annotation.JsonProperty;
import fintech.spain.alfa.product.affiliate.validators.AcceptTerms;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.NotEmpty;

@Data
@Accessors(chain = true)
public class AffiliateRegistrationStep2Form {

    @NotEmpty
    private String code;

    @NotEmpty
    @JsonProperty("request_id")
    private String applicationUuid;

    @JsonProperty("accept_marketing")
    private Boolean acceptMarketing;

    @AcceptTerms
    private Integer tos;
}
