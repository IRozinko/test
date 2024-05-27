package fintech.crm.attachments.event;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AttachmentSavedEvent {
    private Long attachmentId;
    private Long clientId;
    private String type;
}
