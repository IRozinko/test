package fintech.crm.logins;

public interface ChangeEmailService {

    String generateToken(GenerateTokenCommand command);

    boolean isEmailAvailable(Long clientId, String email);

    void verifyAndChange(ChangeEmailCommand command) throws VerifyEmailException;
}
