package fintech.spain.alfa.web.controllers.web;

import fintech.spain.alfa.web.models.LoginRequest;
import fintech.spain.alfa.web.models.LoginResponse;
import fintech.spain.alfa.product.web.WebLoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
public class LoginApi {

    @Autowired
    private WebLoginService loginService;

    @PostMapping("/api/public/web/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest login) {
        log.info("Logging in as client {}", login.getEmail());
        String token = loginService.login(login.getEmail(), login.getPassword());
        return new LoginResponse(token);
    }
}
