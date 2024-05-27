package fintech.spain.callcenter.impl

import com.google.common.collect.ImmutableSet
import fintech.crm.client.ClientService
import fintech.crm.client.CreateClientCommand
import fintech.crm.client.UpdateClientCommand
import fintech.crm.contacts.AddPhoneCommand
import fintech.crm.contacts.PhoneContactService
import fintech.crm.contacts.PhoneType
import fintech.presence.OutboundLoadRecordQuery
import fintech.presence.impl.PresenceDataServiceBean
import fintech.presence.model.OutboundLoadRecordStatus
import fintech.presence.model.PhoneDescription
import fintech.settings.SettingsService
import fintech.spain.callcenter.BaseSpecification
import fintech.spain.callcenter.CallCenterDataService
import fintech.spain.callcenter.CallCenterException
import fintech.spain.callcenter.CallCenterService
import fintech.spain.callcenter.CallQuery
import fintech.spain.callcenter.CallStatus
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Subject

class CallCenterServiceTest extends BaseSpecification {

    @Subject
    @Autowired
    CallCenterService callCenterService

    @Autowired
    CallCenterDataService callCenterDataService

    @Autowired
    PresenceDataServiceBean presenceDataServiceBean

    @Autowired
    ClientService clientService

    @Autowired
    PhoneContactService phoneContactService

    @Autowired
    SettingsService settingsService

    def "add client to call list"() {
        given:
        def clientId = clientService.create(new CreateClientCommand(RandomStringUtils.randomAlphanumeric(10)))
        clientService.update(new UpdateClientCommand(clientId: clientId, firstName: "Name", lastName: "Surname"))
        phoneContactService.addPhoneContact(new AddPhoneCommand(clientId: clientId, countryCode: "34", localNumber: "666123456", type: PhoneType.MOBILE))

        when:
        callCenterService.addPhoneRecordsToCallList(clientId)
        def records = callCenterDataService.find(new CallQuery().setClientId(clientId).setStatuses(ImmutableSet.of(CallStatus.PENDING)))
        def presenceRecords = presenceDataServiceBean.findRecords(new OutboundLoadRecordQuery().setServiceId(presenceServiceId).setLoadId(presenceLoadId))

        then:
        records
        records.size() == 1
        records[0].providerId
        records[0].clientId == clientId
        records[0].status == CallStatus.PENDING

        and:
        presenceRecords
        presenceRecords.size() == 1
        with(presenceRecords[0]) {
            sourceId
            name == "Name Surname"
            status == OutboundLoadRecordStatus.PENDING
            phoneRecords
            phoneRecords.size() == 1
            with(phoneRecords[0]) {
                number == "666123456"
                description == PhoneDescription.MOBILE
            }
        }
    }

    def "add twice client to call list"() {
        given:
        def clientId = clientService.create(new CreateClientCommand(RandomStringUtils.randomAlphanumeric(10)))
        clientService.update(new UpdateClientCommand(clientId: clientId, firstName: "Name", lastName: "Surname"))
        phoneContactService.addPhoneContact(new AddPhoneCommand(clientId: clientId, countryCode: "34", localNumber: "666123456", type: PhoneType.MOBILE))
        callCenterService.addPhoneRecordsToCallList(clientId)

        when:
        callCenterService.addPhoneRecordsToCallList(clientId)

        then:
        noExceptionThrown()

        when:
        def records = callCenterDataService.find(new CallQuery().setClientId(clientId).setStatuses(ImmutableSet.of(CallStatus.PENDING)))
        def presenceRecords = presenceDataServiceBean.findRecords(new OutboundLoadRecordQuery().setServiceId(presenceServiceId).setLoadId(presenceLoadId))

        then:
        records
        records.size() == 1
        records[0].providerId
        records[0].clientId == clientId
        records[0].status == CallStatus.PENDING

        and:
        presenceRecords
        presenceRecords.size() == 1
        with(presenceRecords[0]) {
            sourceId
            name == "Name Surname"
            status == OutboundLoadRecordStatus.PENDING
            phoneRecords
            phoneRecords.size() == 1
            with(phoneRecords[0]) {
                number == "666123456"
                description == PhoneDescription.MOBILE
            }
        }
    }

