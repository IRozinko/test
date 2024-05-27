package fintech.security.user;

import lombok.Data;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Data
@ToString
public class UpdateUserCommand {

    private String email;
    private Set<String> roles = new HashSet<>();
}
