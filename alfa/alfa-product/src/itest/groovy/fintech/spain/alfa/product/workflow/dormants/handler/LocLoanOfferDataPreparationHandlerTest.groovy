package fintech.spain.alfa.product.workflow.dormants.handler

import fintech.JsonUtils
import fintech.crm.attachments.Attachment
import fintech.crm.attachments.ClientAttachmentService
import fintech.filestorage.FileStorageService
import fintech.filestorage.impl.MockFileStorageProvider
import fintech.lending.core.application.LoanApplicationService
import fintech.lending.core.application.commands.SaveCreditLimitCommand
import fintech.spain.alfa.product.AbstractAlfaTest
import fintech.web.api.models.ContractAgreementRequest
import fintech.workflow.Activity
import fintech.workflow.Workflow
import fintech.workflow.WorkflowService
import fintech.workflow.impl.ActivityContextImpl
import fintech.workflow.spi.ActivityContext
import fintech.workflow.spi.ActivityResult
import fintech.workflow.spi.WorkflowBuilder
import org.springframework.beans.factory.annotation.Autowired

import java.nio.charset.Charset

class LocLoanOfferDataPreparationHandlerTest extends AbstractAlfaTest {

    @Autowired
    private LocLoanOfferDataPreparationHandler activity
    @Autowired
    private WorkflowService workflowService
    @Autowired
    private ClientAttachmentService attachmentService
    @Autowired
    private FileStorageService fileStorageService
    @Autowired
    private MockFileStorageProvider mockFileStorageProvider
    @Autowired
    private LoanApplicationService applicationService

    def "Test offer and documents are generated from presto mock"() {
        given:
        def workflow = fintech.spain.alfa.product.testing.TestFactory.newClient().signUp().toLoanWorkflow().workflow
        def context = activityContext(workflow)
        when:
        ActivityResult activityResult = activity.handle(context)
        workflow = workflowService.getWorkflow(workflow.id)
        then:
        activityResult.resolution == fintech.spain.alfa.product.workflow.common.Resolutions.OK
        workflow.getAttributes().get(fintech.spain.alfa.product.workflow.common.Attributes.LOC_OFFER)
        workflow.getAttributes().get(fintech.spain.alfa.product.workflow.common.Attributes.AGREEMENT_ATTACHMENT_ID)
        workflow.getAttributes().get(fintech.spain.alfa.product.workflow.common.Attributes.STANDARD_INFORMATON_ATTACHMENT_ID)
    }

    def "Agreement offer generated with expected file name"() {
        given:
        def workflow = fintech.spain.alfa.product.testing.TestFactory.newClient().signUp().toLoanWorkflow().workflow
        def context = activityContext(workflow)
        when:
        activity.handle(context)
        workflow = workflowService.getWorkflow(workflow.id)
        Attachment attachment = clientAttachmentService.get(Long.valueOf(workflow.getAttributes().get(fintech.spain.alfa.product.workflow.common.Attributes.AGREEMENT_ATTACHMENT_ID)))
        then:
        attachment.name == fintech.spain.alfa.product.presto.api.MockLineOfCreditCrossApiClient.TEST_AGREEMENT_FILE_NAME
    }

    def "Agreement offer requested with expected application parameters"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient()
        def testWorkflow = client.signUp().toLoanWorkflow()
        def context = activityContext(testWorkflow.workflow)
        when:
        applicationService.saveCreditLimit(new SaveCreditLimitCommand(testWorkflow.toApplication().application.id,400.00))
        activity.handle(context)
        def workflow = workflowService.getWorkflow(testWorkflow.workflow.id)
        Attachment attachment = clientAttachmentService.get(Long.valueOf(workflow.getAttributes().get(fintech.spain.alfa.product.workflow.common.Attributes.AGREEMENT_ATTACHMENT_ID)))
        def content = fileStorageService.readContentAsString(attachment.getFileId(), Charset.defaultCharset())
        def request = JsonUtils.readValue(content, ContractAgreementRequest.class)

        then:
        with(request.application) {
            date == testWorkflow.toApplication().application.submittedAt.toLocalDate()
            number == testWorkflow.toApplication().application.number
            offeredPrincipal == testWorkflow.toApplication().application.creditLimit
        }

        with(request.client) {
            number == client.client.number
            documentNumber == client.client.documentNumber
            email == client.email
            phoneNumber == client.client.phone
            firstName == client.firstName
            lastName == client.lastName
            iban == client.client.accountNumber
        }
    }

    def "Standard info offer generated with expected file name"() {
        given:
        def workflow = fintech.spain.alfa.product.testing.TestFactory.newClient().signUp().toLoanWorkflow().workflow
        def context = activityContext(workflow)
        when:
        activity.handle(context)
        workflow = workflowService.getWorkflow(workflow.id)
        Attachment attachment = clientAttachmentService.get(Long.valueOf(workflow.getAttributes().get(fintech.spain.alfa.product.workflow.common.Attributes.STANDARD_INFORMATON_ATTACHMENT_ID)))
        then:
        attachment.name == fintech.spain.alfa.product.presto.api.MockLineOfCreditCrossApiClient.TEST_STANDARD_INFO_FILE_NAME
    }

    private ActivityContext activityContext(Workflow workflow) {
        return new ActivityContextImpl(
            new WorkflowBuilder(workflow.getName()).build(),
            workflow,
            new Activity(),
            workflowService
        )
    }
}
