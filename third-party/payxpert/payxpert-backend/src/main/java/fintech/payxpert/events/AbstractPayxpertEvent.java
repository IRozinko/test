package fintech.payxpert.events;

public class AbstractPayxpertEvent {

    private final Long id;
    private final Long clientId;

    public AbstractPayxpertEvent(Long id, Long clientId) {
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
