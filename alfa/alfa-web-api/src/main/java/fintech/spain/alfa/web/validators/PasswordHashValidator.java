package fintech.spain.alfa.web.validators;

import fintech.crm.logins.EmailLogin;
import fintech.crm.logins.EmailLoginService;
import fintech.spain.alfa.web.config.security.WebApiUser;
import fintech.spain.alfa.product.utils.PasswordHashUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Optional;

public class PasswordHashValidator implements ConstraintValidator<PasswordHash, String> {

    @Autowired
    private EmailLoginService emailLoginService;

    @Autowired
    private PasswordHashUtils passwordHashUtils;

    private PasswordHash constraintAnnotation;

    @Override
    public void initialize(PasswordHash constraintAnnotation) {
        this.constraintAnnotation = constraintAnnotation;
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        Optional<EmailLogin> emailLogin = getEmailLogin();
        return emailLogin.map(emailLogin1 -> isHashValid(emailLogin1, password)).orElse(true);
    }

    private boolean isHashValid(EmailLogin emailLogin, String password) {
        if (constraintAnnotation.skipTemporaryValidation() && emailLogin.isTemporaryPassword()) {
            return true;
        } else {
            return passwordHashUtils.verifyPassword(password, emailLogin.getPassword());
        }
    }

    private Optional<EmailLogin> getEmailLogin() {
        WebApiUser webApiUser = (WebApiUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (webApiUser != null) {
            return emailLoginService.findByClientId(webApiUser.getClientId());
        } else {
            return Optional.empty();
        }
    }
}
