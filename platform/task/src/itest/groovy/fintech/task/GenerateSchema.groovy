package fintech.task

import fintech.db.impl.AuditedRevisionEntity
import fintech.task.db.AgentEntity
import fintech.task.db.TaskEntity
import fintech.task.db.TaskLogEntity
import fintech.testing.integration.TestDatabaseFactory

class GenerateSchema {

    static void main(String[] args) {
        TestDatabaseFactory.generateDdlWithHibernateToStdout(AuditedRevisionEntity.class, TaskEntity.class, TaskLogEntity.class, AgentEntity.class)
    }

}
