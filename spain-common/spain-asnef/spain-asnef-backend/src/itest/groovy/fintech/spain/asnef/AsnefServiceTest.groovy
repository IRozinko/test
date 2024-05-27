package fintech.spain.asnef

import fintech.filestorage.FileStorageService
import fintech.filestorage.SaveFileCommand
import fintech.spain.asnef.commands.ExportFileCommand
import fintech.spain.asnef.commands.GenerateFotoaltasFileCommand
import fintech.spain.asnef.commands.GenerateRpFileCommand
import fintech.spain.asnef.commands.ImportFileCommand
import fintech.spain.asnef.commands.OutputRecordHolder
import fintech.spain.asnef.models.FotoaltasOutputControlRecord
import fintech.spain.asnef.models.FotoaltasOutputHeaderRecord
import fintech.spain.asnef.models.FotoaltasOutputRecord
import fintech.spain.asnef.models.RpOutputControlRecord
import fintech.spain.asnef.models.RpOutputHeaderRecord
import fintech.spain.asnef.models.RpOutputRecord
import fintech.spain.alfa.product.asnef.internal.MockAsnefFtpGateway
import fintech.testing.integration.AbstractBaseSpecification
import org.apache.commons.io.IOUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ClassPathResource

import java.nio.charset.StandardCharsets
import java.time.LocalDate
import java.util.function.Function

class AsnefServiceTest extends AbstractBaseSpecification {

    @Autowired
    AsnefService asnefService

    @Autowired
    MockAsnefFtpGateway asnefFtpGateway

    @Autowired
    ReportingEntityProvider reportingEntityProvider

    @Autowired
    FileStorageService fileStorageService

    def setup() {
        testDatabase.cleanDb()
    }

