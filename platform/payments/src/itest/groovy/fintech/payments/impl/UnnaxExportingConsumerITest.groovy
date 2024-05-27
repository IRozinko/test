package fintech.payments.impl

import fintech.TimeMachine
import fintech.crm.bankaccount.AddClientBankAccountCommand
import fintech.crm.bankaccount.ClientBankAccountService
import fintech.crm.client.ClientService
import fintech.crm.client.CreateClientCommand
import fintech.crm.client.UpdateClientCommand
import fintech.crm.client.util.ClientNumberGenerator
import fintech.payments.BaseSpecification
import fintech.payments.DisbursementService
import fintech.payments.commands.AddDisbursementCommand
import fintech.payments.commands.AddPaymentCommand
import fintech.payments.model.Disbursement
import fintech.payments.model.DisbursementStatusDetail
import fintech.payments.model.PaymentType
import fintech.spain.unnax.UnnaxPayOutService
import fintech.spain.unnax.db.DisbursementQueueRepository
import fintech.spain.unnax.db.DisbursementQueueStatus
import fintech.spain.unnax.db.Entities
import fintech.spain.unnax.transfer.impl.MockTransferAutoUnnaxClient
import fintech.transactions.AddTransactionCommand
import fintech.transactions.TransactionService
import fintech.transactions.TransactionType
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Subject

import java.time.LocalDate

class UnnaxExportingConsumerITest extends BaseSpecification {

    static final String IBAN = "ES6821000844240200657804"

    @Subject
    @Autowired
    UnnaxExportingConsumer consumer

    @Autowired
    UnnaxDisbursementProcessorBean processorBean

    @Autowired
    DisbursementService disbursementService

    @Autowired
    UnnaxPayOutService unnaxService

    @Autowired
    ClientService clientService

    @Autowired
    ClientBankAccountService clientBankAccountService

    @Autowired
    ClientNumberGenerator clientNumberGenerator

    @Autowired
    DisbursementQueueRepository disbursementQueueRepository

    @Autowired
    TransactionService transactionService

    def "Accept"() {
        given:
        AddDisbursementCommand command = buildCommand()
        def disbursementId = disbursementService.add(command)

        when:
        processorBean.exportSingleDisbursement(disbursementId)

        then:
        !unnaxService.getTransferOutQueue(TimeMachine.now()).isEmpty()

        when:
        consumer.accept(TimeMachine.now())

        then:
        unnaxService.getTransferOutQueue(TimeMachine.now()).isEmpty()
        disbursementQueueRepository.findAll().each {
            it.status == DisbursementQueueStatus.SUCCESS
            it.attempts == 1
        }

    }

    def "Export failed"() {
        given:
        AddDisbursementCommand command = buildCommand()
        def disbursementId = disbursementService.add(command)
        MockTransferAutoUnnaxClient.THROW_EXCEPTION = true

        when:
        processorBean.exportSingleDisbursement(disbursementId)
        consumer.accept(TimeMachine.now())

        then:
        disbursementQueueRepository.findAll().each {
            it.status == DisbursementQueueStatus.ERROR
            it.attempts == 1
        }

        cleanup:
        MockTransferAutoUnnaxClient.THROW_EXCEPTION = false
    }

    def "Successfully export disbursement after retry when happened export error"() {
        given:
        AddDisbursementCommand command = buildCommand()
        def disbursementId = disbursementService.add(command)
        def accountId = institutionService.getPrimaryInstitution().getPrimaryAccount().getId()
        def paymentId = paymentService.addPayment(new AddPaymentCommand(valueDate: TimeMachine.today(), amount: 100.0, paymentType: PaymentType.INCOMING, details: "nothing interesting", accountId: accountId, key: RandomStringUtils.randomAlphanumeric(10)))
        def payment = paymentService.getPayment(paymentId)

        transactionService.addTransaction(new AddTransactionCommand(
            loanId: command.loanId,
            disbursementId: disbursementId,
            transactionType: TransactionType.DISBURSEMENT,
            cashIn: command.amount,
            paymentId: payment.id,
            institutionAccountId: institution.primaryAccount.id,
            institutionId: institution.id,
            principalPaid: command.amount,
            valueDate: command.valueDate,
            bookingDate: command.valueDate,
        ))

        when:
        processorBean.exportSingleDisbursement(disbursementId)
        then:
        disbursementQueueRepository.findAll().each {
            it.status == DisbursementQueueStatus.NEW
            it.attempts == 0
        }

        when:
        MockTransferAutoUnnaxClient.THROW_EXCEPTION = true
        consumer.accept(TimeMachine.now())

        then:
        disbursementQueueRepository.findAll().each {
            it.status == DisbursementQueueStatus.ERROR
            it.attempts == 1
        }
        Disbursement disbursement = disbursementService.getDisbursement(disbursementId)
        disbursement.statusDetail == DisbursementStatusDetail.EXPORT_ERROR

        when:
        processorBean.retrySingleDisbursement(disbursementId)

        then:
        disbursementService.getDisbursement(disbursement.getId()).statusDetail == DisbursementStatusDetail.PENDING

        when:
        MockTransferAutoUnnaxClient.THROW_EXCEPTION = false
        processorBean.exportSingleDisbursement(disbursement.getId())
        disbursement = disbursementService.getDisbursement(disbursementId)

        then:
        assert disbursement.statusDetail == DisbursementStatusDetail.EXPORTED
        disbursementQueueRepository.findAll(Entities.disbursementQueue.disbursementId.eq(disbursementId).and(
            Entities.disbursementQueue.status.eq(DisbursementQueueStatus.NEW))).each {
            it.status == DisbursementQueueStatus.NEW
            it.attempts == 0
        }

        when:
        consumer.accept(TimeMachine.now())
        disbursementService.settled(disbursement.getId())

        then:
        disbursementQueueRepository.findAll(Entities.disbursementQueue.disbursementId.eq(disbursementId).and(
            Entities.disbursementQueue.status.eq(DisbursementQueueStatus.SUCCESS))).each {
            it.status == DisbursementQueueStatus.SUCCESS
            it.attempts == 1
        }
    }

    private AddDisbursementCommand buildCommand() {
        def clientId = clientService.create(new CreateClientCommand(
            clientNumberGenerator.newNumber("T", 5)
        ))
        clientService.update(new UpdateClientCommand(clientId: clientId, firstName: "Test", lastName: "LastName"))
        clientBankAccountService.addBankAccount(new AddClientBankAccountCommand(clientId: clientId, accountNumber: IBAN, primaryAccount: true))
        def command = new AddDisbursementCommand()
        command.amount = 100
        command.clientId = clientId
        command.loanId = 2L
        command.institutionId = unnaxIxnstitution.id
        command.institutionAccountId = unnaxIxnstitution.primaryAccount.id
        command.valueDate = LocalDate.now()
        command.reference = "AA123"
        return command
    }

}
