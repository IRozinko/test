package fintech.security.user;

import lombok.Data;

@Data
public class ChangePasswordCommand {

    private String email;
    private String password;
    private boolean temporaryPassword;
}
