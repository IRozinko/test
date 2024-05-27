package fintech.bo.api


import fintech.TimeMachine
import fintech.crm.attachments.AddAttachmentCommand
import fintech.crm.attachments.ClientAttachmentService
import fintech.crm.client.ClientService
import fintech.crm.client.Gender
import fintech.crm.client.UpdateClientCommand
import fintech.crm.contacts.EmailContactService
import fintech.crm.logins.EmailLoginService
import fintech.filestorage.FileStorageService
import fintech.filestorage.SaveFileCommand
import fintech.spain.alfa.bo.api.AlfaApiFacade
import fintech.spain.alfa.bo.model.SaveIdentificationDocumentRequest
import fintech.spain.alfa.bo.model.UpdateClientDataRequest
import fintech.spain.alfa.product.AlfaConstants
import fintech.spain.alfa.product.db.Entities
import fintech.spain.alfa.product.db.IdentificationDocumentRepository
import fintech.spain.alfa.product.testing.TestFactory
import org.springframework.beans.factory.annotation.Autowired

import javax.transaction.Transactional

import static fintech.crm.attachments.ClientAttachmentService.AttachmentQuery

class AlfaApiFacadeTest extends AbstractAlfaBoApiTest {

    @Autowired
    private AlfaApiFacade apiFacade

    @Autowired
    private FileStorageService fileStorageService

    @Autowired
    private ClientAttachmentService clientAttachmentService

    @Autowired
    private IdentificationDocumentRepository identificationDocumentRepository

    @Autowired
    private EmailLoginService emailLoginService

    @Autowired
    private EmailContactService emailContactService

    @Autowired
    private ClientService clientService

    def "saving new identification document with existing attachments"() {
        given:
        def client = TestFactory.newClient()
            .signUpWithApplication()

        UpdateClientCommand updateCommand = UpdateClientCommand.fromClient(client.getClient())
        updateCommand.setGender(Gender.MALE)
        updateCommand.setDateOfBirth(client.getDateOfBirth())
        clientService.update(updateCommand)
        def frontFile = fileStorageService.save(new SaveFileCommand(
            originalFileName: "test1.pdf",
            directory: "test",
            contentType: "txt",
            inputStream: new ByteArrayInputStream("text".bytes)
        ))

        def backFile = fileStorageService.save(new SaveFileCommand(
            originalFileName: "test2.pdf",
            directory: "test",
            contentType: "txt",
            inputStream: new ByteArrayInputStream("text".bytes)
        ))

        when:
        clientAttachmentService.addAttachment(new AddAttachmentCommand(
            clientId: client.clientId,
            attachmentType: AlfaConstants.ATTACHMENT_TYPE_ID_DOCUMENT,
            fileId: frontFile.fileId,
            name: "test1.pdf"
        ))

        clientAttachmentService.addAttachment(new AddAttachmentCommand(
            clientId: client.clientId,
            attachmentType: AlfaConstants.ATTACHMENT_TYPE_ID_DOCUMENT,
            fileId: backFile.fileId,
            name: "test2.pdf"
        ))

        then:
        def attachments = clientAttachmentService.findAttachments(new AttachmentQuery(clientId: client.clientId, type: AlfaConstants.ATTACHMENT_TYPE_ID_DOCUMENT))

        attachments.size() == 2
        attachments.find { it.fileId == frontFile.fileId } != null
        attachments.find { it.fileId == backFile.fileId } != null

        when:
        apiFacade.saveIdentificationDocument(new SaveIdentificationDocumentRequest(
            clientId: client.clientId,
            documentType: SaveIdentificationDocumentRequest.DocumentType.DNI,
            documentNumber: client.getDni(),
            surname1: client.lastName,
            surname2: client.lastName,
            name: client.firstName,
            gender: client.getGender(),
            nationality: "Spanish",
            dateOfBirth: client.getDateOfBirth(),
            expirationDate: TimeMachine.today(),
            placeOfBirth: "Spain",
            frontAttachment: new SaveIdentificationDocumentRequest.Attachment(
                fileId: frontFile.fileId,
                fileName: frontFile.originalFileName
            ),
            backAttachment: new SaveIdentificationDocumentRequest.Attachment(
                fileId: backFile.fileId,
                fileName: backFile.originalFileName
            )
        ))

        attachments = clientAttachmentService.findAttachments(new AttachmentQuery(clientId: client.clientId, type: AlfaConstants.ATTACHMENT_TYPE_ID_DOCUMENT))

        then:
        attachments.size() == 2
        attachments.find { it.fileId == frontFile.fileId } != null
        attachments.find { it.fileId == backFile.fileId } != null

        and:
        def idDoc = identificationDocumentRepository.findOne(Entities.identificationDocument.clientId.eq(client.clientId))

        idDoc.documentType == "DNI"
        idDoc.documentNumber == client.dni
        idDoc.surname1 == client.lastName
        idDoc.surname2 == client.lastName
        idDoc.name == client.firstName
        idDoc.gender == client.gender
        idDoc.nationality == "Spanish"
        idDoc.dateOfBirth == client.dateOfBirth
        idDoc.expirationDate == TimeMachine.today()
        idDoc.placeOfBirth == "Spain"
        idDoc.frontFileId == frontFile.fileId
        idDoc.frontFileName == frontFile.originalFileName
        idDoc.backFileId == backFile.fileId
        idDoc.backFileName == backFile.originalFileName
    }

    def "update client email"() {
        given:
        def newEmail = "new_email@test.com"
        def client = TestFactory.newClient()
            .signUpWithApplication()

        when:
        apiFacade.updateClient(new UpdateClientDataRequest(clientId: client.clientId, firstName: client.client.firstName, lastName: client.client.lastName,
            secondLastName: client.client.secondLastName, dateOfBirth: client.client.dateOfBirth, email: newEmail, phone: client.client.phone,
            accountNumber: client.client.accountNumber,
            blockCommunication: client.client.blockCommunication, excludedFromASNEF: client.client.excludedFromASNEF, gender: client.gender))

        then:
        emailLoginService.findByClientId(client.clientId).get().email == newEmail
        emailContactService.findPrimaryEmail(client.clientId).get().email == newEmail
    }

    @Transactional
    def "update client email when no email login entity"() {
        given:
        def newEmail = "new_email@test.com"
        def client = TestFactory.newClient()
            .signUpWithApplication()
        emailLoginService.delete(client.clientId)

        when:
        apiFacade.updateClient(new UpdateClientDataRequest(clientId: client.clientId, firstName: client.client.firstName, lastName: client.client.lastName,
            secondLastName: client.client.secondLastName, dateOfBirth: client.client.dateOfBirth, email: newEmail, phone: client.client.phone,
            accountNumber: client.client.accountNumber,
            blockCommunication: client.client.blockCommunication, excludedFromASNEF: client.client.excludedFromASNEF, gender: client.gender))

        then:
        noExceptionThrown()
        !emailLoginService.findByClientId(client.clientId).isPresent()
        emailContactService.findPrimaryEmail(client.clientId).get().email == newEmail
    }
}