    def "Export/import RP file - one record cancelled"() {
        when:
        def command = new GenerateRpFileCommand(
            preparedAt: LocalDate.parse("2018-01-01"),
            headerRecord: new RpOutputHeaderRecord(reportingEntity: reportingEntityProvider.getRpNotificaReportingEntity(), batchDate: LocalDate.parse("2018-01-01")),
            outputRecordHolders: [
                new OutputRecordHolder<RpOutputRecord>(
                    clientId: 1L,
                    loanId: 2L,
                    operationIdentifier: "R2", // loan number
                    number: "3",
                    outputRecord: new RpOutputRecord(personIdentifier: "1234567", surnameAndName: "John Doe", identifierOfOperation: 2L, amountClaimed: 300.00g, firstUnpaidPaymentDueDate: LocalDate.parse("2017-12-20"), address: "Calle Aduana, 29", cityTown: "Madrid", provinceCode: "28", postalCode: "28070", typeOfLetter: "NV01")
                ),
                new OutputRecordHolder<RpOutputRecord>(
                    clientId: 3L,
                    loanId: 4L,
                    operationIdentifier: "R4",  // loan number
                    number: "5",
                    outputRecord: new RpOutputRecord(personIdentifier: "2345678", surnameAndName: "Jane Doe", identifierOfOperation: 4L, amountClaimed: 500.00g, firstUnpaidPaymentDueDate: LocalDate.parse("2017-12-25"), address: "Avenida de Guadalajara, 58", cityTown: "Madrid", provinceCode: "28", postalCode: "28032", typeOfLetter: "NV01")
                )
            ],
            controlRecord: new RpOutputControlRecord(reportingEntity: reportingEntityProvider.getRpNotificaReportingEntity(), batchDate: LocalDate.parse("2018-01-01"), letterRecordsNumber: 2, totalNumberOfRecords: 4)
        )

        and:
        def logId = asnefService.generateRpFile(command)

        and:
        def log = asnefService.get(logId)

        then:
        with(log) {
            type == LogType.NOTIFICA_RP
            status == LogStatus.PREPARED
            preparedAt == LocalDate.parse("2018-01-01")
            !exportedAt
            !responseReceivedAt
            outgoingFileId
            !incomingFileId
            logRows.size() == 2

            with(logRows[0]) {
                status == LogRowStatus.PREPARED
                clientId == 1L
                loanId == 2L
                operationIdentifier == "R2"
                number == "3"
                outgoingRow
                !incomingRow
            }

            with(logRows[1]) {
                status == LogRowStatus.PREPARED
                clientId == 3L
                loanId == 4L
                operationIdentifier == "R4"
                number == "5"
                outgoingRow
                !incomingRow
            }
        }

        when:
        asnefService.exportFile(new ExportFileCommand(logId: logId, exportedAt: LocalDate.parse("2018-01-02")))

        and:
        log = asnefService.get(logId)

        then:
        with(log) {
            type == LogType.NOTIFICA_RP
            status == LogStatus.EXPORTED
            preparedAt == LocalDate.parse("2018-01-01")
            exportedAt == LocalDate.parse("2018-01-02")
            !responseReceivedAt
            outgoingFileId
            !incomingFileId
            logRows.size() == 2

            with(logRows[0]) {
                status == LogRowStatus.EXPORTED
                clientId == 1L
                loanId == 2L
                number == "3"
                outgoingRow
                !incomingRow
            }

            with(logRows[1]) {
                status == LogRowStatus.EXPORTED
                clientId == 3L
                loanId == 4L
                number == "5"
                outgoingRow
                !incomingRow
            }
        }

        when:
        def cloudFile = fileStorageService.get(log.outgoingFileId)

        then:
        with(cloudFile.get()) {
            originalFileName == AsnefConstants.Rp.getFilenameTxt(reportingEntityProvider.getRpNotificaReportingEntity())
            contentType == "text/plain"
        }

        when:
        def expected = readFunction().apply(new ClassPathResource("rp_output.asnef").inputStream)

        and:
        def actual = asnefFtpGateway.exported

        then:
        expected == actual

        when:
        def fileId = fileStorageService.save(new SaveFileCommand(originalFileName: "rp_input.asnef", directory: "asnef", inputStream: new ClassPathResource("rp_input.asnef").inputStream, contentType: "text/plain")).fileId

        and:
        asnefService.importRpFile(new ImportFileCommand(fileId: fileId, responseReceivedAt: LocalDate.parse("2018-01-03")))

        and:
        log = asnefService.get(logId)

        then:
        with(log) {
            type == LogType.NOTIFICA_RP
            status == LogStatus.RESPONSE_RECEIVED
            preparedAt == LocalDate.parse("2018-01-01")
            exportedAt == LocalDate.parse("2018-01-02")
            responseReceivedAt == LocalDate.parse("2018-01-03")
            outgoingFileId
            incomingFileId == fileId
            logRows.size() == 2

            with(logRows[0]) {
                status == LogRowStatus.SUCCEED
                clientId == 1L
                loanId == 2L
                number == "3"
                outgoingRow
                !incomingRow
            }

            with(logRows[1]) {
                status == LogRowStatus.SUCCEED
                clientId == 3L
                loanId == 4L
                number == "5"
                outgoingRow
                !incomingRow
            }
        }

        when:
        def devoFileId = fileStorageService.save(new SaveFileCommand(originalFileName: "rp_input_devo.asnef", directory: "asnef", inputStream: new ClassPathResource("rp_input_devo.asnef").inputStream, contentType: "text/plain")).fileId

        and:
        asnefService.importRpFile(new ImportFileCommand(fileId: devoFileId, responseReceivedAt: LocalDate.parse("2018-01-04")))

        and:
        log = asnefService.get(logId)

        then:
        with(log) {
            type == LogType.NOTIFICA_RP
            status == LogStatus.RESPONSE_RECEIVED
            preparedAt == LocalDate.parse("2018-01-01")
            exportedAt == LocalDate.parse("2018-01-02")
            responseReceivedAt == LocalDate.parse("2018-01-03")
            outgoingFileId
            incomingFileId == fileId
            logRows.size() == 2

            with(logRows[0]) {
                status == LogRowStatus.SUCCEED
                clientId == 1L
                loanId == 2L
                number == "3"
                outgoingRow
                !incomingRow
            }

            with(logRows[1]) {
                status == LogRowStatus.CANCELLED
                clientId == 3L
                loanId == 4L
                number == "5"
                outgoingRow
                incomingRow
            }
        }
    }

