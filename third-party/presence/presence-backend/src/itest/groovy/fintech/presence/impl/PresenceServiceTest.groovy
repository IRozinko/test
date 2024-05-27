package fintech.presence.impl

import fintech.DateUtils
import fintech.presence.BaseSpecification
import fintech.presence.OutboundLoadQuery
import fintech.presence.OutboundLoadRecordQuery
import fintech.presence.PhoneRecord
import fintech.presence.PresenceDataService
import fintech.presence.PresenceException
import fintech.presence.PresenceOutboundLoadNotAvailable
import fintech.presence.PresenceService
import fintech.presence.model.OutboundLoadRecordStatus
import fintech.presence.model.OutboundLoadStatus
import fintech.presence.model.PhoneDescription
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException
import spock.lang.Subject

class PresenceServiceTest extends BaseSpecification {

    @Subject
    @Autowired
    PresenceService presenceService

    @Autowired
    PresenceDataService presenceDataService

    def "add record when there are no services"() {
        given:
        def presenceServiceId = 1
        def presenceLoadId = 1
        mockProviderBean.setup(presenceServiceId, presenceLoadId)
        setupSettings(presenceServiceId, presenceLoadId)
        mockProviderBean.noServices = true

        when:
        presenceService.addRecordToCurrentOutboundLoad("added", "123", [new PhoneRecord("123456", PhoneDescription.MOBILE)])

        then:
        def e = thrown(PresenceException)
        e.errorCode == -335544833
        e.errorMessage == "ADM_NOT_FOUND_OUTBOUND_SERVICE"
    }

    def "add record to not existing outbound load"() {
        given:
        def presenceServiceId = 1
        def presenceLoadId = 1
        mockProviderBean.setup(presenceServiceId, presenceLoadId)
        setupSettings(presenceServiceId, presenceLoadId)
        mockProviderBean.noLoads = true

        when:
        presenceService.addRecordToCurrentOutboundLoad("added", "123", [new PhoneRecord("123456", PhoneDescription.MOBILE)])

        then:
        def e = thrown(PresenceOutboundLoadNotAvailable)
        e.message == "No active outbound loads found for serviceId ${presenceServiceId}"

        when:
        def records = presenceDataService.findRecords(new OutboundLoadRecordQuery())

        then:
        !records
    }

    def "add record when there are no outbound loads"() {
        given:
        def presenceServiceId = 1
        def presenceLoadId = 1
        mockProviderBean.setup(presenceServiceId, presenceLoadId)
        setupSettings(presenceServiceId, null)
        mockProviderBean.noLoads = true

        when:
        presenceService.addRecordToCurrentOutboundLoad("added", "123", [new PhoneRecord("123456", PhoneDescription.MOBILE)])

        then:
        def e = thrown(PresenceOutboundLoadNotAvailable)
        e.message == "No active outbound loads found for serviceId ${presenceServiceId}"

        when:
        def records = presenceDataService.findRecords(new OutboundLoadRecordQuery())

        then:
        !records
    }

    def "add record to outbound load with service disabled"() {
        given:
        def presenceServiceId = 1
        def presenceLoadId = 1
        def presenceSourceId = 1000
        mockProviderBean.setup(presenceServiceId, presenceLoadId)
        mockProviderBean.serviceStatus = "D"
        setupSettings(presenceServiceId, presenceLoadId)

        when:
        presenceService.addRecordToCurrentOutboundLoad("added", "123", [new PhoneRecord("123456", PhoneDescription.MOBILE)])
        def records = presenceDataService.findRecords(new OutboundLoadRecordQuery())

        then:
        records
        records.size() == 1
        with(records[0]) {
            sourceId == presenceSourceId
            name == "added"
            status == OutboundLoadRecordStatus.PENDING
            !qualificationCode
            outboundLoad
            with(outboundLoad) {
                loadId == presenceLoadId
                serviceId == presenceServiceId
                status == OutboundLoadStatus.ENABLED
            }
            phoneRecords
            phoneRecords.size() == 1
            with(phoneRecords[0]) {
                number == "123456"
                description == PhoneDescription.MOBILE
            }
        }
    }

