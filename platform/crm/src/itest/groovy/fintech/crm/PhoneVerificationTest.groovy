package fintech.crm

import fintech.TimeMachine
import fintech.crm.contacts.*
import org.springframework.beans.factory.annotation.Autowired

class PhoneVerificationTest extends BaseSpecification {

    Long clientId

    @Autowired
    PhoneContactService phoneContactService

    def setup() {
        clientId = createClient()
    }

    def "Verify"() {
        given:
        def phoneId = phoneContactService.addPhoneContact(new AddPhoneCommand(clientId: clientId, countryCode: "+371", localNumber: "2900900", type: PhoneType.MOBILE))

        expect:
        !phoneContactService.findClientPhoneContacts(clientId)[0].verified
        !phoneContactService.findLatestVerificationCode(phoneId).present

        when:
        phoneContactService.addPhoneVerification(new AddPhoneVerificationCommand(phoneContactId: phoneId, code: "666"))
        phoneContactService.addPhoneVerification(new AddPhoneVerificationCommand(phoneContactId: phoneId, code: "777"))

        then:
        phoneContactService.findLatestVerificationCode(phoneId).get().code == "777"
        !phoneContactService.findClientPhoneContacts(clientId)[0].verified

        when: "Invalid code"
        phoneContactService.verifyPhone(new VerifyPhoneCommand(phoneContactId: phoneId, code: "INVALID", codeCreatedAfter: TimeMachine.now().minusHours(1)))

        then:
        !phoneContactService.findClientPhoneContacts(clientId)[0].verified

        when: "Code expired"
        phoneContactService.verifyPhone(new VerifyPhoneCommand(phoneContactId: phoneId, code: "777", codeCreatedAfter: TimeMachine.now().plusMinutes(1)))

        then:
        thrown(PhoneVerificationCodeExpiredException)

        then:
        !phoneContactService.findClientPhoneContacts(clientId)[0].verified
        !eventConsumer.containsEvent(PhoneVerifiedEvent.class)

        when: "All good"
        phoneContactService.verifyPhone(new VerifyPhoneCommand(phoneContactId: phoneId, code: "777", codeCreatedAfter: TimeMachine.now().minusHours(1)))

        then:
        phoneContactService.findClientPhoneContacts(clientId)[0].verified
        phoneContactService.findClientPhoneContacts(clientId)[0].verifiedAt
        eventConsumer.containsEvent(PhoneVerifiedEvent.class)

        when: "tries to verify twice"
        phoneContactService.verifyPhone(new VerifyPhoneCommand(phoneContactId: phoneId, code: "777", codeCreatedAfter: TimeMachine.now().minusHours(1)))

        then:
        thrown(PhoneAlreadyVerifiedException)
    }

    def "count sent verification codes"() {
        given:
        def phoneId = phoneContactService.addPhoneContact(new AddPhoneCommand(clientId: clientId, countryCode: "+371", localNumber: "2900900", type: PhoneType.MOBILE))

        when:
        phoneContactService.addPhoneVerification(new AddPhoneVerificationCommand(phoneContactId: phoneId, code: "666"))

        then:
        phoneContactService.countSentVerificationCodes(clientId, phoneId) == 1
    }
}
