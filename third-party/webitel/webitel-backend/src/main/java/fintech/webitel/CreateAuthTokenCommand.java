package fintech.webitel;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateAuthTokenCommand {
    private String username;
    private String password;
}