    def "Export/import RP file - one record failed"() {
        when:
        def command = new GenerateRpFileCommand(
            preparedAt: LocalDate.parse("2018-01-01"),
            headerRecord: new RpOutputHeaderRecord(reportingEntity: reportingEntityProvider.getRpNotificaReportingEntity(), batchDate: LocalDate.parse("2018-01-01")),
            outputRecordHolders: [
                new OutputRecordHolder<RpOutputRecord>(
                    clientId: 1L,
                    loanId: 2L,
                    operationIdentifier: "R2",
                    number: "3",
                    outputRecord: new RpOutputRecord(personIdentifier: "1234567", surnameAndName: "John Doe", identifierOfOperation: 2L, amountClaimed: 300.00g, firstUnpaidPaymentDueDate: LocalDate.parse("2017-12-20"), address: "Calle Aduana, 29", cityTown: "Madrid", provinceCode: "28", postalCode: "28070", typeOfLetter: "NV01")
                ),
                new OutputRecordHolder<RpOutputRecord>(
                    clientId: 3L,
                    loanId: 4L,
                    operationIdentifier: "R4",
                    number: "5",
                    outputRecord: new RpOutputRecord(personIdentifier: "2345678", surnameAndName: "Jane Doe", identifierOfOperation: 4L, amountClaimed: 500.00g, firstUnpaidPaymentDueDate: LocalDate.parse("2017-12-25"), address: "Avenida de Guadalajara, 58", cityTown: "Madrid", provinceCode: "28", postalCode: "28032", typeOfLetter: "NV01")
                )
            ],
            controlRecord: new RpOutputControlRecord(reportingEntity: reportingEntityProvider.getRpNotificaReportingEntity(), batchDate: LocalDate.parse("2018-01-01"), letterRecordsNumber: 2, totalNumberOfRecords: 4)
        )

        and:
        def logId = asnefService.generateRpFile(command)

        and:
        def log = asnefService.get(logId)

        then:
        with(log) {
            type == LogType.NOTIFICA_RP
            status == LogStatus.PREPARED
            preparedAt == LocalDate.parse("2018-01-01")
            !exportedAt
            !responseReceivedAt
            outgoingFileId
            !incomingFileId
            logRows.size() == 2

            with(logRows[0]) {
                status == LogRowStatus.PREPARED
                clientId == 1L
                loanId == 2L
                number == "3"
                outgoingRow
                !incomingRow
            }

            with(logRows[1]) {
                status == LogRowStatus.PREPARED
                clientId == 3L
                loanId == 4L
                number == "5"
                outgoingRow
                !incomingRow
            }
        }

        when:
        asnefService.exportFile(new ExportFileCommand(logId: logId, exportedAt: LocalDate.parse("2018-01-02")))

        and:
        log = asnefService.get(logId)

        then:
        with(log) {
            type == LogType.NOTIFICA_RP
            status == LogStatus.EXPORTED
            preparedAt == LocalDate.parse("2018-01-01")
            exportedAt == LocalDate.parse("2018-01-02")
            !responseReceivedAt
            outgoingFileId
            !incomingFileId
            logRows.size() == 2

            with(logRows[0]) {
                status == LogRowStatus.EXPORTED
                clientId == 1L
                loanId == 2L
                number == "3"
                outgoingRow
                !incomingRow
            }

            with(logRows[1]) {
                status == LogRowStatus.EXPORTED
                clientId == 3L
                loanId == 4L
                number == "5"
                outgoingRow
                !incomingRow
            }
        }

        when:
        def cloudFile = fileStorageService.get(log.outgoingFileId)

        then:
        with(cloudFile.get()) {
            originalFileName == AsnefConstants.Rp.getFilenameTxt(reportingEntityProvider.getRpNotificaReportingEntity())
            contentType == "text/plain"
        }

        when:
        def expected = readFunction().apply(new ClassPathResource("rp_output.asnef").inputStream)

        and:
        def actual = asnefFtpGateway.exported

        then:
        expected == actual

        when:
        def fileId = fileStorageService.save(new SaveFileCommand(originalFileName: "rp_input_error.asnef", directory: "asnef", inputStream: new ClassPathResource("rp_input_error.asnef").inputStream, contentType: "text/plain")).fileId

        and:
        asnefService.importRpFile(new ImportFileCommand(fileId: fileId, responseReceivedAt: LocalDate.parse("2018-01-03")))

        and:
        log = asnefService.get(logId)

        then:
        with(log) {
            type == LogType.NOTIFICA_RP
            status == LogStatus.RESPONSE_RECEIVED
            preparedAt == LocalDate.parse("2018-01-01")
            exportedAt == LocalDate.parse("2018-01-02")
            responseReceivedAt == LocalDate.parse("2018-01-03")
            outgoingFileId
            incomingFileId == fileId
            logRows.size() == 2

            with(logRows.find { it.status == LogRowStatus.SUCCEED }) {
                clientId == 1L
                loanId == 2L
                number == "3"
                outgoingRow
                !incomingRow
            }

            with(logRows.find { it.status == LogRowStatus.FAILED }) {
                status == LogRowStatus.FAILED
                clientId == 3L
                loanId == 4L
                number == "5"
                outgoingRow
                incomingRow
            }
        }
    }

