package fintech.spain.alfa.product.lending

import fintech.JsonUtils
import fintech.TimeMachine
import fintech.notification.NotificationHelper
import fintech.settings.SettingsService
import fintech.settings.commands.UpdatePropertyCommand
import fintech.spain.alfa.product.AbstractAlfaTest
import fintech.spain.alfa.product.cms.CmsSetup
import fintech.spain.alfa.product.settings.AlfaSettings
import fintech.workflow.impl.WorkflowBackgroundJobs
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Subject

import static fintech.DateUtils.dateTime
import static fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.COLLECT_BASIC_INFORMATION
import static fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.EQUIFAX_RULES_RUN_1

class ExpiredLoanApplicationNotificationServiceTest extends AbstractAlfaTest {

    @Subject
    @Autowired
    ExpiredLoanApplicationNotificationService expiredLoanApplicationNotificationService

    @Autowired
    WorkflowBackgroundJobs workflowBackgroundJobs

    @Autowired
    NotificationHelper notificationHelper

    @Autowired
    SettingsService settingsService

    def "no loan applications"() {
        when:
        def applications = expiredLoanApplicationNotificationService.getExpiredLoanApplications(TimeMachine.now())

        then:
        !applications
    }

    def "no expired loan applications"() {
        given:
        fintech.spain.alfa.product.testing.TestFactory.newClient().signUp().acceptMarketing(true)

        when:
        def applications = expiredLoanApplicationNotificationService.getExpiredLoanApplications(TimeMachine.now())

        then:
        !applications
    }

    def "expired loan application but still no notification has to be sent"() {
        given:
        TimeMachine.useFixedClockAt(dateTime("2019-01-01 18:00:00"))
        fintech.spain.alfa.product.testing.TestFactory.newClient().signUp().acceptMarketing(true)

        when:
        TimeMachine.useFixedClockAt(dateTime("2019-01-02 18:00:00"))
        workflowBackgroundJobs.run(TimeMachine.now())
        def applications = expiredLoanApplicationNotificationService.getExpiredLoanApplications(TimeMachine.now())

        then:
        !applications
    }

    def "expired loan application but client is not accepting marketing communications"() {
        given:
        TimeMachine.useFixedClockAt(dateTime("2019-01-01 18:00:00"))
        fintech.spain.alfa.product.testing.TestFactory.newClient().signUp().acceptMarketing(false)

        when:
        TimeMachine.useFixedClockAt(dateTime("2019-01-02 18:00:00"))
        workflowBackgroundJobs.run(TimeMachine.now())
        def applications = expiredLoanApplicationNotificationService.getExpiredLoanApplications(TimeMachine.now().plusHours(3))

        then:
        !applications
    }

    def "expired loan application but a new one is created after"() {
        given:
        TimeMachine.useFixedClockAt(dateTime("2019-01-01 18:00:00"))
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient().signUp()

        when:
        TimeMachine.useFixedClockAt(dateTime("2019-01-02 18:00:00"))
        workflowBackgroundJobs.run(TimeMachine.now())
        client.submitApplicationAndStartFirstLoanWorkflow(100.0, 30, TimeMachine.today())
        def applications = expiredLoanApplicationNotificationService.getExpiredLoanApplications(TimeMachine.now().plusHours(3))

        then:
        !applications

        when: "the second application expires, we send the notification"
        TimeMachine.useFixedClockAt(dateTime("2019-01-03 18:00:00"))
        workflowBackgroundJobs.run(TimeMachine.now())
        applications = expiredLoanApplicationNotificationService.getExpiredLoanApplications(TimeMachine.now().plusHours(3))

        then:
        applications
        applications.size() == 1
    }

    def "expired loan application for all clients"() {
        given:
        TimeMachine.useFixedClockAt(dateTime("2019-01-01 18:00:00"))
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient().signUp().acceptMarketing(true)
        def repeatedClient = fintech.spain.alfa.product.testing.TestFactory.newClient().registerDirectly().acceptMarketing(true)
        repeatedClient.issueActiveLoan(100.0, 30, TimeMachine.today()).repayAll(TimeMachine.today())
        repeatedClient.submitApplicationAndStartFirstLoanWorkflow(100.0, 30, TimeMachine.today())
        repeatedClient.toLoanWorkflow().runAfterActivity(EQUIFAX_RULES_RUN_1)

        when:
        TimeMachine.useFixedClockAt(dateTime("2019-01-04 19:00:00"))
        workflowBackgroundJobs.run(TimeMachine.now())
        def applications = expiredLoanApplicationNotificationService.getExpiredLoanApplications(TimeMachine.now().plusHours(3))

        then:
        applications
        applications.size() == 2

        when: "send the notification..."
        expiredLoanApplicationNotificationService.sendNotification(applications[0])
        expiredLoanApplicationNotificationService.sendNotification(applications[1])

        then:
        notificationHelper.countEmails(client.clientId, CmsSetup.DROPOUT_NOTIFICATION) == 1
        notificationHelper.countEmails(repeatedClient.clientId, CmsSetup.DROPOUT_NOTIFICATION) == 1

        when: "...only once"
        applications = expiredLoanApplicationNotificationService.getExpiredLoanApplications(TimeMachine.now().plusHours(3))

        then:
        !applications
    }

