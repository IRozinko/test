package fintech.lending

import fintech.db.impl.AuditedRevisionEntity
import fintech.lending.core.loan.db.InstallmentEntity
import fintech.lending.core.loan.db.LoanEntity
import fintech.lending.core.loan.db.PaymentScheduleEntity
import fintech.testing.integration.TestDatabaseFactory

class GenerateSchema {

    public static void main(String[] args) {
        TestDatabaseFactory.generateDdlWithHibernateToStdout(AuditedRevisionEntity.class, PaymentScheduleEntity.class, LoanEntity.class, InstallmentEntity.class)
    }
}
