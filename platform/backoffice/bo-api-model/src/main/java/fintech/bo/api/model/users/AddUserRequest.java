package fintech.bo.api.model.users;

import lombok.Data;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Data
@ToString(exclude = "password")
public class AddUserRequest {

    private String email;
    private String password;
    private boolean temporaryPassword;
    private Set<String> roles = new HashSet<>();

}
