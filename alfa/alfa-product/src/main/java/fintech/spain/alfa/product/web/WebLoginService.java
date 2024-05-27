package fintech.spain.alfa.product.web;

import fintech.crm.CrmConstants;
import fintech.crm.documents.IdentityDocumentService;
import fintech.crm.logins.EmailLogin;
import fintech.crm.logins.EmailLoginService;
import fintech.risk.checklist.CheckListConstants;
import fintech.risk.checklist.CheckListService;
import fintech.risk.checklist.model.CheckListQuery;
import fintech.spain.alfa.product.utils.PasswordHashUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.time.Duration;

import static java.time.Instant.now;

@Slf4j
@Component
public class WebLoginService {

    public static final Duration WEB_FULL_TOKEN_VALIDITY_IN_HOURS = Duration.ofHours(12);
    public static final Duration WEB_READ_TOKEN_VALIDITY_IN_MINUTES = Duration.ofMinutes(15);

    @Autowired
    private CheckListService checkListService;

    @Autowired
    private EmailLoginService emailLoginService;

    @Autowired
    private IdentityDocumentService identityDocumentService;

    @Autowired
    private PasswordHashUtils passwordHashUtils;

    @Autowired
    private WebJwtTokenService jwtTokenService;

    public String login(String email, String password) throws AccessDeniedException {
        verifyEmailNotBlacklisted(email);

        EmailLogin emailLogin = emailLoginService.findByEmail(email).orElseThrow(() -> new AccessDeniedException("Invalid username or password"));

        verifyDniNotBlacklisted(emailLogin.getClientId());

        if (!passwordHashUtils.verifyPassword(password, emailLogin.getPassword())) {
            log.info("Login failed with email {}, wrong password", email);
            throw new AccessDeniedException("Invalid username or password");
        }

        return jwtTokenService.tokenBuilder(emailLogin.getEmail(), "web:" + emailLogin.getEmail(), WebAuthorities.WEB_FULL, now().plus(WEB_FULL_TOKEN_VALIDITY_IN_HOURS)).build();
    }

    private void verifyEmailNotBlacklisted(String email) {
        if (!checkListService.isAllowed(CheckListQuery.builder().type(CheckListConstants.CHECKLIST_TYPE_EMAIL).value1(email).build())) {
            log.info("Login failed, email {} blacklisted", email);
            throw new AccessDeniedException("Invalid username or password");
        }
    }

    private void verifyDniNotBlacklisted(Long clientId) {
        identityDocumentService.findPrimaryDocument(clientId, CrmConstants.IDENTITY_DOCUMENT_DNI).ifPresent(identityDocument -> {
            if (!checkListService.isAllowed(CheckListQuery.builder().type(CheckListConstants.CHECKLIST_TYPE_DNI).value1(identityDocument.getNumber()).build())) {
                log.info("Login failed, dni {} blacklisted", identityDocument.getNumber());
                throw new AccessDeniedException("Invalid username or password");
            }
        });
    }

    public String login(Long clientId, Duration validity) {
        return login(clientId, validity, WebAuthorities.WEB_FULL);
    }

    public String login(Long clientId, Duration validity, String role) {
        EmailLogin emailLogin = emailLoginService.findByClientId(clientId).orElseThrow(() -> new AccessDeniedException("Unknown client: "+ clientId));
        return jwtTokenService.tokenBuilder(emailLogin.getEmail(), "web:" + emailLogin.getEmail(), role, now().plus(validity)).build();
    }

    public String login(Long clientId, String affiliateId, Duration validity) {
        EmailLogin emailLogin = emailLoginService.findByClientId(clientId).orElseThrow(() -> new AccessDeniedException("Unknown client"));
        return jwtTokenService.tokenBuilder(emailLogin.getEmail(), "web:" + emailLogin.getEmail(), WebAuthorities.WEB_FULL, now().plus(validity))
            .withClaim(WebJwtTokenService.CLAIM_AFFILIATE_ID, affiliateId)
            .build();
    }

    public String loginOnBehalfOfClient(Long clientId, String backofficeUser) {
        EmailLogin emailLogin = emailLoginService.findByClientId(clientId).orElseThrow(() -> new AccessDeniedException("Unknown client"));
        return jwtTokenService.tokenBuilder(emailLogin.getEmail(), "bo:" + backofficeUser, WebAuthorities.WEB_READ_ONLY, now().plus(WEB_READ_TOKEN_VALIDITY_IN_MINUTES)).build();
    }
}
