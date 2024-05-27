package fintech.security.user;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Set;

@Data
@Accessors(chain = true)
public class SaveRoleCommand {

    private String name;
    private Set<String> permissions;
}
