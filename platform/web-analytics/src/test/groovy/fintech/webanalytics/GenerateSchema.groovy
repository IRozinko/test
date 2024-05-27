package fintech.webanalytics

import fintech.db.impl.AuditedRevisionEntity
import fintech.testing.integration.TestDatabaseFactory
import fintech.webanalytics.db.WebAnalyticsEventEntity

class GenerateSchema {

    static void main(String[] args) {
        TestDatabaseFactory.generateDdlWithHibernateToStdout(AuditedRevisionEntity.class, WebAnalyticsEventEntity.class)
    }

}
