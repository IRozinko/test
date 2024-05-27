package fintech.dc

import fintech.db.impl.AuditedRevisionEntity
import fintech.dc.db.*
import fintech.testing.integration.TestDatabaseFactory

class GenerateSchema {

    static void main(String[] args) {
        TestDatabaseFactory.generateDdlWithHibernateToStdout(AuditedRevisionEntity.class, DebtActionEntity.class, DebtEntity.class, DcSettingsEntity.class, DcAgentEntity.class, DcAgentAbsenceEntity.class)
    }

}