    def "Export/import FOTOALTAS file - record succeed"() {
        when:
        def command = new GenerateFotoaltasFileCommand(
            preparedAt: LocalDate.parse("2018-01-01"),
            headerRecord: new FotoaltasOutputHeaderRecord(reportingEntity: reportingEntityProvider.getFotoaltasReportingEntity(), fileIdentifier: reportingEntityProvider.getFotoaltasReportingEntity() + "0001", dateOfProcessing: LocalDate.parse("2018-01-01"), closingDateOfAccounts: LocalDate.parse("2018-01-01")),
            outputRecordHolders: [new OutputRecordHolder<FotoaltasOutputRecord>(
                clientId: 1L,
                loanId: 2L,
                operationIdentifier: "R2",
                number: "3",
                outputRecord: new FotoaltasOutputRecord(operationIdentifier: "R2", startDateOfOperation: LocalDate.parse("2017-11-20"), endDateOfOperation: LocalDate.parse("2017-12-20"), nominalAmountOfOperation: 300.00g, numberOfPayments: 2, numberOfPaymentsCurrentlyOverdue: 2, dueDateOfFirstCurrentlyUnpaidPayment: LocalDate.parse("2017-12-20"), dueDateOfLastCurrentlyUnpaidPayment: LocalDate.parse("2017-12-22"), totalAmountCurrentlyUnpaid: 310.00g, personIdentifier: "1234567", lastName: "Doe", firstName: "John", roadName: "Calle Aduana", roadNumber: "29", nameOfTown: "Madrid", provinceCode: "28", postalCode: "28070", phoneNumber: "987654321")
            )],
            controlRecord: new FotoaltasOutputControlRecord(reportingEntity: reportingEntityProvider.getFotoaltasReportingEntity(), fileIdentifier: reportingEntityProvider.getFotoaltasReportingEntity() + "0001", dateOfProcessing: LocalDate.parse("2018-01-01"), numberOfOperations: 1, totalNumberOfRecords: 3)
        )

        and:
        def logId = asnefService.generateFotoaltasFile(command)

        and:
        def log = asnefService.get(logId)

        then:
        with(log) {
            type == LogType.FOTOALTAS
            status == LogStatus.PREPARED
            preparedAt == LocalDate.parse("2018-01-01")
            !exportedAt
            !responseReceivedAt
            outgoingFileId
            !incomingFileId
            logRows.size() == 1

            with(logRows[0]) {
                status == LogRowStatus.PREPARED
                clientId == 1L
                loanId == 2L
                operationIdentifier == "R2"
                number == "3"
                outgoingRow
                !incomingRow
            }
        }

        when:
        asnefService.exportFile(new ExportFileCommand(logId: logId, exportedAt: LocalDate.parse("2018-01-02")))

        and:
        log = asnefService.get(logId)

        then:
        with(log) {
            type == LogType.FOTOALTAS
            status == LogStatus.EXPORTED
            preparedAt == LocalDate.parse("2018-01-01")
            exportedAt == LocalDate.parse("2018-01-02")
            !responseReceivedAt
            outgoingFileId
            !incomingFileId
            logRows.size() == 1

            with(logRows[0]) {
                status == LogRowStatus.EXPORTED
                clientId == 1L
                loanId == 2L
                operationIdentifier == "R2"
                number == "3"
                outgoingRow
                !incomingRow
            }
        }

        when:
        def cloudFile = fileStorageService.get(log.outgoingFileId)

        then:
        with(cloudFile.get()) {
            originalFileName == AsnefConstants.Fotoaltas.getFilenameTxt(reportingEntityProvider.getFotoaltasReportingEntity())
            contentType == "text/plain"
        }

        when:
        def expected = readFunction().apply(new ClassPathResource("fotoaltas_output.asnef").inputStream)

        and:
        def actual = fileStorageService.readContents(log.outgoingFileId, readFunction())

        then:
        expected == actual

        when:
        def fileId = fileStorageService.save(new SaveFileCommand(originalFileName: "fotoaltas_input.asnef", directory: "asnef", inputStream: new ClassPathResource("fotoaltas_input.asnef").inputStream, contentType: "text/plain")).fileId

        and:
        asnefService.importFotoaltasFile(new ImportFileCommand(fileId: fileId, responseReceivedAt: LocalDate.parse("2018-01-03")))

        and:
        log = asnefService.get(logId)

        then:
        with(log) {
            type == LogType.FOTOALTAS
            status == LogStatus.RESPONSE_RECEIVED
            preparedAt == LocalDate.parse("2018-01-01")
            exportedAt == LocalDate.parse("2018-01-02")
            responseReceivedAt == LocalDate.parse("2018-01-03")
            outgoingFileId
            incomingFileId == fileId
            logRows.size() == 1

            with(logRows[0]) {
                status == LogRowStatus.SUCCEED
                clientId == 1L
                loanId == 2L
                operationIdentifier == "R2"
                number == "3"
                outgoingRow
                !incomingRow
            }
        }
    }

