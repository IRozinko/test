package fintech.spain.alfa.product.workflow.common;


import fintech.crm.attachments.event.AttachmentSavedEvent;
import fintech.workflow.spi.ActivityTrigger;

public class AttachmentSavedEventTrigger extends ActivityTrigger {

    private final String attachmentType;

    public AttachmentSavedEventTrigger(String attachmentType) {
        super(AttachmentSavedEvent.class);
        this.attachmentType = attachmentType;
    }

    @Override
    public Boolean apply(Object input) {
        return attachmentType.equals(((AttachmentSavedEvent) input).getType());
    }

    public static AttachmentSavedEventTrigger attachmentSavedWithType(String attachmentType) {
        return new AttachmentSavedEventTrigger(attachmentType);
    }
}
