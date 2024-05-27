package fintech.payments

import fintech.DateUtils
import fintech.filestorage.FileStorageService
import fintech.filestorage.SaveFileCommand
import fintech.payments.commands.StatementImportCommand
import fintech.payments.db.StatementRepository
import fintech.payments.db.StatementRowRepository
import fintech.payments.model.*
import fintech.payments.spi.StatementProcessorRegistry
import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired

class StatementServiceTest extends BaseSpecification {

    @Autowired
    FileStorageService fileStorageService

    @Autowired
    StatementService statementService

    @Autowired
    StatementRepository statementRepository

    @Autowired
    StatementRowRepository statementRowRepository

    @Autowired
    InstitutionService institutionService

    @Autowired
    StatementProcessorRegistry statementParserRegistry

    def "Successfully parse and save statement"() {
        given:
        prepareStatementData()
        def statementFile = uploadFile()
        assert statementFile.fileId > 0
        StatementImportCommand command = new StatementImportCommand(
            fileId: statementFile.fileId,
            institutionId: institution.id)

        when:
        def statementId = statementService.importStatement(command)
        def statement = statementRepository.getRequired(statementId)

        then:
        statement.fileId == statementFile.fileId
        statement.startDate == DateUtils.date("2017-01-02")
        statement.endDate == DateUtils.date("2017-02-02")
        statement.status == StatementStatus.NEW
        statement.error == null

        def statementRows = statementService.findStatementRows(statementId)

        statementRows[0].valueDate == DateUtils.date("2017-01-02")
        statementRows[0].accountNumber == "123-primary"
        statementRows[0].transactionCode == "IN"
        statementRows[0].counterpartyAccount == "111"
        statementRows[0].counterpartyName == "Payer Name"
        statementRows[0].description == "Payment Details 1"
        statementRows[0].reference == ""
        statementRows[0].amount == 100.00g
        statementRows[0].currency == "EUR"
        statementRows[0].balance == 1000.00g
        statementRows[0].status == StatementRowStatus.NEW
        statementRows[0].suggestedTransactionSubType == "SUB_TYPE"
        !StringUtils.isEmpty(statementRows[0].sourceJson)
        statementRows[0].attributes["attribute-a"] == "test"

        statementRows[1].valueDate == DateUtils.date("2017-02-02")
        statementRows[1].accountNumber == "456"
        statementRows[1].transactionCode == "OUT"
        statementRows[1].counterpartyAccount == "222"
        statementRows[1].counterpartyName == "Receiver Name"
        statementRows[1].description == "Payment Details 2"
        statementRows[1].reference == ""
        statementRows[1].amount == -5.00g
        statementRows[1].currency == "EUR"
        statementRows[1].balance == 995.00g
        statementRows[1].status == StatementRowStatus.NEW
        statementRows[1].attributes["attribute-a"] == "test"
    }

    def "Handle row parsing error"() {
        given:
        def result = new StatementParseResult()
        result.error = "Error"
        MockStatementParser.setResult(result)
        def statementFile = uploadFile()
        assert statementFile.fileId > 0
        StatementImportCommand command = new StatementImportCommand(
            fileId: statementFile.fileId,
            institutionId: institution.id)

        when:
        def statementId = statementService.importStatement(command)
        def statement = statementRepository.getRequired(statementId)

        then:
        statement.fileId == statementFile.fileId
        statement.startDate == null
        statement.endDate == null
        statement.status == StatementStatus.FAILED
        statement.error == "Error"
    }

    def "Process statement successfully - create payments"() {
        given:
        prepareStatementData()
        def statementFile = uploadFile()
        assert statementFile.fileId > 0
        StatementImportCommand command = new StatementImportCommand(
            fileId: statementFile.fileId,
            institutionId: institution.id)
        def statementId = statementService.importStatement(command)

        when:
        statementService.processStatement(statementId)
        def statement = statementService.findStatement(statementId).get()
        def statementRows = statementService.findStatementRows(statementId)

        then:
        statement.status == StatementStatus.PROCESSED
        statementRows.size() == 2
        statementRows.countBy { it.status.toString() } == [PROCESSED: 2]

        statementRows[0].paymentId != null
        statementRows[0].status == StatementRowStatus.PROCESSED

        with(paymentService.getPayment(statementRows[0].paymentId)) { payment ->
            assert payment.paymentType == PaymentType.INCOMING
            assert payment.amount == 100.00g
        }

        statementRows[1].paymentId != null
        statementRows[1].status == StatementRowStatus.PROCESSED
        with(paymentService.getPayment(statementRows[1].paymentId)) { payment ->
            assert payment.paymentType == PaymentType.OUTGOING
            assert payment.amount == 5.00g
        }
    }

