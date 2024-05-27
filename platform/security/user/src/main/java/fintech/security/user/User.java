package fintech.security.user;

import lombok.Data;
import lombok.ToString;

import java.util.Set;

@Data
@ToString(exclude = "password")
public class User {

    private Long id;
    private String email;
    private String password;
    private boolean temporaryPassword;

    private Set<String> roles;
    private Set<String> permissions;
}
