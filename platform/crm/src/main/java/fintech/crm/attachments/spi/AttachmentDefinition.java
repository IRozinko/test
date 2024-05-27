package fintech.crm.attachments.spi;

import com.google.common.collect.ImmutableSet;
import fintech.crm.attachments.AttachmentSubType;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class AttachmentDefinition {

    private final String type;
    private AttachmentSubType subType;
    private final String group;
    private final Set<String> statuses = new HashSet<>();
    private final Set<String> invalidStatuses = new HashSet<>();
    private String validStatus;
    private int order = 0;

    public AttachmentDefinition(String group, String type) {
        this.group = group;
        this.type = type;
    }


    public String getType() {
        return type;
    }

    public AttachmentSubType getSubType() {
        return subType;
    }

    public String getGroup() {
        return group;
    }

    public Set<String> getStatuses() {
        return ImmutableSet.copyOf(statuses);
    }

    void order(int order) {
        this.order = order;
    }

    void subType(AttachmentSubType subType){
        this.subType = subType;
    }

    public int getOrder() {
        return order;
    }

    void statuses(String validStatus, String... invalidStatuses) {
        this.statuses.add(validStatus);
        this.validStatus = validStatus;
        Collections.addAll(this.statuses, invalidStatuses);
        Collections.addAll(this.invalidStatuses, invalidStatuses);

    }

    public Set<String> getInvalidStatuses() {
        return ImmutableSet.copyOf(invalidStatuses);
    }

    public String getValidStatus() {
        return validStatus;
    }

}
