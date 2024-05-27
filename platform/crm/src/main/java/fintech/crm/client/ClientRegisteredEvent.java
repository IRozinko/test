package fintech.crm.client;

import lombok.Value;

@Value
public class ClientRegisteredEvent {

    private final Client client;
}
