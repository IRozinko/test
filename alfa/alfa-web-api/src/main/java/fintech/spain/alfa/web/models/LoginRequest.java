package fintech.spain.alfa.web.models;

import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.NotEmpty;

@Data
@ToString(exclude = "password")
public class LoginRequest {
    @NotEmpty
    private String email;
    @NotEmpty
    private String password;
}
