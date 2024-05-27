package fintech.crm.attachments.spi;

import java.util.List;

public interface ClientAttachmentRegistry {

    void addDefinition(AttachmentDefinition definition);

    AttachmentDefinition getDefinition(String type);

    boolean hasDefinition(String type);

    List<AttachmentDefinition> getByGroup(String group);
}
