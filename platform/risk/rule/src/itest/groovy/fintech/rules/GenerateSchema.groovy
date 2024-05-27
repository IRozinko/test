package fintech.rules

import fintech.rules.db.RuleLogEntity
import fintech.rules.db.RuleSetLogEntity
import fintech.testing.integration.TestDatabase

class GenerateSchema {

    public static void main(String[] args) {
        TestDatabase.generateDdlWithHibernateToStdout(RuleSetLogEntity.class, RuleLogEntity.class)
    }
}
