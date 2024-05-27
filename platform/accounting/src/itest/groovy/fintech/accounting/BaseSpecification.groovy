package fintech.accounting

import fintech.testing.integration.AbstractBaseSpecification
import fintech.transactions.TransactionService
import org.springframework.beans.factory.annotation.Autowired

abstract class BaseSpecification extends AbstractBaseSpecification {

    public static final String RECEIVABLES_PRINCIPAL = "2315"
    public static final String BANK_A = "2621"
    public static final String BANK_B = "2631"
    public static final String RECEIVABLES_INTEREST = "2311"
    public static final String INTEREST = "6216"
    public static final String REGISTRATION_FEE = "6100"
    public static final String ACCOUNTS_PAYABLE_PARTNER = "5100"


    public static final String ACCOUNT_A = "A"
    public static final String ACCOUNT_B = "B"
    public static final String ACCOUNT_C = "C"

    @Autowired
    AccountingService accountingService

    @Autowired
    AccountingReports accountingReports

    @Autowired
    TransactionService transactionService

    def setup() {
        testDatabase.cleanDb()

        accountingService.addAccount(new AddAccountCommand(code: RECEIVABLES_PRINCIPAL, name: "Receivables principal"))
        accountingService.addAccount(new AddAccountCommand(code: BANK_A, name: "Bank A"))
        accountingService.addAccount(new AddAccountCommand(code: BANK_B, name: "Bank B"))
        accountingService.addAccount(new AddAccountCommand(code: RECEIVABLES_INTEREST, name: "Receivables interest"))
        accountingService.addAccount(new AddAccountCommand(code: INTEREST, name: "Interest"))
        accountingService.addAccount(new AddAccountCommand(code: REGISTRATION_FEE, name: "Registration fee"))
        accountingService.addAccount(new AddAccountCommand(code: ACCOUNTS_PAYABLE_PARTNER, name: "Accounts payable partner"))

        accountingService.addAccount(new AddAccountCommand(code: ACCOUNT_A, name: "Account A"))
        accountingService.addAccount(new AddAccountCommand(code: ACCOUNT_B, name: "Account B"))
        accountingService.addAccount(new AddAccountCommand(code: ACCOUNT_C, name: "Account C"))
    }
}
