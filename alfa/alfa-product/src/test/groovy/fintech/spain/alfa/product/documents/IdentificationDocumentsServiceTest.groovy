package fintech.spain.alfa.product.documents

import fintech.TimeMachine
import fintech.filestorage.FileStorageService
import fintech.filestorage.SaveFileCommand
import fintech.spain.alfa.product.AbstractAlfaTest
import fintech.spain.alfa.product.db.IdentificationDocumentRepository
import org.springframework.beans.factory.annotation.Autowired

class IdentificationDocumentsServiceTest extends AbstractAlfaTest {

    @Autowired
    IdentificationDocumentsService documentsService

    @Autowired
    IdentificationDocumentRepository identificationDocumentRepository

    @Autowired
    FileStorageService fileStorageService

    def "upload document"() {
        given:
        fintech.spain.alfa.product.testing.TestClient client = fintech.spain.alfa.product.testing.TestFactory.newClient().signUpWithApplication()
        def cloudFile = fileStorageService.save(new SaveFileCommand(
            originalFileName: "test.pdf",
            directory: "test",
            inputStream: new ByteArrayInputStream("test".bytes),
            contentType: "txt"
        ))

        when:
        def identificationDocumentId = documentsService.saveIdentificationDocument(new SaveIdentificationDocumentCommand(
            clientId: client.clientId,
            documentType: DocumentType.DNI,
            documentNumber: client.dni,
            frontFileId: cloudFile.fileId,
            frontFileName: "test1.pdf",
            backFileId: cloudFile.fileId,
            backFileName: "test2.pdf",
            gender: client.gender,
            surname1: client.lastName,
            surname2: "Testor 2",
            name: client.firstName,
            nationality: "Spanish",
            dateOfBirth: client.dateOfBirth,
            expirationDate: TimeMachine.today(),
            street: "str1",
            house: "house1",
            city: "Madrid",
            province: "-"
        ))

        then:
        identificationDocumentId != null

        and:
        with(identificationDocumentRepository.findOne(identificationDocumentId)) {
            documentType == DocumentType.DNI.name()
            documentNumber == client.dni
            frontFileId != null
            frontFileName == "test1.pdf"
            backFileId != null
            backFileName == "test2.pdf"
            surname1 == client.lastName
            surname2 == "Testor 2"
            name == client.firstName
            nationality == "Spanish"
            dateOfBirth == client.dateOfBirth
            expirationDate == TimeMachine.today()
            street == "str1"
            house == "house1"
            city == "Madrid"
            province == "-"
        }
    }

    def "do not allow to save identification document with invalid dni/nie if type is DNI/NIE"() {
        given:
        fintech.spain.alfa.product.testing.TestClient client = fintech.spain.alfa.product.testing.TestFactory.newClient().signUpWithApplication()
        def cloudFile = fileStorageService.save(new SaveFileCommand(
            originalFileName: "test.pdf",
            directory: "test",
            inputStream: new ByteArrayInputStream("test".bytes),
            contentType: "txt"
        ))

        when:
        documentsService.saveIdentificationDocument(new SaveIdentificationDocumentCommand(
            clientId: client.clientId,
            documentType: documentType,
            documentNumber: documentNumber,
            frontFileId: cloudFile.fileId,
            frontFileName: "test.pdf",
            backFileId: cloudFile.fileId,
            backFileName: "back.pdf",
            surname1: client.lastName,
            name: client.firstName,
            nationality: "Spanish",
            gender: client.gender,
            dateOfBirth: client.dateOfBirth,
            expirationDate: TimeMachine.today(),
            street: "str1",
            house: "house1",
            city: "Madrid",
            province: "-"
        ))

        then:
        IllegalArgumentException r = thrown()
        r.message == "Invalid DNI/NIE"

        where:
        documentNumber << ["A1111111B", "12345678K"]
        documentType << [DocumentType.DNI, DocumentType.NIE]
    }

