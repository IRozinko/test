package fintech.spain.alfa.product.lending

import fintech.TimeMachine
import fintech.spain.alfa.product.AbstractAlfaTest
import fintech.spain.alfa.product.lending.spi.AlfaLoanDerivedValuesResolver
import fintech.spain.alfa.product.tasks.StandaloneTasks
import fintech.task.model.TaskQuery
import fintech.task.TaskService
import fintech.task.model.TaskStatus
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Subject

import static fintech.DateUtils.date

class ExtensionSaleCallServiceTest extends AbstractAlfaTest {

    @Subject
    @Autowired
    ExtensionSaleCallService extensionSaleCallService

    @Autowired
    AlfaLoanDerivedValuesResolver alfaLoanDerivedValuesResolver

    @Autowired
    TaskService taskService

    def "loan without installments"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .signUp()
            .addPrimaryBankAccount()
            .issueLoan(100.0, 15, date("2018-11-01"))

        when:
        alfaLoanDerivedValuesResolver.resolveDerivedValues(client.loanId)
        def result = extensionSaleCallService.findInstallmentsWithoutTask()

        then:
        result.isEmpty()
    }

    def "multiple loans without installments"() {
        given:
        def client1 = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .signUp()
            .addPrimaryBankAccount()
            .issueLoan(100.0, 15, date("2018-11-01"))
        def client2 = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .signUp()
            .addPrimaryBankAccount()
            .issueLoan(150.0, 20, date("2018-11-05"))

        when:
        alfaLoanDerivedValuesResolver.resolveDerivedValues(client1.loanId)
        alfaLoanDerivedValuesResolver.resolveDerivedValues(client2.loanId)
        def result = extensionSaleCallService.findInstallmentsWithoutTask()

        then:
        result.isEmpty()
    }

    def "loan with one installment with dpd -5"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .signUp()
            .addPrimaryBankAccount()
            .issueLoan(100.0, 15, date("2018-10-01"))
            .exportDisbursements(date("2018-10-01"))

        when:
        TimeMachine.useFixedClockAt(date("2018-10-10"))
        alfaLoanDerivedValuesResolver.resolveDerivedValues(client.loanId)
        def result = extensionSaleCallService.findInstallmentsWithoutTask()

        then:
        result.isEmpty()
    }

    def "loan with multiple installment with dpd -5"() {
        given:
        def client1 = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .signUp()
            .addPrimaryBankAccount()
            .issueLoan(100.0, 15, date("2018-10-01"))
            .exportDisbursements(date("2018-10-01"))
        def client2 = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .signUp()
            .addPrimaryBankAccount()
            .issueLoan(50.0, 10, date("2018-10-05"))
            .exportDisbursements(date("2018-10-05"))

        when:
        TimeMachine.useFixedClockAt(date("2018-10-10"))
        alfaLoanDerivedValuesResolver.resolveDerivedValues(client1.loanId)
        alfaLoanDerivedValuesResolver.resolveDerivedValues(client2.loanId)
        def result = extensionSaleCallService.findInstallmentsWithoutTask()

        then:
        result.isEmpty()
    }

    def "loan with one installment with dpd -3"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .signUp()
            .addPrimaryBankAccount()
            .issueLoan(100.0, 15, date("2018-10-01"))
            .exportDisbursements(date("2018-10-01"))

        when:
        TimeMachine.useFixedClockAt(date("2018-10-13"))
        alfaLoanDerivedValuesResolver.resolveDerivedValues(client.loanId)
        def result = extensionSaleCallService.findInstallmentsWithoutTask()

        then:
        result.size() == 1
        with(result[0]) {
            loanId == client.loanId
            clientId == client.testClient.clientId
        }
    }

    def "multiple loan with installment with dpd -3"() {
        given:
        def client1 = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .signUp()
            .addPrimaryBankAccount()
            .issueLoan(200.0, 15, date("2018-10-01"))
            .exportDisbursements(date("2018-10-01"))
        def client2 = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .signUp()
            .addPrimaryBankAccount()
            .issueLoan(100.0, 10, date("2018-10-06"))
            .exportDisbursements(date("2018-10-06"))

        when:
        TimeMachine.useFixedClockAt(date("2018-10-13"))
        alfaLoanDerivedValuesResolver.resolveDerivedValues(client1.loanId)
        alfaLoanDerivedValuesResolver.resolveDerivedValues(client2.loanId)
        def result = extensionSaleCallService.findInstallmentsWithoutTask()

        then:
        result.size() == 2
        with(result[0]) {
            loanId == client1.loanId
            clientId == client1.testClient.clientId
        }
        with(result[1]) {
            loanId == client2.loanId
            clientId == client2.testClient.clientId
        }
    }

    def "loan with one installment with dpd 0"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .signUp()
            .addPrimaryBankAccount()
            .issueLoan(100.0, 15, date("2018-10-01"))
            .exportDisbursements(date("2018-10-01"))

        when:
        TimeMachine.useFixedClockAt(date("2018-10-16"))
        alfaLoanDerivedValuesResolver.resolveDerivedValues(client.loanId)
        def result = extensionSaleCallService.findInstallmentsWithoutTask()

        then:
        result.size() == 0
    }

    def "multiple loan with installment with dpd 0"() {
        given:
        def client1 = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .signUp()
            .addPrimaryBankAccount()
            .issueLoan(200.0, 15, date("2018-10-01"))
            .exportDisbursements(date("2018-10-01"))
        def client2 = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .signUp()
            .addPrimaryBankAccount()
            .issueLoan(100.0, 10, date("2018-10-06"))
            .exportDisbursements(date("2018-10-06"))

        when:
        TimeMachine.useFixedClockAt(date("2018-10-16"))
        alfaLoanDerivedValuesResolver.resolveDerivedValues(client1.loanId)
        alfaLoanDerivedValuesResolver.resolveDerivedValues(client2.loanId)
        def result = extensionSaleCallService.findInstallmentsWithoutTask()

        then:
        result.size() == 0
    }

    def "loan with one installment with dpd 10"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .signUp()
            .addPrimaryBankAccount()
            .issueLoan(100.0, 15, date("2018-10-01"))
            .exportDisbursements(date("2018-10-01"))

        when:
        TimeMachine.useFixedClockAt(date("2018-10-26"))
        alfaLoanDerivedValuesResolver.resolveDerivedValues(client.loanId)
        def result = extensionSaleCallService.findInstallmentsWithoutTask()

        then:
        result.size() == 0
    }

    def "multiple loan with installment with dpd 10"() {
        given:
        def client1 = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .signUp()
            .addPrimaryBankAccount()
            .issueLoan(200.0, 15, date("2018-10-01"))
            .exportDisbursements(date("2018-10-01"))
        def client2 = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .signUp()
            .addPrimaryBankAccount()
            .issueLoan(100.0, 10, date("2018-10-06"))
            .exportDisbursements(date("2018-10-06"))

        when:
        TimeMachine.useFixedClockAt(date("2018-10-26"))
        alfaLoanDerivedValuesResolver.resolveDerivedValues(client1.loanId)
        alfaLoanDerivedValuesResolver.resolveDerivedValues(client2.loanId)
        def result = extensionSaleCallService.findInstallmentsWithoutTask()

        then:
        result.size() == 0
    }

    def "tasks for multiple loan with installment with dpd -3"() {
        given:
        def client1 = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .signUp()
            .addPrimaryBankAccount()
            .issueLoan(200.0, 15, date("2018-10-01"))
            .exportDisbursements(date("2018-10-01"))
        def client2 = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .signUp()
            .addPrimaryBankAccount()
            .issueLoan(100.0, 10, date("2018-10-06"))
            .exportDisbursements(date("2018-10-06"))
        TimeMachine.useFixedClockAt(date("2018-10-13"))
        alfaLoanDerivedValuesResolver.resolveDerivedValues(client1.loanId)
        alfaLoanDerivedValuesResolver.resolveDerivedValues(client2.loanId)
        def result = extensionSaleCallService.findInstallmentsWithoutTask()

        when:
        extensionSaleCallService.createTasks(result)
        def tasks = taskService.findTasks(new TaskQuery().setType(StandaloneTasks.ExtensionSaleCall.TYPE))

        then:
        tasks.size() == 2
        with(tasks[0]) {
            loanId == client1.loanId
            clientId == client1.testClient.clientId
            installmentId == result[0].id
            status == TaskStatus.OPEN
        }
        with(tasks[1]) {
            loanId == client2.loanId
            clientId == client2.testClient.clientId
            installmentId == result[1].id
            status == TaskStatus.OPEN
        }
    }
}
