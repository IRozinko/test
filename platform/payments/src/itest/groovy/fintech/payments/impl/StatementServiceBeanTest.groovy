package fintech.payments.impl

import fintech.DateUtils
import fintech.TimeMachine
import fintech.payments.BaseSpecification
import fintech.payments.MockStatementParser
import fintech.payments.commands.AddPaymentCommand
import fintech.payments.db.StatementEntity
import fintech.payments.model.PaymentType
import fintech.payments.model.StatementParseResult
import fintech.payments.model.StatementRow
import fintech.payments.model.StatementRowStatus
import fintech.payments.model.StatementStatus
import fintech.spain.unnax.event.TransferAutoProcessedEvent
import fintech.spain.unnax.transfer.model.TransferAutoDetails
import org.springframework.beans.factory.annotation.Autowired

class StatementServiceBeanTest extends BaseSpecification {

    @Autowired
    StatementServiceBean statementService

    def "Upload statement with payment bank_order_code that already exists"() {
        given:
        def payment = paymentService.addPayment(AddPaymentCommand.fromUnnaxEvent(institution.accounts.get(0).id,
            PaymentType.OUTGOING, TransferAutoProcessedEvent.success(
            TransferAutoDetails.builder()
                .orderCode("1")
                .bankOrderCode("bankorder1")
                .sourceAccount("123-primary")
                .amount(1000)
                .build()
        )))

        expect:
        paymentService.getPayment(payment).valueDate == TimeMachine.today()

        def result = prepareUnnaxStatementData()

        def statementEntity = new StatementEntity()
        statementEntity.setInstitutionId(institution.getId())
        statementEntity.setFormat(institution.getStatementImportFormat())
        statementEntity.setFileId(123L)
        statementEntity.setFileName("123.csv")

        def statementId = statementService.saveStatement(statementEntity, result).id

        when:
        statementService.processStatement(statementId)

        def statement = statementService.findStatement(statementId).get()
        def statementRows = statementService.findStatementRows(statementId)

        then:
        statementRows.size() == 1
        statement.status == StatementStatus.PROCESSED

        statementRows[0].status == StatementRowStatus.IGNORED

        and:
        paymentService.getPayment(payment).valueDate == DateUtils.date("2017-01-03")
    }

    def prepareUnnaxStatementData() {
        def result = new StatementParseResult()
        result.accountNumber = "123-primary"
        result.startDate = DateUtils.date("2017-01-03")
        result.endDate = DateUtils.date("2017-02-03")

        def row1 = new StatementRow()
        row1.setUniqueKey("1")
        row1.valueDate = DateUtils.date("2017-01-03")
        row1.date = DateUtils.date("2017-01-02")
        row1.accountNumber = "123-primary"
        row1.transactionCode = "IN"
        row1.counterpartyAccount = "111"
        row1.counterpartyName = "Payer Name"
        row1.counterpartyAddress = "Address"
        row1.description = "Transferencia emitida Jon Doe UNX0373 bankorder1 Unnax test"
        row1.reference = ""
        row1.amount = 100.00g
        row1.currency = "EUR"
        row1.balance = 1000.00g
        row1.status = StatementRowStatus.NEW
        row1.suggestedTransactionSubType = "SUB_TYPE"
        row1.sourceJson = "[1,2]"
        row1.attributes["attribute-a"] = "test"
        result.setRows([row1])
        MockStatementParser.setResult(result)
        return result
    }
}
