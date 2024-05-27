package fintech.spain.alfa.product

import com.google.common.collect.ImmutableMap
import fintech.crm.client.ClientService
import fintech.crm.client.UpdateClientCommand
import fintech.notification.NotificationHelper
import fintech.spain.alfa.product.cms.CmsSetup
import fintech.spain.alfa.product.cms.PhoneVerificationModel
import fintech.spain.alfa.product.cms.AlfaCmsModels
import fintech.spain.alfa.product.cms.AlfaNotificationBuilderFactory
import org.springframework.beans.factory.annotation.Autowired

class NotificationSenderTest extends AbstractAlfaTest {

    @Autowired
    AlfaNotificationBuilderFactory notificationFactory

    @Autowired
    NotificationHelper notificationHelper

    @Autowired
    ClientService clientService

    def "Don't enqueue notification if communication blocked"() {
        when:
        fintech.spain.alfa.product.testing.TestClient client = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .signUp()

        and:
        notificationFactory.fromCustomerService(client.clientId)
            .render(CmsSetup.PHONE_VERIFICATION_NOTIFICATION, ImmutableMap.of(AlfaCmsModels.SCOPE_PHONE_VERIFICATION, new PhoneVerificationModel("test")))
            .send()

        then:
        notificationHelper.countEmails(client.email, CmsSetup.PHONE_VERIFICATION_NOTIFICATION) == 0
        notificationHelper.countSms(client.clientId, CmsSetup.PHONE_VERIFICATION_NOTIFICATION) == 1

        when:
        UpdateClientCommand command = UpdateClientCommand.fromClient(clientService.get(client.clientId))
        command.setBlockCommunication(true)

        and:
        clientService.update(command)

        and:
        notificationFactory.fromCustomerService(client.clientId)
            .render(CmsSetup.PHONE_VERIFICATION_NOTIFICATION, ImmutableMap.of(AlfaCmsModels.SCOPE_PHONE_VERIFICATION, new PhoneVerificationModel("test")))
            .send()

        then:
        notificationHelper.countEmails(client.email, CmsSetup.PHONE_VERIFICATION_NOTIFICATION) == 0
        notificationHelper.countSms(client.clientId, CmsSetup.PHONE_VERIFICATION_NOTIFICATION) == 1
    }
}