    def "add record to predefined outbound load"() {
        given:
        def presenceServiceId = 1
        def presenceLoadId = 1
        mockProviderBean.setup(presenceServiceId, presenceLoadId)
        setupSettings(presenceServiceId, presenceLoadId)

        when:
        presenceService.addRecordToCurrentOutboundLoad("added", "123", [new PhoneRecord("123456", PhoneDescription.MOBILE)])
        def records = presenceDataService.findRecords(new OutboundLoadRecordQuery())

        then:
        records
        records.size() == 1
        with(records[0]) {
            sourceId == 1000
            name == "added"
            status == OutboundLoadRecordStatus.PENDING
            !qualificationCode
            outboundLoad
            with(outboundLoad) {
                loadId == presenceLoadId
                serviceId == presenceServiceId
                status == OutboundLoadStatus.ENABLED
            }
            phoneRecords
            phoneRecords.size() == 1
            with(phoneRecords[0]) {
                number == "123456"
                description == PhoneDescription.MOBILE
            }
        }
    }

    def "add record to predefined disabled outbound load"() {
        given:
        def presenceServiceId = 1
        def disabledPresenceLoadId = 2
        mockProviderBean.setup(presenceServiceId, disabledPresenceLoadId)
        mockProviderBean.loadEnabled = false
        setupSettings(presenceServiceId, disabledPresenceLoadId)

        when:
        presenceService.addRecordToCurrentOutboundLoad("added", "123", [new PhoneRecord("123456", PhoneDescription.MOBILE)])
        def records = presenceDataService.findRecords(new OutboundLoadRecordQuery())

        then:
        records
        records.size() == 1
        with(records[0]) {
            sourceId == 1000
            name == "added"
            status == OutboundLoadRecordStatus.PENDING
            !qualificationCode
            outboundLoad
            with(outboundLoad) {
                loadId == disabledPresenceLoadId
                serviceId == presenceServiceId
                status == OutboundLoadStatus.DISABLED
            }
            phoneRecords
            phoneRecords.size() == 1
            with(phoneRecords[0]) {
                number == "123456"
                description == PhoneDescription.MOBILE
            }
        }
    }

    def "add record to current outbound load"() {
        given:
        def presenceServiceId = 1
        def presenceLoadId = 1
        def presenceSourceId = 1000
        mockProviderBean.setup(presenceServiceId, presenceLoadId)
        setupSettings(presenceServiceId, null)

        when:
        presenceService.addRecordToCurrentOutboundLoad("added", "123", [new PhoneRecord("123456", PhoneDescription.MOBILE)])
        def records = presenceDataService.findRecords(new OutboundLoadRecordQuery())

        then:
        records
        records.size() == 1
        with(records[0]) {
            sourceId == presenceSourceId
            name == "added"
            status == OutboundLoadRecordStatus.PENDING
            !qualificationCode
            outboundLoad
            with(outboundLoad) {
                loadId == presenceLoadId
                serviceId == presenceServiceId
                status == OutboundLoadStatus.ENABLED
            }
            phoneRecords
            phoneRecords.size() == 1
            with(phoneRecords[0]) {
                number == "123456"
                description == PhoneDescription.MOBILE
            }
        }
    }

    def "add twice same record"() {
        given:
        def presenceServiceId = 1
        def presenceLoadId = 1
        mockProviderBean.setup(presenceServiceId, presenceLoadId)
        setupSettings(presenceServiceId, presenceLoadId)
        presenceService.addRecordToCurrentOutboundLoad("added", "123", [new PhoneRecord("123456", PhoneDescription.MOBILE)])

        when:
        mockProviderBean.setup(presenceServiceId, presenceLoadId)
        presenceService.addRecordToCurrentOutboundLoad("added", "123", [new PhoneRecord("123456", PhoneDescription.MOBILE)])
        def records = presenceDataService.findRecords(new OutboundLoadRecordQuery())

        then:
        noExceptionThrown()
        records
        records.size() == 2
        with(records[0]) {
            sourceId == 1000
            name == "added"
            status == OutboundLoadRecordStatus.PENDING
            !qualificationCode
            outboundLoad
            with(outboundLoad) {
                loadId == presenceLoadId
                serviceId == presenceServiceId
                status == OutboundLoadStatus.ENABLED
            }
            phoneRecords
            phoneRecords.size() == 1
            with(phoneRecords[0]) {
                number == "123456"
                description == PhoneDescription.MOBILE
            }
        }
        with(records[1]) {
            sourceId == 1001
            name == "added"
            status == OutboundLoadRecordStatus.PENDING
            !qualificationCode
            outboundLoad
            with(outboundLoad) {
                loadId == presenceLoadId
                serviceId == presenceServiceId
                status == OutboundLoadStatus.ENABLED
            }
            phoneRecords
            phoneRecords.size() == 1
            with(phoneRecords[0]) {
                number == "123456"
                description == PhoneDescription.MOBILE
            }
        }
    }

