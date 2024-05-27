package fintech.payments

import fintech.payments.db.InstitutionAccountEntity
import fintech.payments.db.InstitutionEntity
import fintech.payments.db.StatementEntity
import fintech.payments.db.StatementRowEntity
import fintech.testing.integration.TestDatabase

class GenerateDbSchema {

    public static void main(String[] args) {
        TestDatabase.generateDdlWithHibernateToStdout(InstitutionEntity.class, InstitutionAccountEntity.class, StatementEntity.class, StatementRowEntity.class)
    }
}
