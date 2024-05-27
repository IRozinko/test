package fintech.email

import fintech.email.db.EmailLogEntity
import fintech.email.db.EmailLogRepository
import fintech.email.impl.EmailQueueConsumer
import fintech.email.mock.MockEmailProvider
import fintech.email.spi.EmailException
import org.springframework.beans.factory.annotation.Autowired

import static java.time.LocalDateTime.now

class EmailIntegrationTest extends BaseSpecification {

    @Autowired
    EmailService emailService

    @Autowired
    EmailQueueConsumer queueConsumer

    @Autowired
    EmailLogRepository repository

    @Autowired
    MockEmailProvider mockEmailProvider

    def "Send email successfully"() {
        given:
        def email = email()
        println email

        when:
        def id = emailService.enqueue(email)

        then:
        id > 0
        assertEmailStatus(id, EmailLogEntity.Status.PENDING, 0)

        when:
        queueConsumer.consumeNow()

        then:
        assertEmailStatus(id, EmailLogEntity.Status.SENT, 1)

        and:
        def log = repository.findOne(id)
        log.provider == MockEmailProvider.NAME

        when:
        queueConsumer.consumeNow()

        then: "No more attempts"
        assertEmailStatus(id, EmailLogEntity.Status.SENT, 1)
    }

    def "Fail sending when max attempts reached"() {
        given:
        def email = email()
        email.maxSendingAttempts = 2

        when:
        def id = emailService.enqueue(email)
        mockEmailProvider.failNextEmail(new EmailException("first error"))
        queueConsumer.consumeNow()

        then:
        assertEmailStatus(id, EmailLogEntity.Status.PENDING, 1)

        when:
        mockEmailProvider.failNextEmail(new RuntimeException("second error"))
        queueConsumer.consume(now().plusMinutes(10))

        then:
        assertEmailStatus(id, EmailLogEntity.Status.FAILED, 2)
        def log = repository.findOne(id)
        log.error == "second error"

        when:
        queueConsumer.consume(now().plusMinutes(10))

        then:
        assertEmailStatus(id, EmailLogEntity.Status.FAILED, 2)
    }


    def "Increase next attempt time"() {
        given:
        def email = email()
        email.maxSendingAttempts = 2
        email.attemptTimeoutInSeconds = 60

        when:
        def id = emailService.enqueue(email)
        mockEmailProvider.failNextEmail(new EmailException(""))
        queueConsumer.consumeNow();

        then:
        def log = repository.findOne(id)
        log.attemptTimeoutInSeconds == 60
        log.nextAttemptAt > now().plusSeconds(50)

        when:
        queueConsumer.consumeNow()

        then:
        assertEmailStatus(id, EmailLogEntity.Status.PENDING, 1)

        when:
        queueConsumer.consume(now().plusSeconds(61))
        queueConsumer.consumeNow()

        then:
        assertEmailStatus(id, EmailLogEntity.Status.SENT, 2)
    }

    def "Fail with invalid attachment file id"() {
        given:
        def email = email()
        email.attachmentFileIds = [-1L]

        when:
        emailService.enqueue(email)

        then:
        thrown(IllegalArgumentException.class)
    }

    def "Fields are stored"() {
        when:
        emailService.enqueue(new Email(
            from: "from@mailinator.com",
            fromName: "LOANS",
            to: "to@mailinator.com",
            subject: "subject",
            body: "body"))

        then:
        with(repository.findAll()[0].toEmail()) {
            from == "from@mailinator.com"
            fromName == "LOANS"
            to == "to@mailinator.com"
            body == "body"
            subject == "subject"
        }
    }

    def "Email whitelist by domain"() {
        given:
        sender.setWhitelistedDomains("gmail.com, mailinator.com, whatever.com")

        when:
        def email1 = email()
        email1.setTo("test@ibm.com")
        def id1 = emailService.enqueue(email1)
        queueConsumer.consumeNow()

        then:
        assertEmailStatus(id1, EmailLogEntity.Status.IGNORED, 1)

        when:
        def email2 = email()
        email2.setTo("john@MAILINATOR.COM")
        def id2 = emailService.enqueue(email2)
        queueConsumer.consumeNow()

        then:
        assertEmailStatus(id2, EmailLogEntity.Status.SENT, 1)
    }

    def "Email whitelist by email"() {
        given:
        sender.setWhitelistedEmails("john@gmail.com, john@mailinator.com, john@whatever.com")

        when:
        def email1 = email()
        email1.setTo("test@ibm.com")
        def id1 = emailService.enqueue(email1)
        queueConsumer.consumeNow()

        then:
        assertEmailStatus(id1, EmailLogEntity.Status.IGNORED, 1)

        when:
        def email2 = email()
        email2.setTo("john@MAILINATOR.COM")
        def id2 = emailService.enqueue(email2)
        queueConsumer.consumeNow()

        then:
        assertEmailStatus(id2, EmailLogEntity.Status.SENT, 1)
    }

    static Email email() {
        return new Email(from: "from@mailinator.com", fromName: "LOANS", to: "to@mailinator.com", subject: "subject", body: "body", maxSendingAttempts: 1)
    }

    private void assertEmailStatus(Long id, EmailLogEntity.Status status, int attempts) {
        def log = repository.findOne(id);
        assert log.sendingStatus == status
        assert log.attempts == attempts
    }
}