    def "remove not existing record"() {
        given:
        def presenceServiceId = 1
        def presenceLoadId = 1
        mockProviderBean.setup(presenceServiceId, presenceLoadId)
        setupSettings(presenceServiceId, presenceLoadId)

        when:
        presenceService.removeRecordFromOutboundLoad(0)

        then:
        thrown(JpaObjectRetrievalFailureException)
    }

    def "remove record when there are no services"() {
        given:
        def presenceServiceId = 1
        def presenceLoadId = 1
        mockProviderBean.setup(presenceServiceId, presenceLoadId)
        setupSettings(presenceServiceId, presenceLoadId)
        def recordId = presenceService.addRecordToCurrentOutboundLoad("added", "123", [new PhoneRecord("123456", PhoneDescription.MOBILE)])
        mockProviderBean.noServices = true

        when:
        presenceService.removeRecordFromOutboundLoad(recordId)

        then:
        def e = thrown(PresenceException)
        e.errorCode == -335544833
        e.errorMessage == "ADM_NOT_FOUND_OUTBOUND_SERVICE"
    }

    def "remove record when there are no outbound loads"() {
        given:
        def presenceServiceId = 1
        def presenceLoadId = 1
        mockProviderBean.setup(presenceServiceId, presenceLoadId)
        setupSettings(presenceServiceId, null)
        def recordId = presenceService.addRecordToCurrentOutboundLoad("added", "123", [new PhoneRecord("123456", PhoneDescription.MOBILE)])
        mockProviderBean.noLoads = true

        when:
        presenceService.removeRecordFromOutboundLoad(recordId)

        then:
        noExceptionThrown()

        when:
        def records = presenceDataService.findRecords(new OutboundLoadRecordQuery())

        then:
        records
        records.size() == 1
        with(records[0]) {
            sourceId == 1000
            name == "added"
            status == OutboundLoadRecordStatus.UNLOADED
            !qualificationCode
            outboundLoad
            with(outboundLoad) {
                loadId == presenceLoadId
                serviceId == presenceServiceId
                status == OutboundLoadStatus.ENABLED
            }
            phoneRecords
            phoneRecords.size() == 1
            with(phoneRecords[0]) {
                number == "123456"
                description == PhoneDescription.MOBILE
            }
        }
    }

    def "remove record from predefined outbound load"() {
        given:
        def presenceServiceId = 1
        def presenceLoadId = 1
        mockProviderBean.setup(presenceServiceId, presenceLoadId)
        setupSettings(presenceServiceId, presenceLoadId)
        def recordId = presenceService.addRecordToCurrentOutboundLoad("test", "123", [new PhoneRecord("123456", PhoneDescription.MOBILE)])

        when:
        presenceService.removeRecordFromOutboundLoad(recordId)
        def records = presenceDataService.findRecords(new OutboundLoadRecordQuery())

        then:
        records
        records.size() == 1
        with(records[0]) {
            sourceId == 1000
            name == "test"
            status == OutboundLoadRecordStatus.UNLOADED
            !qualificationCode
            outboundLoad
            with(outboundLoad) {
                loadId == presenceLoadId
                serviceId == presenceServiceId
                status == OutboundLoadStatus.ENABLED
            }
            phoneRecords
            phoneRecords.size() == 1
            with(phoneRecords[0]) {
                number == "123456"
                description == PhoneDescription.MOBILE
            }
        }
    }

