package fintech.dowjones.events;

public class AbstractDowJonesEvent {

    private final Long id;
    private final Long clientId;

    public AbstractDowJonesEvent(Long id, Long clientId) {
        this.id = id;
        this.clientId = clientId;
    }

    public Long getId() {
        return id;
    }

    public Long getClientId() {
        return clientId;
    }
}
