package fintech.security.user;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.HashSet;
import java.util.Set;

@Data
@Accessors(chain = true)
@ToString(exclude = "password")
public class AddUserCommand {

    private String email;
    private String password;
    private boolean temporaryPassword;
    private Set<String> roles = new HashSet<>();
}
