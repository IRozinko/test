package fintech.payments.impl

import fintech.BigDecimalUtils
import fintech.TimeMachine
import fintech.crm.bankaccount.ClientBankAccount
import fintech.crm.bankaccount.ClientBankAccountService
import fintech.crm.client.Client
import fintech.crm.client.ClientService
import fintech.payments.DisbursementService
import fintech.payments.model.Disbursement
import fintech.spain.unnax.UnnaxPayOutService
import fintech.spain.unnax.db.DisbursementQueueEntity
import fintech.spain.unnax.db.DisbursementQueueStatus
import fintech.spain.unnax.transfer.model.TransferAutoRequest
import org.springframework.transaction.TransactionException
import org.springframework.transaction.support.SimpleTransactionStatus
import org.springframework.transaction.support.TransactionCallback
import org.springframework.transaction.support.TransactionTemplate
import spock.lang.Specification
import spock.lang.Subject

class UnnaxExportingConsumerTest extends Specification {

    @Subject
    UnnaxExportingConsumer consumer

    DisbursementService disbursementService
    UnnaxPayOutService unnaxService
    ClientService clientService
    ClientBankAccountService clientBankAccountService
    TransactionTemplate tx

    def "setup"() {
        disbursementService = Mock(DisbursementService)
        unnaxService = Mock(UnnaxPayOutService)
        clientService = Mock(ClientService)
        clientBankAccountService = Mock(ClientBankAccountService)
        tx = new TransactionTemplate() {
            @Override
            <T> T execute(TransactionCallback<T> action) throws TransactionException {
                return action.doInTransaction(new SimpleTransactionStatus())
            }
        }

        consumer = new UnnaxExportingConsumer(disbursementService, unnaxService, clientService, clientBankAccountService, tx)
    }

    def "Accept"() {
        given:
        def now = TimeMachine.now()
        def disbursement = new Disbursement(id: 1L, amount: 100.00)

        when:
        consumer.accept(now)

        then:
        1 * unnaxService.getTransferOutQueue(now) >> [new DisbursementQueueEntity(id: 1L, disbursementId: 1L)]
        1 * disbursementService.getDisbursement(_ as Long) >> disbursement
        1 * clientService.get(disbursement.getClientId()) >> new Client(number: "1", firstName: "James", "lastName": "Bond")
        1 * clientBankAccountService.findPrimaryByClientId(disbursement.getClientId()) >> Optional.of(new ClientBankAccount(accountNumber: "123"))
        1 * unnaxService.transferOut(_ as TransferAutoRequest) >> Optional.empty()
        1 * disbursementService.exportError(1L, "Error during sending request to Unnax")
        1 * unnaxService.addAttempt(1L, DisbursementQueueStatus.ERROR)
    }

    def "ToTransferRequest"() {
        given:
        def disbursement = new Disbursement(id: 1, clientId: 1L, amount: 100.00, reference: "p-123-p")

        when:
        def req = consumer.toTransferRequest(disbursement)

        then:
        1 * clientService.get(disbursement.getClientId()) >> new Client(number: "1", firstName: "James", "lastName": "Bond")
        1 * clientBankAccountService.findPrimaryByClientId(disbursement.getClientId()) >> Optional.of(new ClientBankAccount(accountNumber: "123"))
        req.amount == BigDecimalUtils.multiplyByHundred(disbursement.getAmount())
        req.destinationAccount == "123"
        req.customerCode == "1"
        req.customerNames == "James Bond"
        req.orderCode == "p-123-p"
        req.bankOrderCode == "1"
    }

    def "ToTransferRequest - No bank account"() {
        given:
        def disbursement = new Disbursement(clientId: 1L, amount: 100.00)

        when:
        def req = consumer.toTransferRequest(disbursement)

        then:
        1 * clientService.get(disbursement.getClientId()) >> new Client(number: "1", firstName: "James", "lastName": "Bond")
        1 * clientBankAccountService.findPrimaryByClientId(disbursement.getClientId()) >> Optional.empty()
        thrown IllegalArgumentException
    }

}