    def "expired loan application but no notification has to be sent for new clients"() {
        given:
        def settings = settingsService.getJson(AlfaSettings.EXPIRED_APPLICATION_REMINDER_SETTINGS, ExpiredLoanApplicationReminderSettings.class)
        settings.newClients = false
        settingsService.update(new UpdatePropertyCommand(name: AlfaSettings.EXPIRED_APPLICATION_REMINDER_SETTINGS, textValue: JsonUtils.writeValueAsString(settings)))

        and:
        TimeMachine.useFixedClockAt(dateTime("2019-01-01 18:00:00"))
        fintech.spain.alfa.product.testing.TestFactory.newClient().signUp()

        when:
        TimeMachine.useFixedClockAt(dateTime("2019-01-02 18:00:00"))
        workflowBackgroundJobs.run(TimeMachine.now())
        def applications = expiredLoanApplicationNotificationService.getExpiredLoanApplications(TimeMachine.now())

        then:
        !applications
    }

    def "expired loan application but no notification has to be sent for repeated clients"() {
        given:
        def settings = settingsService.getJson(AlfaSettings.EXPIRED_APPLICATION_REMINDER_SETTINGS, ExpiredLoanApplicationReminderSettings.class)
        settings.repeatedClients = false
        settingsService.update(new UpdatePropertyCommand(name: AlfaSettings.EXPIRED_APPLICATION_REMINDER_SETTINGS, textValue: JsonUtils.writeValueAsString(settings)))

        and:
        TimeMachine.useFixedClockAt(dateTime("2019-01-01 18:00:00"))
        def repeatedClient = fintech.spain.alfa.product.testing.TestFactory.newClient().registerDirectly().acceptMarketing(true)
        repeatedClient.issueActiveLoan(100.0, 30, TimeMachine.today()).repayAll(TimeMachine.today())
        repeatedClient.submitApplicationAndStartFirstLoanWorkflow(100.0, 30, TimeMachine.today())
        repeatedClient.toLoanWorkflow().runAfterActivity(COLLECT_BASIC_INFORMATION)

        when:
        TimeMachine.useFixedClockAt(dateTime("2019-01-02 18:00:00"))
        workflowBackgroundJobs.run(TimeMachine.now())
        def applications = expiredLoanApplicationNotificationService.getExpiredLoanApplications(TimeMachine.now())

        then:
        !applications
    }

    def "send notifications - expired loan application for all clients that are not deleted"() {
        given:
        TimeMachine.useFixedClockAt(dateTime("2019-01-01 18:00:00"))
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient().signUp().acceptMarketing(true)
        def deletedClient = fintech.spain.alfa.product.testing.TestFactory.newClient().signUp().acceptMarketing(true).softDelete()
        def repeatedClient = fintech.spain.alfa.product.testing.TestFactory.newClient().registerDirectly().acceptMarketing(true)
        repeatedClient.issueActiveLoan(100.0, 30, TimeMachine.today()).repayAll(TimeMachine.today())
        repeatedClient.submitApplicationAndStartFirstLoanWorkflow(100.0, 30, TimeMachine.today())
        repeatedClient.toLoanWorkflow().runAfterActivity(EQUIFAX_RULES_RUN_1)

        when:
        TimeMachine.useFixedClockAt(dateTime("2019-01-04 19:00:00"))
        workflowBackgroundJobs.run(TimeMachine.now())
        def applications = expiredLoanApplicationNotificationService.getExpiredLoanApplications(TimeMachine.now().plusHours(3))

        then:
        applications
        applications.size() == 2

        when: "send the notification..."
        expiredLoanApplicationNotificationService.sendNotification(applications[0])
        expiredLoanApplicationNotificationService.sendNotification(applications[1])

        then:
        notificationHelper.countEmails(client.clientId, CmsSetup.DROPOUT_NOTIFICATION) == 1
        notificationHelper.countEmails(repeatedClient.clientId, CmsSetup.DROPOUT_NOTIFICATION) == 1
        notificationHelper.countEmails(deletedClient.clientId, CmsSetup.DROPOUT_NOTIFICATION) == 0

        when: "...only once"
        applications = expiredLoanApplicationNotificationService.getExpiredLoanApplications(TimeMachine.now().plusHours(3))

        then:
        !applications
    }
}
