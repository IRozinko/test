package fintech.payxpert.api;

import com.google.common.base.Charsets;
import com.payxpert.connect2pay.client.response.CallbackStatusResponse;
import fintech.payxpert.PayxpertService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Controller
public class PayxpertCallbackApi {

    public static final String CALLBACK_PATH = "/api/public/web/payxpert/callback";

    @Autowired
    private PayxpertService service;

    @SneakyThrows
    @PostMapping(path = CALLBACK_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> callback(HttpServletRequest request) {
        String json = IOUtils.toString(request.getInputStream(), Charsets.UTF_8);
        log.info("Received callback: [{}]", json);
        try {
            service.handleCallback(json);
            return new ResponseEntity<>(CallbackStatusResponse.getDefaultSuccessResponse().toJson(), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Failed to handle callback", e);
            return new ResponseEntity<>(CallbackStatusResponse.getDefaultFailureResponse().toJson(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @SneakyThrows
    @PostMapping(path = "/api/public/web/payxpert/redirect", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> redirect() {
        log.info("Received redirect");
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }
}
