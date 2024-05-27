package fintech.bo.api.model.users;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(exclude = "password")
public class ChangePasswordRequest {

    private String email;
    private String password;
    private boolean temporaryPassword;
}
