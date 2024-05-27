package fintech.instantor.events;

import fintech.instantor.json.common.InstantorCommonResponse;
import lombok.Getter;

@Getter
public class InstantorResponseProcessed  extends AbstractInstantorEvent {

    private InstantorCommonResponse response;

    public InstantorResponseProcessed(Long id, Long clientId, InstantorCommonResponse response) {
        super(id, clientId);
        this.response = response;
    }
}
