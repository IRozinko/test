package fintech.crm.documents;

import lombok.Value;

@Value
public class ClientPrimaryIdentityDocumentUpdatedEvent {

    private final IdentityDocument identityDocument;
}
