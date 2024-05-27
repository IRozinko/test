package fintech.spain.unnax.callback;

import fintech.Validate;
import fintech.spain.unnax.UnnaxCallbackService;
import fintech.spain.unnax.callback.model.CallbackRequest;
import fintech.spain.unnax.model.WebHookEvents;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor

public class UnnaxCallbackApi {

    public static final String UNNAX_CALLBACK_ENDPOINT = "/api/public/unnax/";

    private final UnnaxCallbackService unnaxCallbackService;

    @PostMapping(UNNAX_CALLBACK_ENDPOINT + "{event}")
    public ResponseEntity processCallback(@PathVariable("event") String eventName,
                                          @RequestBody CallbackRequest request) {
        Validate.isTrue(unnaxCallbackService.isSignatureValid(request), "Callback request signature is invalid, {%s}", request);

        unnaxCallbackService.save(request);

        WebHookEvents event = WebHookEvents.findEvent(eventName);
        unnaxCallbackService.publishEvent(event.produceEvent(request));

        return ResponseEntity.noContent().build();
    }

}
