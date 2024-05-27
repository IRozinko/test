package fintech.spain.alfa.product.registration.events;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DocumentsUploadedEvent implements ClientDataUpdatedEvent {

    private Long clientId;
}
