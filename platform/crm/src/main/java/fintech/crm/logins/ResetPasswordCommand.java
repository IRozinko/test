package fintech.crm.logins;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import static com.google.common.base.Preconditions.checkArgument;

@Data
public class ResetPasswordCommand {
    private final String password;
    private final String token;

    public ResetPasswordCommand(String token, String password) {
        checkArgument(!StringUtils.isEmpty(token), "Empty token");
        checkArgument(!StringUtils.isEmpty(password), "Empty password");

        this.token = token;
        this.password = password;
    }
}


