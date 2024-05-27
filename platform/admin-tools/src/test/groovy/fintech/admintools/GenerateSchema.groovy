package fintech.admintools

import fintech.admintools.db.AdminActionLogEntity
import fintech.testing.integration.TestDatabaseFactory

class GenerateSchema {

    static void main(String[] args) {
        TestDatabaseFactory.generateDdlWithHibernateToStdout(AdminActionLogEntity.class)
    }

}
