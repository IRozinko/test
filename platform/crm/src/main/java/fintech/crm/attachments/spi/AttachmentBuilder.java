package fintech.crm.attachments.spi;

public class AttachmentBuilder {

    private final AttachmentDefinition definition;

    public AttachmentBuilder(String group, String name) {
        definition = new AttachmentDefinition(group, name);
    }

    public AttachmentBuilder statuses(String validStatus, String... invalidStatuses) {
        definition.statuses(validStatus, invalidStatuses);
        return this;
    }

    public AttachmentDefinition build() {
        return definition;
    }

    public AttachmentBuilder order(int order) {
        definition.order(order);
        return this;
    }
}