    def "Export/import FOTOALTAS file - record failed"() {
        when:
        def command = new GenerateFotoaltasFileCommand(
            preparedAt: LocalDate.parse("2018-01-01"),
            headerRecord: new FotoaltasOutputHeaderRecord(reportingEntity: reportingEntityProvider.getFotoaltasReportingEntity(), fileIdentifier: reportingEntityProvider.getFotoaltasReportingEntity() + "0001", dateOfProcessing: LocalDate.parse("2018-01-01"), closingDateOfAccounts: LocalDate.parse("2018-01-01")),
            outputRecordHolders: [new OutputRecordHolder<FotoaltasOutputRecord>(
                clientId: 1L,
                loanId: 2L,
                operationIdentifier: "R2",
                number: "3",
                outputRecord: new FotoaltasOutputRecord(operationIdentifier: "R2", startDateOfOperation: LocalDate.parse("2017-11-20"), endDateOfOperation: LocalDate.parse("2017-12-20"), nominalAmountOfOperation: 300.00g, numberOfPayments: 2, numberOfPaymentsCurrentlyOverdue: 2, dueDateOfFirstCurrentlyUnpaidPayment: LocalDate.parse("2017-12-20"), dueDateOfLastCurrentlyUnpaidPayment: LocalDate.parse("2017-12-22"), totalAmountCurrentlyUnpaid: 310.00g, personIdentifier: "1234567", lastName: "Doe", firstName: "John", roadName: "Calle Aduana", roadNumber: "29", nameOfTown: "Madrid", provinceCode: "28", postalCode: "28070", phoneNumber: "987654321")
            )],
            controlRecord: new FotoaltasOutputControlRecord(reportingEntity: reportingEntityProvider.getFotoaltasReportingEntity(), fileIdentifier: reportingEntityProvider.getFotoaltasReportingEntity() + "0001", dateOfProcessing: LocalDate.parse("2018-01-01"), numberOfOperations: 1, totalNumberOfRecords: 3)
        )

        and:
        def logId = asnefService.generateFotoaltasFile(command)

        and:
        def log = asnefService.get(logId)

        then:
        with(log) {
            type == LogType.FOTOALTAS
            status == LogStatus.PREPARED
            preparedAt == LocalDate.parse("2018-01-01")
            !exportedAt
            !responseReceivedAt
            outgoingFileId
            !incomingFileId
            logRows.size() == 1

            with(logRows[0]) {
                status == LogRowStatus.PREPARED
                clientId == 1L
                loanId == 2L
                operationIdentifier == "R2"
                number == "3"
                outgoingRow
                !incomingRow
            }
        }

        when:
        asnefService.exportFile(new ExportFileCommand(logId: logId, exportedAt: LocalDate.parse("2018-01-02")))

        and:
        log = asnefService.get(logId)

        then:
        with(log) {
            type == LogType.FOTOALTAS
            status == LogStatus.EXPORTED
            preparedAt == LocalDate.parse("2018-01-01")
            exportedAt == LocalDate.parse("2018-01-02")
            !responseReceivedAt
            outgoingFileId
            !incomingFileId
            logRows.size() == 1

            with(logRows[0]) {
                status == LogRowStatus.EXPORTED
                clientId == 1L
                loanId == 2L
                operationIdentifier == "R2"
                number == "3"
                outgoingRow
                !incomingRow
            }
        }

        when:
        def cloudFile = fileStorageService.get(log.outgoingFileId)

        then:
        with(cloudFile.get()) {
            originalFileName == AsnefConstants.Fotoaltas.getFilenameTxt(reportingEntityProvider.getFotoaltasReportingEntity())
            contentType == "text/plain"
        }

        when:
        def expected = readFunction().apply(new ClassPathResource("fotoaltas_output.asnef").inputStream)

        and:
        def actual = asnefFtpGateway.exported

        then:
        expected == actual

        when:
        def fileId = fileStorageService.save(new SaveFileCommand(originalFileName: "fotoaltas_input_error.asnef", directory: "asnef", inputStream: new ClassPathResource("fotoaltas_input_error.asnef").inputStream, contentType: "text/plain")).fileId

        and:
        asnefService.importFotoaltasFile(new ImportFileCommand(fileId: fileId, responseReceivedAt: LocalDate.parse("2018-01-03")))

        and:
        log = asnefService.get(logId)

        then:
        with(log) {
            type == LogType.FOTOALTAS
            status == LogStatus.RESPONSE_RECEIVED
            preparedAt == LocalDate.parse("2018-01-01")
            exportedAt == LocalDate.parse("2018-01-02")
            responseReceivedAt == LocalDate.parse("2018-01-03")
            outgoingFileId
            incomingFileId == fileId
            logRows.size() == 1

            with(logRows[0]) {
                status == LogRowStatus.FAILED
                clientId == 1L
                loanId == 2L
                operationIdentifier == "R2"
                number == "3"
                outgoingRow
                incomingRow
            }
        }
    }

