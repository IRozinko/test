package fintech.spain.crosscheck

import fintech.db.impl.AuditedRevisionEntity
import fintech.spain.crosscheck.db.SpainCrosscheckLogEntity
import fintech.testing.integration.TestDatabaseFactory

class GenerateSchema {

    static void main(String[] args) {
        TestDatabaseFactory.generateDdlWithHibernateToStdout(AuditedRevisionEntity.class, SpainCrosscheckLogEntity.class)
    }

}
