package fintech.workflow

import fintech.db.impl.AuditedRevisionEntity
import fintech.testing.integration.TestDatabaseFactory
import fintech.workflow.db.TriggerEntity

class GenerateSchema {

    static void main(String[] args) {
        TestDatabaseFactory.generateDdlWithHibernateToStdout(AuditedRevisionEntity.class, TriggerEntity.class)
    }
}
