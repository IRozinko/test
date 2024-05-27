package fintech.spain.alfa.product

import fintech.TimeMachine
import fintech.dc.spi.DebtBatchJobs
import fintech.filestorage.FileStorageService
import fintech.filestorage.SaveFileCommand
import fintech.lending.core.loan.LoanService
import fintech.spain.asnef.AsnefService
import fintech.spain.asnef.FileAsnefFotoaltasGenerateConsumer
import fintech.spain.asnef.FileAsnefRpGenerateConsumer
import fintech.spain.asnef.LogRowStatus
import fintech.spain.asnef.LogStatus
import fintech.spain.asnef.FileAsnefService
import fintech.spain.asnef.commands.ImportFileCommand
import fintech.spain.asnef.db.LogRepository
import fintech.spain.asnef.db.LogRowRepository
import fintech.spain.asnef.models.RpInputControlRecord
import fintech.spain.asnef.models.RpInputHeaderRecord
import fintech.spain.asnef.models.RpInputRecord
import fintech.spain.asnef.models.RpOutputHeaderRecord
import org.apache.commons.io.IOUtils
import org.apache.commons.lang.StringUtils
import org.beanio.StreamFactory
import org.beanio.builder.FixedLengthParserBuilder
import org.beanio.builder.StreamBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ClassPathResource

import java.nio.charset.StandardCharsets
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.function.Function

import static fintech.DateUtils.date
import static fintech.spain.asnef.db.Entities.logRow

class FileAsnefServiceTest extends AbstractAlfaTest {

    @Autowired
    LoanService loanService

    @Autowired
    fintech.spain.alfa.product.extension.ExtensionService extensionService

    @Autowired
    FileAsnefService alfaAsnefService

    @Autowired
    AsnefService asnefService

    @Autowired
    FileStorageService fileStorageService

    @Autowired
    DebtBatchJobs debtExecutor

    @Autowired
    LogRepository logRepository

    @Autowired
    LogRowRepository logRowRepository

    @Autowired
    FileAsnefRpGenerateConsumer asnefRpGenerateConsumer

    @Autowired
    FileAsnefFotoaltasGenerateConsumer asnefFotoaltasGenerateConsumer

    def "Export/import RP file"() {
        given:
        def loan = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .registerDirectly()
            .issueActiveLoan(300.00, 30, date("2018-01-01"))

        when:
        def logId = alfaAsnefService.generateRpFile([loan.loanId], LocalDate.parse("2018-01-01"))

        and:
        def log = asnefService.get(logId)

        and:
        def generatedOutput = fileStorageService.readContents(log.outgoingFileId, new Function<InputStream, String>() {

            @Override
            String apply(InputStream input) {
                IOUtils.toString(input, StandardCharsets.US_ASCII)
            }
        })

        and:
        def outputRow = StringUtils.replaceEach(IOUtils.toString(new ClassPathResource("asnef/rp_output_row.asnef").inputStream, StandardCharsets.US_ASCII), ["{DOCUMENT_NUMBER}", "{LAST_NAME}", "{LOAN_NUMBER}", "{PAYMENT_DATE}"] as String[], [loan.testClient.dni, loan.testClient.lastName, StringUtils.rightPad(StringUtils.replace(loan.getLoan().getNumber().toString(), "-", ""), 25, " "), formatDate(loan.loan.paymentDueDate)] as String[])

        then:
        generatedOutput == StringUtils.replace(IOUtils.toString(new ClassPathResource("asnef/rp_output.asnef").inputStream, StandardCharsets.US_ASCII), "{OUTPUT_ROW}", outputRow)

        when:
        def cloudFile = fileStorageService.save(new SaveFileCommand(originalFileName: "rp_input.asnef", directory: "asnef", inputStream: IOUtils.toInputStream(StringUtils.replaceEach(IOUtils.toString(new ClassPathResource("asnef/rp_input.asnef").inputStream, StandardCharsets.US_ASCII), ["{BATCH_DATE}", "{OUTPUT_ROW}"] as String[], [formatDate(LocalDate.parse("2018-01-01")), outputRow] as String[]), StandardCharsets.US_ASCII), contentType: "text/plain"))

        and:
        asnefService.importRpFile(new ImportFileCommand(fileId: cloudFile.fileId, responseReceivedAt: TimeMachine.today()))

        and:
        log = asnefService.get(logId)

        then:
        with(log) {
            status == LogStatus.RESPONSE_RECEIVED
            logRows.size() == 1

            with(logRows[0]) {
                status == LogRowStatus.FAILED
            }
        }
    }

