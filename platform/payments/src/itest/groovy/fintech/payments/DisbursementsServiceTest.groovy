package fintech.payments

import fintech.TimeMachine
import fintech.filestorage.CloudFile
import fintech.filestorage.FileStorageService
import fintech.payments.commands.AddDisbursementCommand
import fintech.payments.db.DisbursementRepository
import fintech.payments.impl.FileBasedDisbursementProcessorBean
import fintech.payments.model.DisbursementExportResult
import fintech.payments.model.DisbursementStatus
import fintech.payments.model.DisbursementStatusDetail
import fintech.payments.model.ExportPendingDisbursementCommand
import fintech.payments.spi.DisbursementProcessorRegistry
import org.springframework.beans.factory.annotation.Autowired

import java.nio.charset.StandardCharsets
import java.time.LocalDate

class DisbursementsServiceTest extends BaseSpecification {

    @Autowired
    DisbursementService disbursementService

    @Autowired
    DisbursementRepository disbursementRepository

    @Autowired
    FileStorageService fileStorageService

    @Autowired
    DisbursementProcessorRegistry disbursementProcessorRegistry

    @Autowired
    FileBasedDisbursementProcessorBean fileBasedDisbursementProcessorBean

    def "Disbursement save and status changes"() {
        when:
        AddDisbursementCommand command = buildCommand()
        def disbursementId = disbursementService.add(command)

        then:
        def savedDisbursement = disbursementRepository.findOne(disbursementId)
        assert savedDisbursement.loanId == command.loanId
        assert savedDisbursement.clientId == command.clientId
        assert savedDisbursement.amount == command.amount
        assert savedDisbursement.valueDate == command.valueDate
        assert savedDisbursement.institutionId == institution.id
        assert savedDisbursement.reference == command.reference
        assert savedDisbursement.statusDetail == DisbursementStatusDetail.PENDING
        assert savedDisbursement.exportedAt == null
        assert savedDisbursement.exportedCloudFileId == null
        assert savedDisbursement.exportedFileName == null
        assert savedDisbursement.settledAt == null

        when:
        disbursementService.settled(disbursementId)

        then:
        def ex = thrown(IllegalStateException)
        assert ex.message.startsWith("Can settle only EXPORTED disbursement")


        when:
        disbursementService.exported(disbursementId, TimeMachine.now(), new DisbursementExportResult(100, new CloudFile(100L, "test-file.csv")))
        def updatedDisbursement = disbursementRepository.findOne(disbursementId)

        then:
        assert updatedDisbursement.statusDetail == DisbursementStatusDetail.EXPORTED
        assert updatedDisbursement.exportedAt != null
        assert updatedDisbursement.exportedCloudFileId == 100L
        assert updatedDisbursement.exportedFileName == "test-file.csv"

        when: "try mark as exported one more time"
        disbursementService.exported(disbursementId, TimeMachine.now(), new DisbursementExportResult(100, new CloudFile(100L, "test-file.csv")))

        then:
        ex = thrown(IllegalArgumentException)
        assert ex.message.startsWith("Can export only PENDING or ERROR disbursement")

        when:
        disbursementService.settled(disbursementId)
        updatedDisbursement = disbursementRepository.findOne(disbursementId)

        then:
        assert updatedDisbursement.statusDetail == DisbursementStatusDetail.SETTLED
        assert updatedDisbursement.settledAt != null

        when: "try mark as settled one more time"
        disbursementService.settled(disbursementId)

        then:
        ex = thrown(IllegalStateException)
        assert ex.message.startsWith("Can settle only EXPORTED disbursement")

        when:
        disbursementService.revertSettled(disbursementId)

        then:
        with(disbursementRepository.findOne(disbursementId)) {
            assert status == DisbursementStatus.OPEN
            assert statusDetail == DisbursementStatusDetail.EXPORTED
            assert settledAt == null
        }
    }

    def "Disbursement processing using export"() {
        given:
        AddDisbursementCommand command = buildCommand()
        def disbursementId = disbursementService.add(command)
        ExportPendingDisbursementCommand exportCommand = new ExportPendingDisbursementCommand(institution.id, institution.primaryAccount.id)

        when:
        def result = fileBasedDisbursementProcessorBean.exportPendingDisbursements(exportCommand)

        then:
        result.isFile()

        and:
        def disbursement = disbursementRepository.findOne(disbursementId)
        assert disbursement.statusDetail == DisbursementStatusDetail.EXPORTED
        assert disbursement.exportedFileName != null
        assert disbursement.exportedCloudFileId == result.fileId

        and:
        def content = fileStorageService.readContentAsString(result.fileId, StandardCharsets.UTF_8)
        assert content == "Testing"
    }

    def "Export single disbursement to file"() {
        given:
        AddDisbursementCommand command = buildCommand()
        def disbursementId = disbursementService.add(command)

        when:
        def result = fileBasedDisbursementProcessorBean.exportSingleDisbursement(disbursementId)

        then:
        result.isFile()

        and:
        def disbursement = disbursementRepository.findOne(disbursementId)
        assert disbursement.statusDetail == DisbursementStatusDetail.EXPORTED
    }

    def "Mark disbursement invalid"() {
        given:
        AddDisbursementCommand command = buildCommand()
        def disbursementId = disbursementService.add(command)

        when:
        disbursementService.invalid(disbursementId, "wrong data")

        then:
        with(disbursementService.getDisbursement(disbursementId)) {
            assert statusDetail == DisbursementStatusDetail.INVALID
            assert error == "wrong data"
        }

        when:
        disbursementService.pending(disbursementId)

        then:
        disbursementService.getDisbursement(disbursementId).statusDetail == DisbursementStatusDetail.PENDING
    }

    def "Cancel disbursement"() {
        given:
        AddDisbursementCommand command = buildCommand()
        def disbursementId = disbursementService.add(command)

        when:
        disbursementService.cancel(disbursementId, "test")

        then:
        disbursementService.getDisbursement(disbursementId).statusDetail == DisbursementStatusDetail.CANCELLED
    }

    def "Void disbursement"() {
        given:
        AddDisbursementCommand command = buildCommand()
        def disbursementId = disbursementService.add(command)

        when:
        disbursementService.voidDisbursement(disbursementId, "test")

        then:
        disbursementService.getDisbursement(disbursementId).statusDetail == DisbursementStatusDetail.VOIDED
    }

    private AddDisbursementCommand buildCommand() {
        def command = new AddDisbursementCommand()
        command.amount = 1.00g
        command.clientId = 1L
        command.loanId = 2L
        command.institutionId = institution.id
        command.institutionAccountId = institution.primaryAccount.id
        command.valueDate = LocalDate.now()
        command.reference = "AA123"
        return command
    }

}
