package fintech.spain.unnax.event;

import lombok.Getter;

@Getter
public abstract class CallbackEvent {

    private String responseId;

    public CallbackEvent(String responseId) {
        this.responseId = responseId;
    }
}