    def "Export/import with history"() {
        given:
        TimeMachine.useFixedClockAt(TimeMachine.now().minusDays(120))
        def loan = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .registerDirectly()
            .issueActiveLoan(300.00, 30, TimeMachine.today())

        when:
        TimeMachine.useFixedClockAt(TimeMachine.now().plusDays(60))
        loanService.resolveLoanDerivedValues(loan.loanId, TimeMachine.today())

        and: "NOTIFICA Report was generated"
        asnefRpGenerateConsumer.consume(TimeMachine.today())

        then: "We can find one logRow with PREPARED status"
        with(logRowRepository.findAll(logRow.loanId.eq(loan.loanId))) {
            size() == 1
            get(0).status == LogRowStatus.PREPARED
        }

        when:
        "we got empty Eqifax Error Report"()

        then: "We can find one logRow with SUCCEED status"
        with(findLogRows(loan.loanId)) {
            size() == 1
            get(0).status == LogRowStatus.SUCCEED
        }

        when: "Client buys extension"
        def offer = extensionService.listOffersForLoan(loan.loanId, TimeMachine.today()).get(0)
        def payment = fintech.spain.alfa.product.testing.TestFactory.payments().newIncomingPayment(offer.price, TimeMachine.today())

        extensionService.applyAndRepayExtensionFee(new fintech.spain.alfa.product.extension.ApplyAndRepayExtensionFeeCommand()
            .setLoanId(loan.loanId)
            .setExtensionOffer(offer)
            .setPaymentAmount(payment.getPayment().pendingAmount)
            .setPaymentId(payment.paymentId)
        )

        and: "Extension time is over"
        loan.getLoan()
        TimeMachine.useFixedClockAt(TimeMachine.now().plusDays(offer.periodCount))
        loanService.resolveLoanDerivedValues(loan.loanId, TimeMachine.today())

        then: "LogRow should be exhausted"
        with(findLogRows(loan.loanId)) {
            size() == 1
            get(0).status == LogRowStatus.EXHAUSTED
        }

        when: "Fotoaltas Report was Generated"
        asnefFotoaltasGenerateConsumer.consume(TimeMachine.today())

        then: "There is must be only one LogRow no any rows for this loan"
        with(findLogRows(loan.loanId)) {
            size() == 1
            get(0).status == LogRowStatus.EXHAUSTED
        }

        when: "NOTIFICA Report was generated"
        asnefRpGenerateConsumer.consume(TimeMachine.today())

        then: "We can find Second LogRow with status PREPARED"
        with(findLogRows(loan.loanId)) {
            size() == 2
            get(0).status == LogRowStatus.PREPARED
            get(1).status == LogRowStatus.EXHAUSTED
        }

        when:
        "we got empty Eqifax Error Report"()

        then: "We can find Second LogRow with status SUCCEED"
        with(findLogRows(loan.loanId)) {
            size() == 2
            get(0).status == LogRowStatus.SUCCEED
            get(1).status == LogRowStatus.EXHAUSTED
        }

        when: "Fotoaltas Report was Generated"
        TimeMachine.useFixedClockAt(TimeMachine.now().plusDays(20))
        asnefFotoaltasGenerateConsumer.consume(TimeMachine.today())

        then: "We can find Third LogRow with status SUCCEED"
        with(findLogRows(loan.loanId)) {
            size() == 3
            get(0).status == LogRowStatus.PREPARED
            get(1).status == LogRowStatus.SUCCEED
            get(2).status == LogRowStatus.EXHAUSTED
        }
    }

    def "Export/import FOTOALTAS file"() {
        given:
        def loan = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .registerDirectly()
            .issueActiveLoan(300.00, 30, date("2018-01-01"))

        when:
        def logId = alfaAsnefService.generateFotoaltasFile([loan.loanId], LocalDate.parse("2018-01-01"))

        and:
        def log = asnefService.get(logId)

        and:
        def generatedOutput = fileStorageService.readContents(log.outgoingFileId, new Function<InputStream, String>() {

            @Override
            String apply(InputStream input) {
                IOUtils.toString(input, StandardCharsets.US_ASCII)
            }
        })

        and:
        def outputRow = StringUtils.replaceEach(IOUtils.toString(new ClassPathResource("asnef/fotoaltas_output_row.asnef").inputStream, StandardCharsets.US_ASCII), ["{LOAN_ISSUE_DATE}", "{LOAN_MATURITY_DATE}", "{INVOICE_DUE_DATE}", "{LOAN_NUMBER}", "{DOCUMENT_NUMBER}", "{LAST_NAME}", "{PHONE_NUMBER}"] as String[], [formatDate(loan.loan.issueDate), formatDate(loan.loan.maturityDate), formatDate(loan.loan.maturityDate), StringUtils.rightPad(StringUtils.replace(loan.getLoan().getNumber(), "-", ""), 25, " "), loan.testClient.dni, loan.testClient.lastName, loan.testClient.mobilePhone] as String[])

        then:
        generatedOutput == StringUtils.replaceEach(IOUtils.toString(new ClassPathResource("asnef/fotoaltas_output.asnef").inputStream, StandardCharsets.US_ASCII), ["{OUTPUT_ROW}", "{CLOSING_DATE_OF_ACCOUNTS}"] as String[], [outputRow, formatDate(LocalDate.parse("2018-01-01"))] as String[])

        when:
        def cloudFile = fileStorageService.save(new SaveFileCommand(originalFileName: "fotoaltas_input.asnef", directory: "asnef", inputStream: IOUtils.toInputStream(StringUtils.replace(IOUtils.toString(new ClassPathResource("asnef/fotoaltas_input.asnef").inputStream, StandardCharsets.US_ASCII), "{OUTPUT_ROW}", outputRow), StandardCharsets.US_ASCII), contentType: "text/plain"))

        and:
        asnefService.importFotoaltasFile(new ImportFileCommand(fileId: cloudFile.fileId, responseReceivedAt: TimeMachine.today()))

        and:
        log = asnefService.get(logId)

        then:
        with(log) {
            status == LogStatus.RESPONSE_RECEIVED
            logRows.size() == 1

            with(logRows[0]) {
                status == LogRowStatus.FAILED
            }
        }
    }

