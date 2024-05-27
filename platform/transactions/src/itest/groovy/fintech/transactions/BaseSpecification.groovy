package fintech.transactions

import fintech.testing.integration.AbstractBaseSpecification
import org.springframework.beans.factory.annotation.Autowired

abstract class BaseSpecification extends AbstractBaseSpecification {

    @Autowired
    TransactionService transactionService

    def setup() {
        testDatabase.cleanDb()
    }

    void assertZeroBalance(BalanceQuery balanceQuery) {
        assert transactionService.getBalance(balanceQuery) == new Balance()
    }
}
