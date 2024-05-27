package fintech.workflow.db;

public class Entities {

    public static final String SCHEMA = "workflow";

    public static final QActivityEntity activity = QActivityEntity.activityEntity;
    public static final QWorkflowEntity workflow = QWorkflowEntity.workflowEntity;
    public static final QTriggerEntity trigger = QTriggerEntity.triggerEntity;
}
