package fintech.dowjones.events;

import lombok.Getter;

@Getter
public class DowJonesResponseProcessed extends AbstractDowJonesEvent {

    public DowJonesResponseProcessed(Long id, Long clientId) {
        super(id, clientId);
    }
}
