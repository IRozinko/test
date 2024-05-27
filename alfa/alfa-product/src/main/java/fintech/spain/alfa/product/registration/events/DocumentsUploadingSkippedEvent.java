package fintech.spain.alfa.product.registration.events;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@Deprecated
public class DocumentsUploadingSkippedEvent implements ClientDataUpdatedEvent {

    private Long clientId;
}
