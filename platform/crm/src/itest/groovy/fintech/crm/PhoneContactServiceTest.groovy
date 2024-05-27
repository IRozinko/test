package fintech.crm

import fintech.crm.contacts.AddPhoneCommand
import fintech.crm.contacts.DuplicatePrimaryPhoneException
import fintech.crm.contacts.PhoneContactService
import fintech.crm.contacts.PhoneType
import fintech.crm.contacts.db.PhoneContactRepository
import org.springframework.beans.factory.annotation.Autowired

class PhoneContactServiceTest extends BaseSpecification {

    Long clientId
    Long secondClientId
    Long thirdClientId

    @Autowired
    PhoneContactService phoneContactService

    @Autowired
    PhoneContactRepository phoneContactRepository

    def setup() {
        clientId = createClient()
        secondClientId = createClient()
        thirdClientId = createClient()
    }

    def "Add phone contact"() {
        when:
        def contactId = phoneContactService.addPhoneContact(new AddPhoneCommand(clientId: clientId, countryCode: "+371", localNumber: "2900900", type: PhoneType.MOBILE));

        then:
        contactId != null

        def phoneContacts = phoneContactService.findClientPhoneContacts(clientId)
        phoneContacts.size() == 1
        with(phoneContacts.get(0)) {
            clientId == clientId
            id == contactId
            localNumber == "2900900"
            countryCode == "+371"
            phoneType == PhoneType.MOBILE
            !primary
        }

        and:
        phoneContactService.findPrimaryPhone(clientId) == Optional.empty()

    }

    def "Make phone primary"() {
        when:
        def contactId = phoneContactService.addPhoneContact(new AddPhoneCommand(clientId: clientId, countryCode: "+371", localNumber: "2900900", type: PhoneType.MOBILE));
        phoneContactService.makePhonePrimary(contactId);

        then:
        phoneContactService.findPrimaryPhone(clientId).isPresent()
        with(phoneContactService.findPrimaryPhone(clientId).get()) {
            id == contactId
        }
    }

    def "Add another phone and make it primary"() {
        when:
        def contactId = phoneContactService.addPhoneContact(new AddPhoneCommand(clientId: clientId, countryCode: "+371", localNumber: "2900900", type: PhoneType.MOBILE));
        def anotherPhoneContactId = phoneContactService.addPhoneContact(new AddPhoneCommand(clientId: clientId, countryCode: "+371", localNumber: "2800800", type: PhoneType.MOBILE));

        then:
        anotherPhoneContactId != null
        phoneContactService.findClientPhoneContacts(clientId).size() == 2

        when:
        phoneContactService.makePhonePrimary(anotherPhoneContactId)

        then:
        with(phoneContactService.findPrimaryPhone(clientId).get()) {
            id == anotherPhoneContactId
            localNumber == "2800800"
        }

        and: "previous primary phone number is not primary anymore"
        with(phoneContactService.findClientPhoneContacts(clientId).find { it.id == contactId }) {
            localNumber == "2900900"
            !primary
        }

    }

    def "There can't be 2 equal primary phone numbers"() {
        given:
        phoneContactService.addPhoneContact(new AddPhoneCommand(clientId: clientId, countryCode: "+371", localNumber: "2900900", type: PhoneType.MOBILE));
        def contactId = phoneContactService.addPhoneContact(new AddPhoneCommand(clientId: clientId, countryCode: "+371", localNumber: "2800800", type: PhoneType.MOBILE));
        phoneContactService.makePhonePrimary(contactId)
        def anotherClientPhoneContactId = phoneContactService.addPhoneContact(new AddPhoneCommand(clientId: secondClientId, countryCode: "+371", localNumber: "2800800", type: PhoneType.MOBILE));

        when:
        phoneContactService.makePhonePrimary(anotherClientPhoneContactId)

        then:
        thrown(DuplicatePrimaryPhoneException.class)
    }

    def "Don't update existing phone contact for user"() {
        when:
        def contactId = phoneContactService.addPhoneContact(new AddPhoneCommand(clientId: clientId, countryCode: "+371", localNumber: "2900900", type: PhoneType.MOBILE));
        phoneContactService.addPhoneContact(new AddPhoneCommand(clientId: clientId, countryCode: "+371", localNumber: "2900900", type: PhoneType.MOBILE));

        then:
        def existingPhoneContactEntity = phoneContactRepository.findOne(contactId)

        assert existingPhoneContactEntity.id == contactId
        assert existingPhoneContactEntity.countryCode == "+371"
        assert existingPhoneContactEntity.phoneType == PhoneType.MOBILE
        assert existingPhoneContactEntity.localNumber == "2900900";
    }


    def "Find by local phone number"() {
        given:
        phoneContactService.addPhoneContact(new AddPhoneCommand(clientId: clientId, countryCode: "+371", localNumber: "2900900", type: PhoneType.MOBILE))
        phoneContactService.addPhoneContact(new AddPhoneCommand(clientId: secondClientId, countryCode: "+371", localNumber: "2900900", type: PhoneType.MOBILE))
        phoneContactService.addPhoneContact(new AddPhoneCommand(clientId: thirdClientId, countryCode: "+371", localNumber: "2900901", type: PhoneType.MOBILE))

        when:
        def phones = phoneContactService.findByLocalPhoneNumber("2900900")

        then:
        phones.size() == 2
        phones[0].localNumber == "2900900"
        phones[1].localNumber == "2900900"

        expect:
        phoneContactService.findByLocalPhoneNumber("2900902").empty
    }

    def "Find actual additional phone"() {
        given:
        phoneContactService.addPhoneContact(new AddPhoneCommand(clientId: clientId, countryCode: "+371", localNumber: "2900900", type: PhoneType.OTHER))
        phoneContactService.addPhoneContact(new AddPhoneCommand(clientId: clientId, countryCode: "+371", localNumber: "2900911", type: PhoneType.OTHER))
        phoneContactService.addPhoneContact(new AddPhoneCommand(clientId: clientId, countryCode: "+371", localNumber: "2900922", type: PhoneType.OTHER))

        when:
        def phone = phoneContactService.findActualAdditionalPhone(clientId)

        then:
        phone.isPresent()
        phone.get().localNumber.equals("2900922")
    }
}
