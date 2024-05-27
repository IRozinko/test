package fintech.spain.consents.bo.api.model;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

@Data
public class UpdateTermsRequest {

    @NotEmpty
    private String name;

    @NotEmpty
    private String version;

    @NotEmpty
    private String text;

}
