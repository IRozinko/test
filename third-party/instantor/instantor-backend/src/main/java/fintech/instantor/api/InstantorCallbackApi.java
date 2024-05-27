package fintech.instantor.api;

import com.google.common.base.Throwables;
import com.instantor.api.InstantorException;
import com.instantor.api.InstantorParams;
import fintech.instantor.InstantorService;
import fintech.instantor.model.InstantorResponseStatus;
import fintech.instantor.model.SaveInstantorResponseCommand;
import lombok.extern.slf4j.Slf4j;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.RetryPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import javax.persistence.OptimisticLockException;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

@Slf4j
@Controller
public class InstantorCallbackApi {


    public static final String API_PATH = "/api/public/web/instantor";

    public static final String PARAMETER_SOURCE = "source";
    public static final String PARAMETER_MESSAGE_ID = "msg_id";
    public static final String PARAMETER_ACTION = "action";
    public static final String PARAMETER_ENCRYPTION = "encryption";
    public static final String PARAMETER_PAYLOAD = "payload";
    public static final String PARAMETER_TIMESTAMP = "timestamp";
    public static final String PARAMETER_HASH = "hash";

    @Value("${instantor.apiKey:LWQ0ZWo0dCM+a11QKn1COSpjOjI+M15I}")
    private String apiKey;

    @Value("${instantor.apiKeyBase64Encoded:true}")
    private boolean apiKeyBase64Encoded;

    @Autowired
    private InstantorService instantorService;

    @Value("${instantor.simulationEnabled:false}")
    private boolean instantorSimulationEnabled;

    private final RetryPolicy retryPolicy = new RetryPolicy()
        .retryOn(OptimisticLockException.class)
        .withMaxRetries(5)
        .withDelay(1, TimeUnit.SECONDS);

    @PostMapping(path = API_PATH, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> post(HttpServletRequest request) {

        String source = request.getParameter(PARAMETER_SOURCE);
        String messageId = request.getParameter(PARAMETER_MESSAGE_ID);
        String action = request.getParameter(PARAMETER_ACTION);
        String encryption = request.getParameter(PARAMETER_ENCRYPTION);
        String payload = request.getParameter(PARAMETER_PAYLOAD);
        String timestamp = request.getParameter(PARAMETER_TIMESTAMP);
        String hash = request.getParameter(PARAMETER_HASH);

        if (instantorSimulationEnabled) {
            log.warn("Instantor simulation enabled, callback ignored");
            return ResponseEntity.ok("OK: " + messageId);
        }

        log.info("Received Instantor request, message id: [{}]", messageId);
        log.debug("Received Instantor request. source: [{}], messageId: [{}], action: [{}], encryption: [{}], payload: [{}], timestamp: [{}], hash: [{}]",
            source, messageId, action, encryption, payload, timestamp, hash);

        SaveInstantorResponseCommand command = new SaveInstantorResponseCommand();
        command.setParamMessageId(messageId);
        command.setParamSource(source);
        command.setParamTimestamp(timestamp);

        Long responseId;
        try {
            String apiKey = apiKey();
            String decryptedPayload = InstantorParams.loadResponse(source,
                apiKey,
                messageId,
                action,
                encryption,
                payload,
                timestamp,
                hash);

            command.setStatus(InstantorResponseStatus.OK);
            command.setPayloadJson(decryptedPayload);
            log.debug("Decrypted Instantor payload:\n{}", decryptedPayload);
            responseId = instantorService.saveResponse(command);
        } catch (InstantorException ex) {
            log.error("Failed to handle Instantor request", ex);
            command.setStatus(InstantorResponseStatus.FAILED);
            command.setError(ex.getMessage() + ", " + Throwables.getRootCause(ex).getMessage());
            instantorService.saveResponse(command);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

        // process in a separate transaction
        try {
            // 'hacky' solution for cases when it throws OptimisticLockException
            Failsafe.with(retryPolicy).run(() -> {
                instantorService.processResponse(responseId);
                log.info("Instantor response [{}] processed", responseId);
            });
        } catch (Exception e) {
            log.error("Failed to process Instantor response [" + responseId + "]", e);
            instantorService.processingFailed(responseId);
        }
        return ResponseEntity.ok("OK: " + messageId);
    }

    private String apiKey() {
        if (apiKeyBase64Encoded) {
            return new String(Base64.getDecoder().decode(apiKey), StandardCharsets.UTF_8);
        } else {
            return apiKey;
        }
    }

    public static void main(String[] args) {
        System.out.println(Base64.getEncoder().encodeToString("-d4ej4t#>k]P*}B9*c:2>3^H".getBytes(StandardCharsets.UTF_8)));
    }
}