    def "add twice client to call list with different outbound load"() {
        given:
        def clientId = clientService.create(new CreateClientCommand(RandomStringUtils.randomAlphanumeric(10)))
        clientService.update(new UpdateClientCommand(clientId: clientId, firstName: "Name", lastName: "Surname"))
        phoneContactService.addPhoneContact(new AddPhoneCommand(clientId: clientId, countryCode: "34", localNumber: "666123456", type: PhoneType.MOBILE))
        callCenterService.addPhoneRecordsToCallList(clientId)

        when:
        mockProviderBean.setup(presenceServiceId, presenceLoadId + 1)
        updatePresenceSettings(presenceServiceId, presenceLoadId + 1)
        callCenterService.addPhoneRecordsToCallList(clientId)

        then:
        noExceptionThrown()

        when:
        def records = callCenterDataService.find(new CallQuery().setClientId(clientId).setStatuses(ImmutableSet.of(CallStatus.PENDING)))
        def presenceRecords = presenceDataServiceBean.findRecords(new OutboundLoadRecordQuery().setServiceId(presenceServiceId))

        then:
        records
        records.size() == 1
        records[0].providerId
        records[0].clientId == clientId
        records[0].status == CallStatus.PENDING

        and:
        presenceRecords
        presenceRecords.size() == 1
        with(presenceRecords[0]) {
            sourceId
            name == "Name Surname"
            status == OutboundLoadRecordStatus.PENDING
            phoneRecords
            phoneRecords.size() == 1
            with(phoneRecords[0]) {
                number == "666123456"
                description == PhoneDescription.MOBILE
            }
        }
    }

    def "add twice client to call list with different disabled outbound load"() {
        given:
        def clientId = clientService.create(new CreateClientCommand(RandomStringUtils.randomAlphanumeric(10)))
        clientService.update(new UpdateClientCommand(clientId: clientId, firstName: "Name", lastName: "Surname"))
        phoneContactService.addPhoneContact(new AddPhoneCommand(clientId: clientId, countryCode: "34", localNumber: "666123456", type: PhoneType.MOBILE))

        and:
        def disabledPresenceLoadId = 2
        mockProviderBean.loadId = disabledPresenceLoadId
        mockProviderBean.loadEnabled = true
        updatePresenceSettings(presenceServiceId, disabledPresenceLoadId)
        callCenterService.addPhoneRecordsToCallList(clientId)

        when:
        mockProviderBean.loadId = presenceLoadId
        updatePresenceSettings(presenceServiceId, presenceLoadId)
        callCenterService.addPhoneRecordsToCallList(clientId)

        then:
        noExceptionThrown()

        when:
        def records = callCenterDataService.find(new CallQuery().setClientId(clientId).setStatuses(ImmutableSet.of(CallStatus.PENDING)))
        def presenceRecords = presenceDataServiceBean.findRecords(new OutboundLoadRecordQuery().setServiceId(presenceServiceId))

        then:
        records
        records.size() == 1
        records[0].providerId
        records[0].clientId == clientId
        records[0].status == CallStatus.PENDING

        and:
        presenceRecords
        presenceRecords.size() == 1
        with(presenceRecords[0]) {
            sourceId
            name == "Name Surname"
            status == OutboundLoadRecordStatus.PENDING
            phoneRecords
            phoneRecords.size() == 1
            with(phoneRecords[0]) {
                number == "666123456"
                description == PhoneDescription.MOBILE
            }
        }
    }

    def "add client with phone contact with country code different than spanish one"() {
        given:
        def clientId = clientService.create(new CreateClientCommand(RandomStringUtils.randomAlphanumeric(10)))
        clientService.update(new UpdateClientCommand(clientId: clientId, firstName: "Name", lastName: "Surname"))
        phoneContactService.addPhoneContact(new AddPhoneCommand(clientId: clientId, countryCode: "39", localNumber: "666123456", type: PhoneType.MOBILE))

        when:
        callCenterService.addPhoneRecordsToCallList(clientId)

        then:
        thrown(IllegalArgumentException)
    }

    def "remove client from call list"() {
        given:
        def clientId = clientService.create(new CreateClientCommand(RandomStringUtils.randomAlphanumeric(10)))
        clientService.update(new UpdateClientCommand(clientId: clientId, firstName: "Name", lastName: "Surname"))
        phoneContactService.addPhoneContact(new AddPhoneCommand(clientId: clientId, countryCode: "34", localNumber: "666123456", type: PhoneType.MOBILE))
        callCenterService.addPhoneRecordsToCallList(clientId)

        when:
        callCenterService.removePhoneRecordsFromCallList(clientId)
        def records = callCenterDataService.find(new CallQuery().setClientId(clientId).setStatuses(ImmutableSet.of(CallStatus.PENDING)))

        then:
        !records

        when:
        records = callCenterDataService.find(new CallQuery().setClientId(clientId).setStatuses(ImmutableSet.of(CallStatus.REMOVED)))
        def presenceRecords = presenceDataServiceBean.findRecords(new OutboundLoadRecordQuery().setServiceId(presenceServiceId).setLoadId(presenceLoadId))

        then:
        records
        records.size() == 1
        records[0].providerId
        records[0].clientId == clientId
        records[0].status == CallStatus.REMOVED

        and:
        presenceRecords
        presenceRecords.size() == 1
        with(presenceRecords[0]) {
            sourceId
            name == "Name Surname"
            status == OutboundLoadRecordStatus.UNLOADED
            phoneRecords
            phoneRecords.size() == 1
            with(phoneRecords[0]) {
                number == "666123456"
                description == PhoneDescription.MOBILE
            }
        }
    }

