package fintech.crm.logins;

import java.util.List;

public interface ResetPasswordService {

    String generateToken(GenerateTokenCommand command);

    EmailLogin resetPassword(ResetPasswordCommand command) throws ResetPasswordException;

    List<String> findTokensByClient(Long clientId);
}
