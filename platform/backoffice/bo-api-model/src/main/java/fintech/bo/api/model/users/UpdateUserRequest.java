package fintech.bo.api.model.users;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class UpdateUserRequest {

    private String email;
    private Set<String> roles = new HashSet<>();

}
