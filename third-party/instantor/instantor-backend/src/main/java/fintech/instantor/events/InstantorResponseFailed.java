package fintech.instantor.events;

import lombok.Getter;

@Getter
public class InstantorResponseFailed extends AbstractInstantorEvent {

    public InstantorResponseFailed(Long id, Long clientId) {
        super(id, clientId);
    }
}