    def "Delete RP file"() {
        given:
        def command = new GenerateRpFileCommand(
            preparedAt: LocalDate.parse("2018-01-01"),
            headerRecord: new RpOutputHeaderRecord(reportingEntity: reportingEntityProvider.getRpNotificaReportingEntity(), batchDate: LocalDate.parse("2018-01-01")),
            outputRecordHolders: [
                new OutputRecordHolder<RpOutputRecord>(
                    clientId: 1L,
                    loanId: 2L,
                    operationIdentifier: "R2", // loan number
                    number: "3",
                    outputRecord: new RpOutputRecord(personIdentifier: "1234567", surnameAndName: "John Doe", identifierOfOperation: 2L, amountClaimed: 300.00g, firstUnpaidPaymentDueDate: LocalDate.parse("2017-12-20"), address: "Calle Aduana, 29", cityTown: "Madrid", provinceCode: "28", postalCode: "28070", typeOfLetter: "NV01")
                ),
                new OutputRecordHolder<RpOutputRecord>(
                    clientId: 3L,
                    loanId: 4L,
                    operationIdentifier: "R4",  // loan number
                    number: "5",
                    outputRecord: new RpOutputRecord(personIdentifier: "2345678", surnameAndName: "Jane Doe", identifierOfOperation: 4L, amountClaimed: 500.00g, firstUnpaidPaymentDueDate: LocalDate.parse("2017-12-25"), address: "Avenida de Guadalajara, 58", cityTown: "Madrid", provinceCode: "28", postalCode: "28032", typeOfLetter: "NV01")
                )
            ],
            controlRecord: new RpOutputControlRecord(reportingEntity: reportingEntityProvider.getRpNotificaReportingEntity(), batchDate: LocalDate.parse("2018-01-01"), letterRecordsNumber: 2, totalNumberOfRecords: 4)
        )

        and:
        def logId = asnefService.generateRpFile(command)

        when:
        asnefService.deleteFile(logId)

        and:
        def log = asnefService.get(logId)

        then:
        with(log) {
            type == LogType.NOTIFICA_RP
            status == LogStatus.DELETED
            preparedAt == LocalDate.parse("2018-01-01")
            !exportedAt
            !responseReceivedAt
            outgoingFileId
            !incomingFileId
            logRows.size() == 2

            logRows.find {
                it.clientId == 1L
                it.loanId == 2L
                it.operationIdentifier == "R2"
                it.number == "3"
                it.outgoingRow
                !it.incomingRow
            }.status == LogRowStatus.DELETED

            logRows.find {
                it.clientId == 3L
                it.loanId == 4L
                it.operationIdentifier == "R4"
                it.number == "5"
                it.outgoingRow
                !it.incomingRow
            }.status == LogRowStatus.DELETED
        }
    }