    def "remove record from current outbound load"() {
        given:
        def presenceServiceId = 1
        def presenceLoadId = 1
        mockProviderBean.setup(presenceServiceId, presenceLoadId)
        setupSettings(presenceServiceId, null)
        def recordId = presenceService.addRecordToCurrentOutboundLoad("test", "123", [new PhoneRecord("123456", PhoneDescription.MOBILE)])

        when:
        presenceService.removeRecordFromOutboundLoad(recordId)
        def records = presenceDataService.findRecords(new OutboundLoadRecordQuery())

        then:
        records
        records.size() == 1
        with(records[0]) {
            sourceId == 1000
            name == "test"
            status == OutboundLoadRecordStatus.UNLOADED
            !qualificationCode
            outboundLoad
            with(outboundLoad) {
                loadId == presenceLoadId
                serviceId == presenceServiceId
                status == OutboundLoadStatus.ENABLED
            }
            phoneRecords
            phoneRecords.size() == 1
            with(phoneRecords[0]) {
                number == "123456"
                description == PhoneDescription.MOBILE
            }
        }
    }

    def "update from current outbound load"() {
        given:
        def presenceServiceId = 1
        def presenceLoadId = 1
        mockProviderBean.setup(presenceServiceId, presenceLoadId)
        setupSettings(presenceServiceId, null)

        when:
        presenceService.updateCurrentOutboundLoad()
        def records = presenceDataService.findRecords(new OutboundLoadRecordQuery())

        then:
        records
        records.size() == 3
        with(records[0]) {
            sourceId == 1
            name == "Test1"
            status == OutboundLoadRecordStatus.COMPLETED
            qualificationCode == 1
            outboundLoad
            with(outboundLoad) {
                loadId == presenceLoadId
                serviceId == presenceServiceId
                status == OutboundLoadStatus.ENABLED
            }
            phoneRecords
            phoneRecords.size() == 1
            with(phoneRecords[0]) {
                number == "666000001"
                description == PhoneDescription.MOBILE
            }
        }
        with(records[1]) {
            sourceId == 2
            name == "Test2"
            status == OutboundLoadRecordStatus.COMPLETED
            qualificationCode == 1
            outboundLoad
            with(outboundLoad) {
                loadId == presenceLoadId
                serviceId == presenceServiceId
                status == OutboundLoadStatus.ENABLED
            }
            phoneRecords
            phoneRecords.size() == 1
            with(phoneRecords[0]) {
                number == "666000002"
                description == PhoneDescription.MOBILE
            }
        }
        with(records[2]) {
            sourceId == 3
            name == "Test3"
            status == OutboundLoadRecordStatus.PENDING
            qualificationCode == 1
            outboundLoad
            with(outboundLoad) {
                loadId == presenceLoadId
                serviceId == presenceServiceId
                status == OutboundLoadStatus.ENABLED
            }
            phoneRecords
            phoneRecords.size() == 1
            with(phoneRecords[0]) {
                number == "666000003"
                description == PhoneDescription.MOBILE
            }
        }

        when: "update twice"
        presenceService.updateCurrentOutboundLoad()
        records = presenceDataService.findRecords(new OutboundLoadRecordQuery())

        then: "nothing changed"
        records
        records.size() == 3
        with(records[0]) {
            sourceId == 1
            name == "Test1"
            status == OutboundLoadRecordStatus.COMPLETED
            qualificationCode == 1
            outboundLoad
            with(outboundLoad) {
                loadId == presenceLoadId
                serviceId == presenceServiceId
                status == OutboundLoadStatus.ENABLED
            }
            phoneRecords
            phoneRecords.size() == 1
            with(phoneRecords[0]) {
                number == "666000001"
                description == PhoneDescription.MOBILE
            }
        }
        with(records[1]) {
            sourceId == 2
            name == "Test2"
            status == OutboundLoadRecordStatus.COMPLETED
            qualificationCode == 1
            outboundLoad
            with(outboundLoad) {
                loadId == presenceLoadId
                serviceId == presenceServiceId
                status == OutboundLoadStatus.ENABLED
            }
            phoneRecords
            phoneRecords.size() == 1
            with(phoneRecords[0]) {
                number == "666000002"
                description == PhoneDescription.MOBILE
            }
        }
        with(records[2]) {
            sourceId == 3
            name == "Test3"
            status == OutboundLoadRecordStatus.PENDING
            qualificationCode == 1
            outboundLoad
            with(outboundLoad) {
                loadId == presenceLoadId
                serviceId == presenceServiceId
                status == OutboundLoadStatus.ENABLED
            }
            phoneRecords
            phoneRecords.size() == 1
            with(phoneRecords[0]) {
                number == "666000003"
                description == PhoneDescription.MOBILE
            }
        }
    }

