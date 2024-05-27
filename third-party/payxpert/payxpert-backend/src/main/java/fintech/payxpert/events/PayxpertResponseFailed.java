package fintech.payxpert.events;

import lombok.Getter;

@Getter
public class PayxpertResponseFailed extends AbstractPayxpertEvent {

    public PayxpertResponseFailed(Long id, Long clientId) {
        super(id, clientId);
    }
}
