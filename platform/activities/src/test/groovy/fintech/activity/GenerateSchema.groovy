package fintech.activity

import fintech.activity.db.ActivityLogEntity
import fintech.db.impl.AuditedRevisionEntity
import fintech.testing.integration.TestDatabaseFactory

class GenerateSchema {

    static void main(String[] args) {
        TestDatabaseFactory.generateDdlWithHibernateToStdout(AuditedRevisionEntity.class, ActivityLogEntity.class)
    }

}
