package fintech.spain.unnax;

import fintech.spain.unnax.callback.model.CallbackRequest;
import fintech.spain.unnax.db.CallbackEntity;
import fintech.spain.unnax.event.CallbackEvent;

public interface UnnaxCallbackService {

    CallbackEntity save(CallbackRequest request);

    void publishEvent(CallbackEvent event);

    boolean isSignatureValid(CallbackRequest request);
}
