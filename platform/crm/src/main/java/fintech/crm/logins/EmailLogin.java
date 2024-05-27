package fintech.crm.logins;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(exclude = "password")
public class EmailLogin {

    private Long id;
    private Long clientId;
    private String clientNumber;
    private String email;
    private String password;
    private boolean temporaryPassword;
}
