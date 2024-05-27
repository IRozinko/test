package fintech.spain.alfa.web.controllers.web;

import fintech.spain.alfa.web.models.LoginResponse;
import fintech.spain.alfa.web.models.SpecialLinkDataResponse;
import fintech.spain.platform.web.SpecialLinkType;
import fintech.spain.platform.web.model.SpecialLink;
import fintech.spain.platform.web.spi.SpecialLinkService;
import fintech.spain.alfa.product.web.WebAuthorities;
import fintech.spain.alfa.product.web.WebLoginService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

import static fintech.spain.platform.web.model.command.SpecialLinkQuery.byToken;

@RestController
public class SpecialLinkApi {

    private final WebLoginService webLoginService;
    private final SpecialLinkService slService;

    public SpecialLinkApi(
        WebLoginService webLoginService,
        SpecialLinkService slService
    ) {
        this.webLoginService = webLoginService;
        this.slService = slService;
    }

    @PostMapping("/api/public/web/special-link/activate/{token}")
    public LoginResponse activate(@PathVariable String token) {
        SpecialLink link = slService.activateLink(token);

        // Do autologin if it required by link
        String userToken = null;
        if (link.isAutoLoginRequired()) {
            userToken = webLoginService.login(link.getClientId(), Duration.ofHours(2), roleBySpecialLinkType(link.getType()));
        }
        return new LoginResponse(userToken);
    }

    @GetMapping("/api/public/web/special-link/data/{token}")
    public SpecialLinkDataResponse getData(@PathVariable String token) {
        return new SpecialLinkDataResponse(slService.findRequiredLink(byToken(token)));
    }

    private String roleBySpecialLinkType(SpecialLinkType type) {
        if (SpecialLinkType.ADD_PAYMENT.equals(type))
            return WebAuthorities.WEB_PAYMENT_ONLY;
        if (SpecialLinkType.LOC_SPECIAL_OFFER.equals(type))
            return WebAuthorities.WEB_FULL;
        throw new IllegalArgumentException("Unknown authority for special link type " + type);
    }

}
