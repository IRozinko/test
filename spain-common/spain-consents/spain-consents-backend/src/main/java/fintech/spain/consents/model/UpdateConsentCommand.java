package fintech.spain.consents.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UpdateConsentCommand {

    private Long clientId;
    private String name;
    private String version;
    private boolean accepted;
    private String source;

}
