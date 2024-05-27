package fintech.webitel.model;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@ToString(exclude = {"password"})
@Accessors(chain = true)
public class WebitelLoginCommand {
    private String username;
    private String password;
}
