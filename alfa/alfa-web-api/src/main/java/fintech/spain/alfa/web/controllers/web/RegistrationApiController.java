//package fintech.spain.alfa.web.controllers.web;
//
//import com.google.common.collect.ImmutableList;
//import fintech.crm.bankaccount.DuplicateBankAccountException;
//import fintech.crm.contacts.DuplicatePrimaryEmailException;
//import fintech.crm.contacts.DuplicatePrimaryPhoneException;
//import fintech.crm.documents.DuplicateDocumentNumberException;
//import fintech.crm.logins.DuplicateEmailLoginException;
//import fintech.spain.alfa.web.common.WebRequestUtils;
//import fintech.spain.alfa.web.config.security.AnonymousApiUser;
//import fintech.spain.alfa.web.config.security.WebApiUser;
//import fintech.spain.alfa.web.models.ChangePhoneRequest;
//import fintech.spain.alfa.web.models.SendVerificationCodeOkResult;
//import fintech.spain.alfa.web.models.SignUpOkResult;
//import fintech.spain.platform.web.validations.ValidationSequence;
//import fintech.spain.alfa.product.registration.RegistrationFacade;
//import fintech.spain.alfa.product.registration.SendVerificationCodeResult;
//import fintech.spain.alfa.product.registration.forms.ApplicationForm;
//import fintech.spain.alfa.product.registration.forms.DocumentNumberForm;
//import fintech.spain.alfa.product.registration.forms.SignUpForm;
//import fintech.spain.alfa.product.web.WebAuthorities;
//import fintech.spain.alfa.product.web.WebLoginService;
//import fintech.spain.web.common.ValidationExceptions;
//import fintech.web.api.models.OkResponse;
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import net.jodah.failsafe.Failsafe;
//import net.jodah.failsafe.RetryPolicy;
//import org.springframework.security.access.annotation.Secured;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.validation.Valid;
//import java.util.concurrent.TimeUnit;
//
//@AllArgsConstructor
//@RestController
//@Slf4j
//public class RegistrationApiController {
//
//    private final RegistrationFacade registrationFacade;
//    private final WebLoginService loginService;
//    private final ValidationExceptions validationExceptions;
//
//    private final RetryPolicy retryPolicy = new RetryPolicy()
//        .retryOn(Exception.class)
//        .withMaxRetries(5)
//        .withDelay(1, TimeUnit.SECONDS);
//
//    @PostMapping("/api/public/web/registration/signup")
//    public SignUpOkResult signUp(@RequestBody @Validated(ValidationSequence.class) SignUpForm request) {
//        log.info("Submitting sign up request {}", request);
//        Long clientId;
//
//        //Temporary replace anonymous user with request data
//        String ipAddress = WebRequestUtils.resolveIpAddress();
//        AnonymousApiUser anonymousApiUser = new AnonymousApiUser(request.getEmail(), "", ImmutableList.of(), ipAddress);
//        PreAuthenticatedAuthenticationToken authentication = new PreAuthenticatedAuthenticationToken(anonymousApiUser, "");
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//
//        try {
//            clientId = registrationFacade.signUp(request, true);
//        } catch (DuplicatePrimaryEmailException | DuplicateEmailLoginException ex) {
//            throw validationExceptions.notUnique("email");
//        } catch (DuplicatePrimaryPhoneException e) {
//            throw validationExceptions.notUnique("mobilePhone");
//        }
//        String token = loginService.login(request.getEmail(), request.getPassword());
//        return new SignUpOkResult(clientId, token);
//    }
//
//    @Secured(WebAuthorities.WEB_FULL)
//    @PostMapping("/api/web/registration/save-application-form")
//    public OkResponse saveApplicationForm(@AuthenticationPrincipal WebApiUser user, @RequestBody @Valid ApplicationForm form) {
//        try {
//            registrationFacade.saveApplicationData(user.getClientId(), form);
//        } catch (DuplicateBankAccountException ex) {
//            throw validationExceptions.notUnique("bankAccountNumber");
//        } catch (DuplicateDocumentNumberException ex) {
//            throw validationExceptions.notUnique("documentNumber");
//        }
//        return OkResponse.OK;
//    }
//
//    @Secured(WebAuthorities.WEB_FULL)
//    @PostMapping("/api/web/registration/save-document-number")
//    public OkResponse saveDocumentNumber(@AuthenticationPrincipal WebApiUser user, @RequestBody @Validated(ValidationSequence.class) DocumentNumberForm form) {
//        try {
//            registrationFacade.saveDocumentNumber(user.getClientId(), form);
//        } catch (DuplicateDocumentNumberException ex) {
//            throw validationExceptions.notUnique("documentNumber");
//        }
//        return OkResponse.OK;
//    }
//
//    @Secured(WebAuthorities.WEB_FULL)
//    @PostMapping("/api/web/registration/send-phone-verification-code")
//    public SendVerificationCodeOkResult sendPhoneVerificationCode(@AuthenticationPrincipal WebApiUser user) {
//        SendVerificationCodeResult result = registrationFacade.sendPhoneVerificationCode(user.getClientId());
//        return SendVerificationCodeOkResult.OK(result.isCodeSent(), result.getAvailableAttempts(), result.getNextAttemptInSeconds());
//    }
//
//    @Secured(WebAuthorities.WEB_FULL)
//    @PostMapping("/api/web/registration/change-phone")
//    public OkResponse changePhone(@AuthenticationPrincipal WebApiUser user, @RequestBody @Valid ChangePhoneRequest request) {
//        try {
//            registrationFacade.changePhone(user.getClientId(), request.getMobilePhone());
//            return OkResponse.OK;
//        } catch (DuplicatePrimaryPhoneException e) {
//            log.info("Failed to change phone for client [{}], phone [{}] already registered", user.getClientId(), request.getMobilePhone());
//            throw validationExceptions.notUnique("mobilePhone");
//        }
//    }
//
//}