    def "remove client not in call list"() {
        given:
        def clientId = clientService.create(new CreateClientCommand(RandomStringUtils.randomAlphanumeric(10)))
        clientService.update(new UpdateClientCommand(clientId: clientId, firstName: "Name", lastName: "Surname"))
        phoneContactService.addPhoneContact(new AddPhoneCommand(clientId: clientId, countryCode: "34", localNumber: "666123456", type: PhoneType.MOBILE))

        when:
        callCenterService.removePhoneRecordsFromCallList(clientId)

        then:
        noExceptionThrown()

        when:
        def records = callCenterDataService.find(new CallQuery().setClientId(clientId).setStatuses(ImmutableSet.of(CallStatus.PENDING)))
        def presenceRecords = presenceDataServiceBean.findRecords(new OutboundLoadRecordQuery().setServiceId(presenceServiceId).setLoadId(presenceLoadId))

        then:
        !records
        !presenceRecords
    }

    def "remove client from call list already called"() {
        given:
        def clientId = clientService.create(new CreateClientCommand(RandomStringUtils.randomAlphanumeric(10)))
        clientService.update(new UpdateClientCommand(clientId: clientId, firstName: "Name", lastName: "Surname"))
        phoneContactService.addPhoneContact(new AddPhoneCommand(clientId: clientId, countryCode: "34", localNumber: "666123456", type: PhoneType.MOBILE))
        callCenterService.addPhoneRecordsToCallList(clientId)
        def call = callCenterDataService.find(new CallQuery().setClientId(clientId).setStatuses(ImmutableSet.of(CallStatus.PENDING)))[0]
        callCenterDataService.updateCallStatus(call.id, CallStatus.COMPLETED)

        when:
        callCenterService.removePhoneRecordsFromCallList(clientId)
        def records = callCenterDataService.find(new CallQuery().setClientId(clientId).setStatuses(ImmutableSet.of(CallStatus.REMOVED)))

        then:
        noExceptionThrown()
        !records
    }

    def "error removing client to list"() {
        given:
        def clientId = clientService.create(new CreateClientCommand(RandomStringUtils.randomAlphanumeric(10)))
        clientService.update(new UpdateClientCommand(clientId: clientId, firstName: "Name", lastName: "Surname"))
        phoneContactService.addPhoneContact(new AddPhoneCommand(clientId: clientId, countryCode: "34", localNumber: "666123456", type: PhoneType.MOBILE))
        callCenterService.addPhoneRecordsToCallList(clientId)

        when:
        mockProviderBean.throwGenericError = true
        callCenterService.removePhoneRecordsFromCallList(clientId)

        then:
        thrown(CallCenterException)

        when:
        def records = callCenterDataService.find(new CallQuery().setClientId(clientId))

        then:
        records
        records.size() == 1
        records[0].providerId
        records[0].clientId == clientId
        records[0].status == CallStatus.ERROR
    }

    def "receive update record of unknown client"() {
        when:
        callCenterService.updatePhoneRecordsFromProvider()
        def call = callCenterDataService.find(new CallQuery())

        then:
        !call
    }

    def "receive update for client record"() {
        given:
        def clientId = clientService.create(new CreateClientCommand(RandomStringUtils.randomAlphanumeric(10)))
        clientService.update(new UpdateClientCommand(clientId: clientId, firstName: "Name", lastName: "Surname"))
        phoneContactService.addPhoneContact(new AddPhoneCommand(clientId: clientId, countryCode: "34", localNumber: "666123456", type: PhoneType.MOBILE))
        callCenterService.addPhoneRecordsToCallList(clientId)

        when:
        mockProviderBean.outboundLoadRecordStatusOverride = OutboundLoadRecordStatus.COMPLETED
        callCenterService.updatePhoneRecordsFromProvider()
        def records = callCenterDataService.find(new CallQuery().setClientId(clientId))

        then:
        records
        records.size() == 1
        records[0].status == CallStatus.COMPLETED
    }
}
