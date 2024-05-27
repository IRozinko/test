package fintech.dowjones

import fintech.db.impl.AuditedRevisionEntity
import fintech.dowjones.db.DowJonesRequestEntity
import fintech.testing.integration.TestDatabaseFactory

class GenerateSchema {

    static void main(String[] args) {
        TestDatabaseFactory.generateDdlWithHibernateToStdout(AuditedRevisionEntity.class, DowJonesRequestEntity.class)
    }

}
