package fintech.crm.logins;


import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ChangePasswordCommand {
    private Long clientId;
    private String currentPassword;
    private String newPassword;
    private String email;
}

