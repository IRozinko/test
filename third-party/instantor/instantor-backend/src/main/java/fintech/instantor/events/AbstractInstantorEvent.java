package fintech.instantor.events;

public class AbstractInstantorEvent {

    private final Long id;
    private final Long clientId;

    public AbstractInstantorEvent(Long id, Long clientId) {
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
