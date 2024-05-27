package fintech.spain.experian

import fintech.db.impl.AuditedRevisionEntity
import fintech.spain.experian.db.CaisDebtEntity
import fintech.spain.experian.db.CaisListOperacionesEntity
import fintech.testing.integration.TestDatabaseFactory

class GenerateSchema {

    static void main(String[] args) {
        TestDatabaseFactory.generateDdlWithHibernateToStdout(AuditedRevisionEntity.class, CaisListOperacionesEntity.class, CaisDebtEntity.class)
    }

}