    def "Delete RP file - Already exported"() {
        given:
        def command = new GenerateRpFileCommand(
            preparedAt: LocalDate.parse("2018-01-01"),
            headerRecord: new RpOutputHeaderRecord(reportingEntity: reportingEntityProvider.getRpNotificaReportingEntity(), batchDate: LocalDate.parse("2018-01-01")),
            outputRecordHolders: [
                new OutputRecordHolder<RpOutputRecord>(
                    clientId: 1L,
                    loanId: 2L,
                    operationIdentifier: "R2", // loan number
                    number: "3",
                    outputRecord: new RpOutputRecord(personIdentifier: "1234567", surnameAndName: "John Doe", identifierOfOperation: 2L, amountClaimed: 300.00g, firstUnpaidPaymentDueDate: LocalDate.parse("2017-12-20"), address: "Calle Aduana, 29", cityTown: "Madrid", provinceCode: "28", postalCode: "28070", typeOfLetter: "NV01")
                ),
                new OutputRecordHolder<RpOutputRecord>(
                    clientId: 3L,
                    loanId: 4L,
                    operationIdentifier: "R4",  // loan number
                    number: "5",
                    outputRecord: new RpOutputRecord(personIdentifier: "2345678", surnameAndName: "Jane Doe", identifierOfOperation: 4L, amountClaimed: 500.00g, firstUnpaidPaymentDueDate: LocalDate.parse("2017-12-25"), address: "Avenida de Guadalajara, 58", cityTown: "Madrid", provinceCode: "28", postalCode: "28032", typeOfLetter: "NV01")
                )
            ],
            controlRecord: new RpOutputControlRecord(reportingEntity: reportingEntityProvider.getRpNotificaReportingEntity(), batchDate: LocalDate.parse("2018-01-01"), letterRecordsNumber: 2, totalNumberOfRecords: 4)
        )

        and:
        def logId = asnefService.generateRpFile(command)
        asnefService.exportFile(new ExportFileCommand(logId: logId, exportedAt: LocalDate.parse("2018-01-02")))

        when:
        asnefService.deleteFile(logId)

        then:
        def e = thrown(IllegalStateException)
        e.message.startsWith("Unable to delete asnef log")
    }

    def "Delete RP file - Response already received"() {
        given:
        def command = new GenerateRpFileCommand(
            preparedAt: LocalDate.parse("2018-01-01"),
            headerRecord: new RpOutputHeaderRecord(reportingEntity: reportingEntityProvider.getRpNotificaReportingEntity(), batchDate: LocalDate.parse("2018-01-01")),
            outputRecordHolders: [
                new OutputRecordHolder<RpOutputRecord>(
                    clientId: 1L,
                    loanId: 2L,
                    operationIdentifier: "R2", // loan number
                    number: "3",
                    outputRecord: new RpOutputRecord(personIdentifier: "1234567", surnameAndName: "John Doe", identifierOfOperation: 2L, amountClaimed: 300.00g, firstUnpaidPaymentDueDate: LocalDate.parse("2017-12-20"), address: "Calle Aduana, 29", cityTown: "Madrid", provinceCode: "28", postalCode: "28070", typeOfLetter: "NV01")
                ),
                new OutputRecordHolder<RpOutputRecord>(
                    clientId: 3L,
                    loanId: 4L,
                    operationIdentifier: "R4",  // loan number
                    number: "5",
                    outputRecord: new RpOutputRecord(personIdentifier: "2345678", surnameAndName: "Jane Doe", identifierOfOperation: 4L, amountClaimed: 500.00g, firstUnpaidPaymentDueDate: LocalDate.parse("2017-12-25"), address: "Avenida de Guadalajara, 58", cityTown: "Madrid", provinceCode: "28", postalCode: "28032", typeOfLetter: "NV01")
                )
            ],
            controlRecord: new RpOutputControlRecord(reportingEntity: reportingEntityProvider.getRpNotificaReportingEntity(), batchDate: LocalDate.parse("2018-01-01"), letterRecordsNumber: 2, totalNumberOfRecords: 4)
        )

        and:
        def logId = asnefService.generateRpFile(command)
        asnefService.exportFile(new ExportFileCommand(logId: logId, exportedAt: LocalDate.parse("2018-01-02")))

        and:
        def fileId = fileStorageService.save(new SaveFileCommand(originalFileName: "rp_input.asnef", directory: "asnef", inputStream: new ClassPathResource("rp_input.asnef").inputStream, contentType: "text/plain")).fileId
        asnefService.importRpFile(new ImportFileCommand(fileId: fileId, responseReceivedAt: LocalDate.parse("2018-01-03")))

        when:
        asnefService.deleteFile(logId)

        then:
        def e = thrown(IllegalStateException)
        e.message.startsWith("Unable to delete asnef log")
    }

