package fintech.crm.attachments.impl;

import fintech.Validate;
import fintech.crm.attachments.spi.AttachmentDefinition;
import fintech.crm.attachments.spi.ClientAttachmentRegistry;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Component
public class ClientAttachmentRegistryBean implements ClientAttachmentRegistry {

    /**
     * Using TreeMap to keep order
     */
    private Map<String, AttachmentDefinition> definitionsByType = new TreeMap<>();

    @Override
    public void addDefinition(AttachmentDefinition definition) {
        definitionsByType.put(definition.getType(), definition);
    }

    @Override
    public AttachmentDefinition getDefinition(String type) {
        AttachmentDefinition definition = definitionsByType.get(type);
        return Validate.notNull(definition, "Attachment definition not found by name: %s", type);
    }

    @Override
    public boolean hasDefinition(String type) {
        return definitionsByType.containsKey(type);
    }

    @Override
    public List<AttachmentDefinition> getByGroup(String group) {
        return definitionsByType.values().stream().filter((d) -> d.getGroup().equals(group)).collect(Collectors.toList());
    }
}
