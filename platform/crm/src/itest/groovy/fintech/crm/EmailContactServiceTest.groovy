package fintech.crm

import fintech.crm.contacts.AddEmailContactCommand
import fintech.crm.contacts.DuplicatePrimaryEmailException
import fintech.crm.contacts.EmailContact
import fintech.crm.contacts.EmailContactService
import fintech.crm.contacts.db.EmailContactRepository
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Shared

class EmailContactServiceTest extends BaseSpecification {

	@Autowired
	EmailContactService emailContactService

    @Autowired
    private EmailContactRepository repository

	Long oneClientId

	@Shared
	private Long emailContactId

    def setup() {
        oneClientId = createClient()
    }

	def "Add email contact"() {
		when:
        emailContactId = emailContactService.addEmailContact(new AddEmailContactCommand(clientId: oneClientId, email: "test@mail.com"))

		then:
		this.emailContactId != null
        emailContactService.findAllEmailContacts(oneClientId) == [new EmailContact(id: this.emailContactId, clientId: oneClientId, email: "test@mail.com", primary: false)]

		and:
		!emailContactService.findPrimaryEmail(oneClientId).isPresent()
	}

	def "Make email primary"() {
		when:
        emailContactId = emailContactService.addEmailContact(new AddEmailContactCommand(clientId: oneClientId, email: "test@mail.com"))
		emailContactService.makeEmailPrimary(emailContactId)
		def primaryEmailContact = emailContactService.findPrimaryEmail(oneClientId)

		then:
		primaryEmailContact.isPresent()
        primaryEmailContact.get() == new EmailContact(id: emailContactId, clientId: oneClientId, email: "test@mail.com", primary: true)
	}

	def "User can add another email and make it primary"() {
        given:
        emailContactId = emailContactService.addEmailContact(new AddEmailContactCommand(clientId: oneClientId, email: "test@mail.com"))
        emailContactService.makeEmailPrimary(emailContactId)

        when:
        def anotherEmailContactId = emailContactService.addEmailContact(new AddEmailContactCommand(clientId: oneClientId, email: "another@mail.com"))
		emailContactService.makeEmailPrimary(anotherEmailContactId)

		then:
		def primaryEmailContact = emailContactService.findPrimaryEmail(oneClientId)
		primaryEmailContact.isPresent()
        primaryEmailContact.get() == new EmailContact(id: anotherEmailContactId, clientId: oneClientId, email: "another@mail.com", primary: true)

		and:
		emailContactService.findAllEmailContacts(oneClientId).size() == 2
	}

	def "User can't make primary email which is primary for another user"() {
        given:
        emailContactId = emailContactService.addEmailContact(new AddEmailContactCommand(clientId: oneClientId, email: "test@mail.com"))
        emailContactService.makeEmailPrimary(emailContactId)
        def anotherClientId = createClient()
        def anotherEmailContact = emailContactService.addEmailContact(new AddEmailContactCommand(clientId: anotherClientId, email: "test@mail.com"))

		when:
		emailContactService.makeEmailPrimary(anotherEmailContact)

		then:
		DuplicatePrimaryEmailException ex = thrown()
		ex.message == 'Email already in use: test@mail.com'
	}

	def "Don't update existing email contact for user"() {
        given:
        emailContactId = emailContactService.addEmailContact(new AddEmailContactCommand(clientId: oneClientId, email: "test@mail.com"))
        emailContactService.makeEmailPrimary(emailContactId)

        when:
        emailContactService.addEmailContact(new AddEmailContactCommand(clientId: oneClientId, email: "test@mail.com"))

        then:
        def emailContactEntity = repository.findOne(emailContactId)
        assert emailContactEntity.id == emailContactId
        assert emailContactEntity.email == "test@mail.com"
        assert emailContactEntity.client.id == oneClientId

    }
}
