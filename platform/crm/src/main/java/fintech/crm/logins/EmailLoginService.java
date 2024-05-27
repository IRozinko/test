package fintech.crm.logins;

import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface EmailLoginService {

    Long add(AddEmailLoginCommand command) throws DuplicateEmailLoginException;

    void changeEmail(ChangeEmailCommand command) throws DuplicateEmailLoginException;

    void changePassword(ChangePasswordCommand command) throws CurrentPasswordMatchException;

    @Transactional
    void setTemporaryPassword(Long clientId, String email, String password);

    Optional<EmailLogin> findByEmail(String email);

    boolean isEmailAvailable(String email);

    Optional<EmailLogin> findByClientId(Long clientId);

    void delete(String email);

    void delete(Long clientId);
}
