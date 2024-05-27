package fintech.presence.impl

import fintech.presence.PhoneRecord
import fintech.presence.PresenceAdministratorProvider
import fintech.presence.PresenceDataService
import fintech.presence.PresenceService
import fintech.presence.model.PhoneDescription
import fintech.settings.SettingsService
import org.springframework.context.ApplicationEventPublisher
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

class PresenceServiceUnitTest extends Specification {

    @Subject
    PresenceService presenceService

    def setup() {
        PresenceAdministratorProvider presenceAdministratorProvider = new MockPresenceAdministratorProviderBean()
        SettingsService settingsService = Mock(SettingsService)
        PresenceDataService presenceDataService = Mock(PresenceDataService)
        ApplicationEventPublisher eventPublisher = Mock(ApplicationEventPublisher)

        presenceService = new PresenceServiceBean("mock", "mock", presenceAdministratorProvider, settingsService, presenceDataService, eventPublisher)
    }

    @Unroll
    def "add record to outbound load validations"() {
        when:
        presenceService.addRecordToCurrentOutboundLoad(name, "123", records)

        then:
        def e = thrown(exception)
        e.message == message

        where:
        name   | records                                                     | exception                | message
        ""     | [new PhoneRecord("123456", PhoneDescription.MOBILE)]        | IllegalArgumentException | "Invalid record name"
        " "    | [new PhoneRecord("123456", PhoneDescription.MOBILE)]        | IllegalArgumentException | "Invalid record name"
        null   | [new PhoneRecord("123456", PhoneDescription.MOBILE)]        | NullPointerException     | "Invalid record name"
        "test" | []                                                          | IllegalArgumentException | "Invalid records"
        "test" | null                                                        | NullPointerException     | "Invalid records"
        "test" | [new PhoneRecord("123456", PhoneDescription.MOBILE), null]  | IllegalArgumentException | "Invalid records"
        "test" | [new PhoneRecord(null, PhoneDescription.MOBILE)]            | NullPointerException     | "Invalid phone number"
        "test" | [new PhoneRecord("", PhoneDescription.MOBILE)]              | IllegalArgumentException | "Invalid phone number"
        "test" | [new PhoneRecord("123456", null)]                           | NullPointerException     | "Invalid phone description for phone number 123456"
        "test" | [new PhoneRecord("123456", PhoneDescription.NOT_SPECIFIED)] | IllegalArgumentException | "Phone description not specified for phone number 123456"
    }

}