    def "Process statement and mark duplicates"() {
        given:
        def result = prepareStatementData()
        result.rows[1] = result.rows[0]
        def statementFile = uploadFile()
        assert statementFile.fileId > 0
        StatementImportCommand command = new StatementImportCommand(
            fileId: statementFile.fileId,
            institutionId: institution.id)
        def statementId = statementService.importStatement(command)
        def statementId2 = statementService.importStatement(command)

        when:
        statementService.processStatement(statementId)
        statementService.processStatement(statementId2)

        def statement1 = statementService.findStatement(statementId).get()
        def statementRows1 = statementService.findStatementRows(statementId)
        def statement2 = statementService.findStatement(statementId).get()
        def statementRows2 = statementService.findStatementRows(statementId)

        then:
        statementRows1.size() == 2
        statementRows1.countBy { it.status.toString() } == [PROCESSED: 1, IGNORED: 1]
        statement1.status == StatementStatus.PROCESSED
        statementRows1[0].paymentId == null
        statementRows1[0].status == StatementRowStatus.IGNORED
        statementRows1[1].paymentId != null
        statementRows1[1].status == StatementRowStatus.PROCESSED

        statementRows2.size() == 2
        statementRows2.countBy { it.status.toString() } == [PROCESSED: 1, IGNORED: 1]
        statement2.status == StatementStatus.PROCESSED
        statementRows2[0].paymentId == null
        statementRows2[0].status == StatementRowStatus.IGNORED
        statementRows2[1].paymentId != null
        statementRows2[1].status == StatementRowStatus.PROCESSED
    }

    def "Error status if account not found"() {
        given:
        def result = prepareStatementData()
        result.accountNumber = "XXX"
        def statementFile = uploadFile()
        assert statementFile.fileId > 0
        StatementImportCommand command = new StatementImportCommand(
            fileId: statementFile.fileId,
            institutionId: institution.id)
        def statementId = statementService.importStatement(command)

        when:
        statementService.processStatement(statementId)

        def statement = statementService.findStatement(statementId).get()
        def statementRows = statementService.findStatementRows(statementId)

        then:
        statementRows.size() == 2
        statementRows.countBy { it.status.toString() } == [NEW: 2]
        statement.status == StatementStatus.FAILED

        statementRows[0].status == StatementRowStatus.NEW
        statementRows[0].paymentId == null
        statementRows[1].status == StatementRowStatus.NEW
        statementRows[1].paymentId == null
    }

    def uploadFile() {
        return fileStorageService.save(new SaveFileCommand(
            originalFileName: "mock_statement.csv",
            directory: "test",
            contentType: "application/csv",
            inputStream: IOUtils.toInputStream("", "UTF-8"))
        )
    }

    def prepareStatementData() {
        def result = new StatementParseResult()
        result.accountNumber = "123-primary"
        result.startDate = DateUtils.date("2017-01-02")
        result.endDate = DateUtils.date("2017-02-02")

        def row1 = new StatementRow()
        row1.setUniqueKey("1")
        row1.valueDate = DateUtils.date("2017-01-02")
        row1.date = DateUtils.date("2017-01-02")
        row1.accountNumber = "123-primary"
        row1.transactionCode = "IN"
        row1.counterpartyAccount = "111"
        row1.counterpartyName = "Payer Name"
        row1.counterpartyAddress = "Address"
        row1.description = "Payment Details 1"
        row1.reference = ""
        row1.amount = 100.00g
        row1.currency = "EUR"
        row1.balance = 1000.00g
        row1.status = StatementRowStatus.NEW
        row1.suggestedTransactionSubType = "SUB_TYPE"
        row1.sourceJson = "[1,2]"
        row1.attributes["attribute-a"] = "test"

        def row2 = new StatementRow()
        row2.setUniqueKey("2")
        row2.valueDate = DateUtils.date("2017-02-02")
        row2.date = DateUtils.date("2017-02-02")
        row2.accountNumber = "456"
        row2.transactionCode = "OUT"
        row2.counterpartyAccount = "222"
        row2.counterpartyName = "Receiver Name"
        row2.counterpartyAddress = "Address"
        row2.description = "Payment Details 2"
        row2.reference = ""
        row2.amount = -5.00g
        row2.currency = "EUR"
        row2.balance = 995.00g
        row2.status = StatementRowStatus.NEW
        row2.sourceJson = "[1,2]"
        row2.attributes["attribute-a"] = "test"

        result.setRows([row1, row2])
        MockStatementParser.setResult(result)
        return result
    }

    def prepareResult() {
        def result = new StatementParseResult()
        def rows = [

        ]
        result.setRows(rows)
        return result
    }

    def prepareRow() {
        def row = new StatementRow()
        row.accountNumber
        return row
    }

}
