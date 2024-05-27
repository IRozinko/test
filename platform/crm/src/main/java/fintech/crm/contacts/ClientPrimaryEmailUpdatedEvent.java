package fintech.crm.contacts;

import lombok.Value;

@Value
public class ClientPrimaryEmailUpdatedEvent {

    private final EmailContact emailContact;
}