    def formatDate(LocalDate date) {
        DateTimeFormatter.ofPattern("yyyyMMdd").format(date)
    }

    def errorReport(LocalDate batchDate) {
        StreamFactory factory = StreamFactory.newInstance()
        factory.define(new StreamBuilder("asnef")
            .format("fixedlength")
            .parser(new FixedLengthParserBuilder())
            .addTypeHandler(BigDecimal.class, new fintech.spain.alfa.product.asnef.internal.RpBigDecimalTypeHandler())
            .addTypeHandler(LocalDate.class, new fintech.spain.alfa.product.asnef.internal.LocalDateTypeHandler())
            .addRecord(RpInputHeaderRecord.class)
            .addRecord(RpInputRecord.class)
            .addRecord(RpInputControlRecord.class))

        def marshaller = factory.createMarshaller("asnef")

        def header = new RpInputHeaderRecord()
        header.inputFileHeaderRecord = new RpOutputHeaderRecord()
        header.inputFileHeaderRecord.batchDate = batchDate
        header.inputFileHeaderRecord.reportingEntity = "B573"

        def controlRecord = new RpInputControlRecord()
        controlRecord.inputFileHeaderRecord = header.inputFileHeaderRecord

        return String.join('\n',
            marshaller.marshal(header).toString(),
            marshaller.marshal(controlRecord).toString()
        )
    }

    def "we got empty Eqifax Error Report"() {
        def inputStream = new StringBufferInputStream(errorReport(TimeMachine.today()))
        def errorsFileId = fileStorageService.save(new SaveFileCommand(originalFileName: "_input_error.asnef", directory: "asnef", inputStream: inputStream, contentType: "text/plain")).fileId
        asnefService.importRpFile(new ImportFileCommand(fileId: errorsFileId, responseReceivedAt: TimeMachine.today()))
    }

    def findLogRows(long loanId) {
        logRowRepository.findAll(logRow.loanId.eq(loanId))
    }
}


//        For future example of generation of error file
//        def logId = alfaAsnefService.generateRpFile([loan.loanId], TimeMachine.today())
//        asnefService.exportFile(new ExportFileCommand(logId: logId, exportedAt: TimeMachine.today()))
//        def log = asnefService.get(logId)

//        def record = new RpInputDevoRecord()
//        record.entityCode = "B573"
//        record.typeOfRecord = AsnefConstants.Rp.DEVO_RECORD_TYPE
//        record.returnDate = loan.getLoan().paymentDueDate
//        record.returnReason = "00"
//        record.batchDate = TimeMachine.today()
//        record.personIdentifier = loan.toClient().getClient().getDocumentNumber()
//        record.surnameAndName = loan.toClient().fullName()
//        record.identifierOfOperation = loan.loanId
//        record.financialProductCode = "07"
//        record.amountClaimed = loan.getLoan().totalDue
//        // ...
//        record.typeOfLetter = "NV01"
//
//        StreamFactory factory = StreamFactory.newInstance()
//        factory.define(new StreamBuilder("asnef")
//            .format("fixedlength")
//            .parser(new FixedLengthParserBuilder())
//            .addTypeHandler(BigDecimal.class, new RpBigDecimalTypeHandler())
//            .addTypeHandler(LocalDate.class, new LocalDateTypeHandler())
//            .addRecord(RpInputDevoRecord.class))
//
//        def marshaller = factory.createMarshaller("asnef")
//
//        def inputStream = new StringBufferInputStream("B573D120180305                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   \n" +
//            marshaller.marshal(record) + "\n" +
//            "B573D320180305000000000000000002                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 ")
//
