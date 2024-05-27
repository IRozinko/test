package fintech.payments.impl

import fintech.TimeMachine
import fintech.payments.BaseSpecification
import fintech.payments.DisbursementService
import fintech.payments.commands.AddDisbursementCommand
import fintech.payments.model.DisbursementStatus
import fintech.payments.model.ExportPendingDisbursementCommand
import fintech.spain.unnax.UnnaxPayOutService
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Subject
import spock.util.concurrent.PollingConditions

import java.time.LocalDate

import static fintech.payments.model.DisbursementStatusDetail.EXPORTED
import static fintech.payments.model.DisbursementStatusDetail.SETTLED

class UnnaxDisbursementProcessorBeanITest extends BaseSpecification {

    @Subject
    @Autowired
    UnnaxDisbursementProcessorBean processorBean

    @Autowired
    DisbursementService disbursementService

    @Autowired
    UnnaxPayOutService unnaxService

    def "isApplicable"() {
        when:
        def req = new DisbursementProcessorRegistryBean.FindBatchProcessorRequest(unnaxIxnstitution.id, unnaxIxnstitution.primaryAccount.id)

        then:
        processorBean.isApplicable(req)

        when:
        req = new DisbursementProcessorRegistryBean.FindBatchProcessorRequest(institution.id, institution.primaryAccount.id)

        then:
        !processorBean.isApplicable(req)
    }


    def "ExportSingleDisbursement"() {
        given:
        AddDisbursementCommand command = buildCommand()
        def disbursementId = disbursementService.add(command)

        when:
        processorBean.exportSingleDisbursement(disbursementId)
        def disbursement = disbursementService.getDisbursement(disbursementId)

        then:
        disbursement.status == DisbursementStatus.OPEN
        disbursement.statusDetail == EXPORTED
        !unnaxService.getTransferOutQueue(TimeMachine.now()).isEmpty()
    }

    def "exportPendingDisbursements"() {
        given:
        def conditions = new PollingConditions(timeout: 10, delay: 1)
        AddDisbursementCommand command = buildCommand()
        def disbursementId = disbursementService.add(command)

        when:
        processorBean.exportPendingDisbursements(new ExportPendingDisbursementCommand(unnaxIxnstitution.id, unnaxIxnstitution.primaryAccount.id))
        def disbursement = disbursementService.getDisbursement(disbursementId)

        then:
        disbursement.status == DisbursementStatus.OPEN
        disbursement.statusDetail == EXPORTED
        !unnaxService.getTransferOutQueue(TimeMachine.now()).isEmpty()

        and:
        conditions.eventually {
            unnaxService.getTransferOutQueue(TimeMachine.now()).isEmpty()
        }

        and:
        conditions.eventually {
            disbursementService.getDisbursement(disbursementId).statusDetail == SETTLED
        }


    }


    private AddDisbursementCommand buildCommand() {
        def command = new AddDisbursementCommand()
        command.amount = 1.00g
        command.clientId = 1L
        command.loanId = 2L
        command.institutionId = unnaxIxnstitution.id
        command.institutionAccountId = unnaxIxnstitution.primaryAccount.id
        command.valueDate = LocalDate.now()
        command.reference = "AA123"
        return command
    }

}
