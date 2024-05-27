package fintech.crm.contacts;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AddEmailContactCommand {
    private Long clientId;
    private String email;
}
