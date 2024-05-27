package fintech.task.db;

public class Entities {

    public static final String SCHEMA = "task";

    public static final QTaskEntity task = QTaskEntity.taskEntity;
    public static final QTaskLogEntity taskLog = QTaskLogEntity.taskLogEntity;
    public static final QAgentEntity agent = QAgentEntity.agentEntity;

}