    def "allow to save identification document with random document number if type is PASSPORT"() {
        given:
        fintech.spain.alfa.product.testing.TestClient client = fintech.spain.alfa.product.testing.TestFactory.newClient().signUpWithApplication()
        def cloudFile = fileStorageService.save(new SaveFileCommand(
            originalFileName: "test.pdf",
            directory: "test",
            inputStream: new ByteArrayInputStream("test".bytes),
            contentType: "txt"
        ))


        when:
        def id = documentsService.saveIdentificationDocument(new SaveIdentificationDocumentCommand(
            clientId: client.clientId,
            documentType: DocumentType.PASSPORT,
            documentNumber: documentNumber,
            frontFileId: cloudFile.fileId,
            frontFileName: "test1.pdf",
            backFileId: cloudFile.fileId,
            backFileName: "test2.pdf",
            surname1: client.lastName,
            name: client.firstName,
            gender: client.gender,
            nationality: "Spanish",
            dateOfBirth: client.dateOfBirth,
            expirationDate: TimeMachine.today(),
            street: "str1",
            house: "house1",
            city: "Madrid",
            province: "-"
        ))

        then:
        id != null

        where:
        documentNumber << ["A1111111B", "12345678K"]
    }


    def "validate/invalidate document"() {
        given:
        fintech.spain.alfa.product.testing.TestClient client = fintech.spain.alfa.product.testing.TestFactory.newClient().signUpWithApplication()
        def cloudFile = fileStorageService.save(new SaveFileCommand(
            originalFileName: "test.pdf",
            directory: "test",
            inputStream: new ByteArrayInputStream("test".bytes),
            contentType: "txt"
        ))
        def identificationDocumentId = documentsService.saveIdentificationDocument(new SaveIdentificationDocumentCommand(
            clientId: client.clientId,
            documentType: DocumentType.DNI,
            documentNumber: client.dni,
            frontFileId: cloudFile.fileId,
            frontFileName: "test1.pdf",
            backFileId: cloudFile.fileId,
            backFileName: "test2.pdf",
            surname1: client.lastName,
            surname2: "Testor 2",
            gender: client.gender,
            name: client.firstName,
            nationality: "Spanish",
            dateOfBirth: client.dateOfBirth,
            expirationDate: TimeMachine.today(),
            street: "str1",
            house: "house1",
            city: "Madrid",
            province: "-"
        ))

        def identificationDocumentId2 = documentsService.saveIdentificationDocument(new SaveIdentificationDocumentCommand(
            clientId: client.clientId,
            documentType: DocumentType.PASSPORT,
            documentNumber: "43559098L",
            frontFileId: cloudFile.fileId,
            frontFileName: "test1.pdf",
            backFileId: cloudFile.fileId,
            backFileName: "test2.pdf",
            surname1: client.lastName,
            surname2: "Testor 2",
            gender: client.gender,
            name: client.firstName,
            nationality: "Spanish",
            dateOfBirth: client.dateOfBirth,
            expirationDate: TimeMachine.today(),
            street: "str1",
            house: "house1",
            city: "Madrid",
            province: "-"
        ))

        when:
        documentsService.validateIdentificationDocument(new ValidateIdentificationDocument(clientId: client.clientId, identificationDocumentId: identificationDocumentId))

        then:
        with(identificationDocumentRepository.findOne(identificationDocumentId)) {
            assert isValid
            assert validatedAt != null
        }

        and:
        with(identificationDocumentRepository.findOne(identificationDocumentId2)) {
            assert !isValid
            assert validatedAt == null
        }

        when:
        documentsService.validateIdentificationDocument(new ValidateIdentificationDocument(clientId: client.clientId, identificationDocumentId: identificationDocumentId2))


        then: "first document is invalidated automatically"
        with(identificationDocumentRepository.findOne(identificationDocumentId)) {
            assert !isValid
            assert validatedAt != null
        }

        and: "target document is valid"
        with(identificationDocumentRepository.findOne(identificationDocumentId2)) {
            assert isValid
            assert validatedAt != null
        }
    }

}
