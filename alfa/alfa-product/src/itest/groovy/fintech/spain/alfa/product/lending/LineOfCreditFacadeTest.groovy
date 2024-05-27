package fintech.spain.alfa.product.lending

import fintech.IoUtils
import fintech.TimeMachine
import fintech.crm.attachments.Attachment
import fintech.crm.attachments.AttachmentConstants
import fintech.crm.attachments.ClientAttachmentService
import fintech.crm.attachments.ClientAttachmentService.AttachmentQuery
import fintech.crm.bankaccount.ClientBankAccountService
import fintech.crm.client.ClientService
import fintech.crm.client.model.DormantsClientConverter
import fintech.crm.contacts.PhoneContactService
import fintech.filestorage.FileStorageService
import fintech.spain.alfa.product.AbstractAlfaTest
import org.springframework.beans.factory.annotation.Autowired

import java.util.function.Function

class LineOfCreditFacadeTest extends AbstractAlfaTest {

    @Autowired
    LineOfCreditFacade lineOfCreditFacade

    @Autowired
    DormantsClientConverter dormantsConverter

    @Autowired
    ClientService clientService

    @Autowired
    PhoneContactService phoneContactService

    @Autowired
    ClientBankAccountService bankAccountService
    @Autowired
    ClientAttachmentService clientAttachmentService

    @Autowired
    FileStorageService fileStorageService

    def "SendClientToPresto"() {
        given:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .signUp()
            .addPrimaryBankAccount()
            .saveApplicationForm()
        def application = client
            .submitApplication(new Inquiry(principal: 100.0, submittedAt: TimeMachine.now(), termInDays: 1, interestDiscountPercent: 0))

        when:
        def prestoResponse = lineOfCreditFacade.sendClientToPresto(client.getClientId(), application.application.id)

        then:
        prestoResponse.token
    }

    def "Client converted to DormantsData"() {
        given:
        def testClient = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .signUp()
            .addPrimaryBankAccount()
            .saveApplicationForm()

        when:
        def client = clientService.get(testClient.clientId)
        def clientAddress = testClient.getPrimaryAddress()
        def dormantsData = dormantsConverter.convert(client)
        def additionalPhone = phoneContactService.findActualAdditionalPhone(client.id)
        def maybeBankAccount = bankAccountService.findPrimaryByClientId(client.id)

        then:
        with(dormantsData) {
            id == client.id
            email == client.email

            firstName == client.firstName
            secondFirstName == client.secondFirstName
            lastName == client.lastName
            secondLastName == client.secondLastName
            maidenName == client.maidenName

            client.documentNumber
            documentNumber == client.documentNumber
            client.accountNumber

            mobilePhone == client.phone
            !additionalPhone.isPresent() || otherPhone == additionalPhone.get().getPhoneNumber()
            gender == client.gender.toString()
            dateOfBirth == client.dateOfBirth

            acceptTerms == client.acceptTerms
            acceptMarketing == client.acceptMarketing
            acceptVerification == client.acceptVerification
            acceptPrivacyPolicy == client.acceptPrivacyPolicy
            blockCommunication == client.blockCommunication
            excludedFromASNEF == client.excludedFromASNEF
        }
        with(dormantsData.address) {
            city == clientAddress.city
            street == clientAddress.street
            houseNumber == clientAddress.houseNumber
            postalCode == clientAddress.postalCode
            housingTenure == clientAddress.housingTenure
        }

        with(dormantsData.bankAccount) {
            maybeBankAccount.isPresent()
            def bankAccount = maybeBankAccount.get()
            bankName == bankAccount.bankName
            accountOwnerName == bankAccount.accountOwnerName
            accountNumber == bankAccount.accountNumber
            primary == bankAccount.primary
            currency == bankAccount.currency
            balance == bankAccount.balance
            numberOfTransactions == bankAccount.numberOfTransactions
        }
    }

    def "Client converted to DormantsData - has expected attachments data"() {
        given:
        def testClient = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .signUp()

        when:
        testClient.toLoanWorkflow().runAll()
        def client = clientService.get(testClient.clientId)
        def dormantsData = dormantsConverter.convert(client)
        Attachment loanAgreement = clientAttachmentService.findAttachments(AttachmentQuery.byClient(client.getId(), AttachmentConstants.ATTACHMENT_TYPE_LOAN_AGREEMENT)).stream().findFirst().get()
        Attachment standardInfo = clientAttachmentService.findAttachments(AttachmentQuery.byClient(client.getId(), AttachmentConstants.ATTACHMENT_TYPE_STANDARD_INFORMATION)).stream().findFirst().get()

        Function<InputStream, byte[]> readInputStream = IoUtils.&copyToByteArray

        then:
        dormantsData.loanAgreementAttachment.name == loanAgreement.name
        dormantsData.standardInfoAttachment.name == standardInfo.name

        dormantsData.loanAgreementAttachment.content == fileStorageService.readContents(loanAgreement.fileId, readInputStream)
        dormantsData.standardInfoAttachment.content == fileStorageService.readContents(standardInfo.fileId, readInputStream)
    }
}
