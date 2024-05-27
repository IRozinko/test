package fintech.bo.api.model.users;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(exclude = "password")
public class ChangeMyPasswordRequest {

    private String password;
}
