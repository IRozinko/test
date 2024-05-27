package fintech.crm.logins;


import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ChangeEmailCommand {
    private Long clientId;
    private String currentEmail;
    private String newEmail;
    private String token;
}

