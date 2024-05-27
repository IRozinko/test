package fintech.bo.api.server;

import fintech.bo.api.model.LoginRequest;
import fintech.bo.api.model.LoginResponse;
import fintech.bo.api.server.security.BackofficeJwtTokenService;
import fintech.bo.api.server.security.BackofficeUserDetailsService;
import fintech.bo.api.server.security.BackoficeApiSecurityConfiguration;
import fintech.security.user.PasswordHash;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;

import static java.time.Instant.now;

@Slf4j
@RestController
public class LoginApiController {

    @Autowired
    private BackofficeUserDetailsService userDetailsService;

    @Autowired
    private BackofficeJwtTokenService jwtTokenService;

    @RequestMapping(path = "/api/public/bo/login", method = RequestMethod.POST)
    public LoginResponse login(@RequestBody LoginRequest request, HttpServletResponse response) {
        if (StringUtils.isBlank(request.getEmail())) {
            throw new AccessDeniedException("Empty email");
        }
        if (StringUtils.isBlank(request.getPassword())) {
            throw new AccessDeniedException("Empty password");
        }

        UserDetails user = getUserDetails(request);
        String token = jwtTokenService.generate(user.getUsername(), now().plus(Duration.ofHours(24)));
        addJwtTokenCookie(response, token);
        return new LoginResponse(token, user.getUsername());
    }

    private void addJwtTokenCookie(HttpServletResponse response, String token) {
        Cookie jwtCookie = new Cookie(BackoficeApiSecurityConfiguration.TOKEN_COOKIE_NAME, token);
        jwtCookie.setMaxAge((int) Duration.ofHours(24).getSeconds());
        jwtCookie.setPath("/api");
        jwtCookie.setHttpOnly(true);
        response.addCookie(jwtCookie);
    }

    private UserDetails getUserDetails(LoginRequest loginRequest) {
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(StringUtils.trim(loginRequest.getEmail()));
            if (!PasswordHash.verifyPassword(loginRequest.getPassword(), userDetails.getPassword())) {
                throw new AccessDeniedException("Access denied, invalid password");
            }
            return userDetails;
        } catch (UsernameNotFoundException e) {
            log.info("Login failed with email {}, unknown email", loginRequest.getEmail());
            throw new AccessDeniedException("Access denied, user not found by email");
        }
    }

}
