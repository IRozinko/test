package fintech.payxpert

import fintech.db.impl.AuditedRevisionEntity
import fintech.payxpert.db.PayxpertCreditCardEntity
import fintech.payxpert.db.PayxpertPaymentRequestEntity
import fintech.payxpert.db.PayxpertRebillEntity
import fintech.testing.integration.TestDatabaseFactory

class GenerateSchema {

    static void main(String[] args) {
        TestDatabaseFactory.generateDdlWithHibernateToStdout(AuditedRevisionEntity.class, PayxpertPaymentRequestEntity.class, PayxpertCreditCardEntity.class, PayxpertRebillEntity.class)
    }

}