    def "update from predefined outbound load"() {
        given:
        def presenceServiceId = 1
        def presenceLoadId = 1
        mockProviderBean.setup(presenceServiceId, presenceLoadId)
        setupSettings(presenceServiceId, presenceLoadId)

        when:
        presenceService.updateCurrentOutboundLoad()
        def records = presenceDataService.findRecords(new OutboundLoadRecordQuery())

        then:
        records
        records.size() == 3
        with(records[0]) {
            sourceId == 1
            name == "Test1"
            status == OutboundLoadRecordStatus.COMPLETED
            qualificationCode == 1
            outboundLoad
            with(outboundLoad) {
                loadId == presenceLoadId
                serviceId == presenceServiceId
                status == OutboundLoadStatus.ENABLED
            }
            phoneRecords
            phoneRecords.size() == 1
            with(phoneRecords[0]) {
                number == "666000001"
                description == PhoneDescription.MOBILE
            }
        }
        with(records[1]) {
            sourceId == 2
            name == "Test2"
            status == OutboundLoadRecordStatus.COMPLETED
            qualificationCode == 1
            outboundLoad
            with(outboundLoad) {
                loadId == presenceLoadId
                serviceId == presenceServiceId
                status == OutboundLoadStatus.ENABLED
            }
            phoneRecords
            phoneRecords.size() == 1
            with(phoneRecords[0]) {
                number == "666000002"
                description == PhoneDescription.MOBILE
            }
        }
        with(records[2]) {
            sourceId == 3
            name == "Test3"
            status == OutboundLoadRecordStatus.PENDING
            qualificationCode == 1
            outboundLoad
            with(outboundLoad) {
                loadId == presenceLoadId
                serviceId == presenceServiceId
                status == OutboundLoadStatus.ENABLED
            }
            phoneRecords
            phoneRecords.size() == 1
            with(phoneRecords[0]) {
                number == "666000003"
                description == PhoneDescription.MOBILE
            }
        }
    }

    def "update from empty outbound load"() {
        given:
        def presenceServiceId = 1
        def presenceLoadId = 1
        mockProviderBean.setup(presenceServiceId, presenceLoadId)
        mockProviderBean.noRecords = true
        setupSettings(presenceServiceId, presenceLoadId)

        when:
        presenceService.updateCurrentOutboundLoad()
        def records = presenceDataService.findRecords(new OutboundLoadRecordQuery())

        then:
        !records
    }

    def "update list of loads when a record is added to outbound load"() {
        given:
        def presenceServiceId = 1
        def presenceLoadId = 100
        mockProviderBean.setup(presenceServiceId, presenceLoadId)
        setupSettings(presenceServiceId, presenceLoadId)
        presenceService.addRecordToCurrentOutboundLoad("added", "123", [new PhoneRecord("123456", PhoneDescription.MOBILE)])

        when:
        def loads = presenceDataService.findLoads(new OutboundLoadQuery())

        then:
        loads
        loads.size() == 4
        with(loads[0]) {
            loadId == 1
            serviceId == presenceServiceId
            status == OutboundLoadStatus.ENABLED
            addedAt == DateUtils.dateTime("2019-03-19 11:41:15")
            description == "Test1"
        }
        with(loads[1]) {
            loadId == 2
            serviceId == presenceServiceId
            status == OutboundLoadStatus.DISABLED
            addedAt == DateUtils.dateTime("2019-03-19 15:47:00")
            description == "Test2"
        }
        with(loads[2]) {
            loadId == 3
            serviceId == presenceServiceId
            status == OutboundLoadStatus.DISABLED
            addedAt == DateUtils.dateTime("2019-03-19 15:49:50")
            description == "Test3"
        }
        with(loads[3]) {
            loadId == presenceLoadId
            serviceId == presenceServiceId
            status == OutboundLoadStatus.ENABLED
            addedAt == DateUtils.dateTime("2019-03-20 18:27:34")
            description == "Test"
        }
    }
}
