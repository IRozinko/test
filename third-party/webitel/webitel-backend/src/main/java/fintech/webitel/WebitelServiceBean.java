package fintech.webitel;

import fintech.webitel.model.WebitelAuthToken;
import fintech.webitel.model.WebitelCallResult;
import fintech.webitel.model.WebitelLoginCommand;
import fintech.webitel.model.WebitelCallCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
class WebitelServiceBean implements WebitelService {

    @Resource(name = "${webitel.provider:" + WebitelApiProperties.MOCK_PROVIDER_NAME + "}")
    private WebitelProvider webitelProvider;

    @Override
    public WebitelCallResult originateNewCall(WebitelCallCommand command) {
        log.info("originating webitel call: {}", command);
        return webitelProvider.originateNewCall(command);
    }

    @Override
    public WebitelAuthToken authenticate(WebitelLoginCommand command) {
        log.info("authenticating in webitel: {}", command);
        return webitelProvider.authenticate(command);
    }
}
