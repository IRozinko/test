package fintech.dowjones.events;

import lombok.Getter;

@Getter
public class DowJonesResponseFailed extends AbstractDowJonesEvent {

    public DowJonesResponseFailed(Long id, Long clientId) {
        super(id, clientId);
    }
}
