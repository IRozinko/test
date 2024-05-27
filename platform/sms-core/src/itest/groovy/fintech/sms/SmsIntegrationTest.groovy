package fintech.sms

import fintech.sms.db.IncomingSmsRepository
import fintech.sms.db.SmsLogEntity
import fintech.sms.db.SmsLogRepository
import fintech.sms.impl.SmsQueueConsumer
import fintech.sms.mock.MockSmsProvider
import fintech.sms.spi.SmsException
import org.springframework.beans.factory.annotation.Autowired

import static fintech.DateUtils.dateTime
import static java.time.LocalDateTime.now

class SmsIntegrationTest extends BaseSpecification {

    @Autowired
    SmsService smsService

    @Autowired
    SmsQueueConsumer queueConsumer

    @Autowired
    SmsLogRepository repository

    @Autowired
    MockSmsProvider mockSmsProvider

    @Autowired
    IncomingSmsRepository incomingSmsRepository

    def "Send SMS successfully"() {
        when:
        def sms = sms()
        sms.setMaxSendingAttempts(1)
        def id = smsService.enqueue(sms)

        then:
        id > 0
        assertSmsStatus(id, SmsLogEntity.Status.PENDING, 0)

        when:
        queueConsumer.consume(now())

        then:
        assertSmsStatus(id, SmsLogEntity.Status.SENT, 1);

        and:
        def log = repository.findOne(id)
        log.provider == MockSmsProvider.NAME
        log.providerId != null

        when:
        queueConsumer.consume(now())

        then: "No more attempts"
        assertSmsStatus(id, SmsLogEntity.Status.SENT, 1)
    }

    def "Send fails when attempt limit reached"() {
        given:
        def sms = sms()
        sms.setAttemptTimeoutInSeconds(0)
        sms.setMaxSendingAttempts(2)

        when:
        def id = smsService.enqueue(sms)
        mockSmsProvider.failNextSms(new SmsException("first error"))
        queueConsumer.consume(now())

        then:
        assertSmsStatus(id, SmsLogEntity.Status.PENDING, 1)

        when:
        mockSmsProvider.failNextSms(new RuntimeException("second error"))
        queueConsumer.consume(now())

        then:
        assertSmsStatus(id, SmsLogEntity.Status.FAILED, 2)
        def log = repository.findOne(id)
        log.error == "second error"

        when:
        queueConsumer.consume(now())

        then:
        assertSmsStatus(id, SmsLogEntity.Status.FAILED, 2)
    }


    def "Next attempt time is increased"() {
        given:
        def sms = sms()
        sms.setAttemptTimeoutInSeconds(60)
        sms.setMaxSendingAttempts(2)

        when:
        def id = smsService.enqueue(sms)
        mockSmsProvider.failNextSms(new SmsException(""))
        queueConsumer.consume(now())

        then:
        def log = repository.findOne(id)
        log.attemptTimeoutInSeconds == 60
        log.nextAttemptAt > now().plusSeconds(50)

        when:
        queueConsumer.consume(now())

        then:
        assertSmsStatus(id, SmsLogEntity.Status.PENDING, 1)

        when:
        queueConsumer.consume(now().plusSeconds(61))
        queueConsumer.consume(now())

        then:
        assertSmsStatus(id, SmsLogEntity.Status.SENT, 2)
    }


    def "Delivery report"() {
        when:
        def sms = sms()
        def id = smsService.enqueue(sms)
        queueConsumer.consume(now())

        then:
        def log = repository.findOne(id)
        log.providerId != null
        log.deliveryReportStatus == null

        when:
        smsService.deliveryReportReceived(new SmsDeliveryReport(
            providerMessageId: log.providerId,
            status: "FAILED",
            status2: "REJECTED",
            error: "Not enough funds",
            receivedAt: dateTime("2016-01-01 12:00:00")
        ))
        log = repository.findOne(id)

        then:
        log.deliveryReportReceivedAt == dateTime("2016-01-01 12:00:00")
        log.deliveryReportStatus == "FAILED"
        log.deliveryReportStatus2 == "REJECTED"
        log.deliveryReportError == "Not enough funds"
    }

    def "Save incoming"() {
        expect:
        incomingSmsRepository.count() == 0

        when:
        smsService.takeIncomingSms(new IncomingSms(source: "altiria", phoneNumber: "1", text: "test", rawDataJson: "{}"))

        then:
        incomingSmsRepository.count() == 1
        with(incomingSmsRepository.findAll()[0]) {
            source == "altiria"
            phoneNumber == "1"
            text == "test"
            rawDataJson == "{}"
        }
    }

    def "Fields are stored"() {
        when:
        smsService.enqueue(new Sms(
            senderId: "sender",
            to: "12345",
            text: "Hello"))

        then:
        with(repository.findAll()[0].toSms()) {
            senderId == "sender"
            to == "12345"
            text == "Hello"
        }
    }

    def "SMS whitelisted numbers"() {
        given:
        sender.setWhitelistedNumbers("123,456,789")

        when:
        def sms1 = sms()
        sms1.setTo("6789")
        def id = smsService.enqueue(sms1)
        queueConsumer.consume(now())

        then:
        assertSmsStatus(id, SmsLogEntity.Status.IGNORED, 1)

        when:
        def sms2 = sms()
        sms2.setTo("456")
        def id2 = smsService.enqueue(sms2)
        queueConsumer.consume(now())

        then:
        assertSmsStatus(id2, SmsLogEntity.Status.SENT, 1)
    }

    static Sms sms() {
        return new Sms(senderId: "sender", to: "12345", text: "Hello")
    }


    void assertSmsStatus(Long id, SmsLogEntity.Status status, int attempts) {
        def log = repository.findOne(id)
        assert log.sendingStatus == status
        assert log.attempts == attempts
    }
}
