package fintech.spain.alfa.web.controllers.web;

import com.google.common.collect.ImmutableMap;
import fintech.crm.logins.EmailLogin;
import fintech.crm.logins.EmailLoginService;
import fintech.crm.logins.GenerateTokenCommand;
import fintech.crm.logins.ResetPasswordCommand;
import fintech.crm.logins.ResetPasswordException;
import fintech.crm.logins.ResetPasswordService;
import fintech.spain.alfa.web.models.ForgotPasswordRequest;
import fintech.spain.alfa.web.models.ResetPasswordRequest;
import fintech.spain.alfa.product.cms.CmsSetup;
import fintech.spain.alfa.product.cms.ResetPasswordModel;
import fintech.spain.alfa.product.cms.AlfaCmsModels;
import fintech.spain.alfa.product.cms.AlfaNotificationBuilderFactory;
import fintech.spain.web.common.ValidationExceptions;
import fintech.web.api.models.OkResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
public class PasswordApi {

    private static final int VALIDITY_IN_HOURS = 24;

    @Autowired
    private ResetPasswordService resetPasswordService;

    @Autowired
    private AlfaNotificationBuilderFactory notificationFactory;

    @Autowired
    private AlfaCmsModels cmsModels;

    @Autowired
    private EmailLoginService emailLoginService;

    @Autowired
    private ValidationExceptions validationExceptions;

    @PostMapping("/api/public/web/forgot-password")
    public OkResponse sendResetPasswordToken(@Valid @RequestBody ForgotPasswordRequest request) {
        emailLoginService.findByEmail(request.getEmail()).ifPresent(emailLogin -> {
            String token = generateToken(emailLogin);
            sendEmail(emailLogin, token);
        });
        return OkResponse.OK;
    }

    @PostMapping("/api/public/web/reset-password")
    public OkResponse resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        ResetPasswordCommand command = new ResetPasswordCommand(request.getToken(), request.getPassword());
        try {
            resetPasswordService.resetPassword(command);
            return OkResponse.OK;
        } catch (ResetPasswordException e) {
            throw validationExceptions.invalidValue("token");
        }
    }

    private String generateToken(EmailLogin login) {
        GenerateTokenCommand command = new GenerateTokenCommand(login.getClientId(), VALIDITY_IN_HOURS);
        return resetPasswordService.generateToken(command);
    }

    private void sendEmail(EmailLogin emailLogin, String token) {
        log.info("Sending reset password email for client id [{}]", emailLogin.getClientId());
        ResetPasswordModel model = cmsModels.resetPassword("/actualizar-contrasena/" + token);
        notificationFactory.fromCustomerService(emailLogin.getClientId())
            .render(CmsSetup.RESET_PASSWORD_NOTIFICATION, ImmutableMap.of(AlfaCmsModels.SCOPE_RESET_PASSWORD, model))
            .send();
    }
}
