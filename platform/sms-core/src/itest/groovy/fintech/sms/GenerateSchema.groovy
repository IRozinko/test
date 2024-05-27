package fintech.sms

import fintech.db.impl.AuditedRevisionEntity
import fintech.sms.db.IncomingSmsEntity
import fintech.testing.integration.TestDatabaseFactory

class GenerateSchema {

    static void main(String[] args) {
        TestDatabaseFactory.generateDdlWithHibernateToStdout(AuditedRevisionEntity.class, IncomingSmsEntity.class)
    }

}
