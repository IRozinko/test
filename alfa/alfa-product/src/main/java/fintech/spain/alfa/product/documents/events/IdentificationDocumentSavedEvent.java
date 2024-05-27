package fintech.spain.alfa.product.documents.events;

import fintech.spain.alfa.product.registration.events.ClientDataUpdatedEvent;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class IdentificationDocumentSavedEvent implements ClientDataUpdatedEvent {
    private Long clientId;
}