    def "Delete FOTOALTAS file"() {
        given:
        def command = new GenerateFotoaltasFileCommand(
            preparedAt: LocalDate.parse("2018-01-01"),
            headerRecord: new FotoaltasOutputHeaderRecord(reportingEntity: reportingEntityProvider.getFotoaltasReportingEntity(), fileIdentifier: reportingEntityProvider.getFotoaltasReportingEntity() + "0001", dateOfProcessing: LocalDate.parse("2018-01-01"), closingDateOfAccounts: LocalDate.parse("2018-01-01")),
            outputRecordHolders: [new OutputRecordHolder<FotoaltasOutputRecord>(
                clientId: 1L,
                loanId: 2L,
                operationIdentifier: "R2",
                number: "3",
                outputRecord: new FotoaltasOutputRecord(operationIdentifier: "R2", startDateOfOperation: LocalDate.parse("2017-11-20"), endDateOfOperation: LocalDate.parse("2017-12-20"), nominalAmountOfOperation: 300.00g, numberOfPayments: 2, numberOfPaymentsCurrentlyOverdue: 2, dueDateOfFirstCurrentlyUnpaidPayment: LocalDate.parse("2017-12-20"), dueDateOfLastCurrentlyUnpaidPayment: LocalDate.parse("2017-12-22"), totalAmountCurrentlyUnpaid: 310.00g, personIdentifier: "1234567", lastName: "Doe", firstName: "John", roadName: "Calle Aduana", roadNumber: "29", nameOfTown: "Madrid", provinceCode: "28", postalCode: "28070", phoneNumber: "987654321")
            )],
            controlRecord: new FotoaltasOutputControlRecord(reportingEntity: reportingEntityProvider.getFotoaltasReportingEntity(), fileIdentifier: reportingEntityProvider.getFotoaltasReportingEntity() + "0001", dateOfProcessing: LocalDate.parse("2018-01-01"), numberOfOperations: 1, totalNumberOfRecords: 3)
        )

        and:
        def logId = asnefService.generateFotoaltasFile(command)

        when:
        asnefService.deleteFile(logId)

        then:
        def e = thrown(IllegalStateException)
        e.message.startsWith("It's possible to delete only NOTIFICA files")
    }

    def "ASNEF file upload when multiple NOTIFICA_RP files exist on same day and only one is active "() {
        given:
        def command = new GenerateRpFileCommand(
            preparedAt: LocalDate.parse("2018-01-01"),
            headerRecord: new RpOutputHeaderRecord(reportingEntity: reportingEntityProvider.getRpNotificaReportingEntity(), batchDate: LocalDate.parse("2018-01-01")),
            outputRecordHolders: [
                new OutputRecordHolder<RpOutputRecord>(
                    clientId: 3L,
                    loanId: 4L,
                    operationIdentifier: "R4",  // loan number
                    number: "5",
                    outputRecord: new RpOutputRecord(personIdentifier: "2345678", surnameAndName: "Jane Doe", identifierOfOperation: 4L, amountClaimed: 500.00g, firstUnpaidPaymentDueDate: LocalDate.parse("2017-12-25"), address: "Avenida de Guadalajara, 58", cityTown: "Madrid", provinceCode: "28", postalCode: "28032", typeOfLetter: "NV01")
                )
            ],
            controlRecord: new RpOutputControlRecord(reportingEntity: reportingEntityProvider.getRpNotificaReportingEntity(), batchDate: LocalDate.parse("2018-01-01"), letterRecordsNumber: 2, totalNumberOfRecords: 4)
        )

        and:
        asnefService.deleteFile(asnefService.generateRpFile(command))
        asnefService.generateRpFile(command)

        when:
        def fileId = fileStorageService.save(new SaveFileCommand(originalFileName: "rp_input.asnef", directory: "asnef", inputStream: new ClassPathResource("rp_input.asnef").inputStream, contentType: "text/plain")).fileId
        asnefService.importRpFile(new ImportFileCommand(fileId: fileId, responseReceivedAt: LocalDate.parse("2018-01-03")))

        then:
        noExceptionThrown()
    }

    def readFunction() {
        new Function<InputStream, String>() {

            @Override
            String apply(InputStream input) {
                IOUtils.toString(input, StandardCharsets.US_ASCII)
            }
        }
    }
}
