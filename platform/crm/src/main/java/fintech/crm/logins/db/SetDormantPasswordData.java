package fintech.crm.logins.db;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class SetDormantPasswordData {

    private String firstName;
    private String email;
    private String token;

}
