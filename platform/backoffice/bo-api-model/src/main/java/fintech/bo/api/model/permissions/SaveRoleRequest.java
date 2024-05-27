package fintech.bo.api.model.permissions;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class SaveRoleRequest {

    private String name;
    private Set<String> permissions = new HashSet<>();

}
