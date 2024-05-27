package fintech.cms

import fintech.cms.spi.CmsItem
import fintech.cms.spi.CmsItemType
import org.springframework.beans.factory.annotation.Autowired

class NotificationRenderTest extends BaseSpecification {

    @Autowired
    NotificationRenderer notificationRenderer

    def "Render email and sms texts"() {
        given:
        registerTemplate()
        def context = [client: [firstName: "John"], loan: [number: "123456"]]

        when: "Render English texts"
        def notification = notificationRenderer.render("agreement.notification", context, "es").get()

        then:
        with(notification.email.get()) {
            subject == "Your loan 123456 agreement"
            body == "Hi, John. Please find attached agreement."
        }
        with(notification.sms.get()) {
            text == "We have sent your loan 123456 agreement to the email."
        }

        when: "Render Latvian texts"
        notification = notificationRenderer.render("agreement.notification", context, "lv").get()

        then:
        with(notification.email.get()) {
            subject == "Jūsu kredīta 123456 līgums"
        }
        with(notification.sms.get()) {
            text == "Mēs nosūtījām jums kredīta 123456 līgumu uz epastu."
        }
    }


    def "Ignore unknown template key"() {
        expect:
        !notificationRenderer.render("unknown", [:], "en").isPresent()
    }

    private void registerTemplate() {
        registry.saveItem(new CmsItem(key: "agreement.notification", itemType: CmsItemType.NOTIFICATION,
            emailSubjectTemplate: "Your loan {{loan.number}} agreement",
            emailBodyTemplate: "Hi, {{client.firstName}}. Please find attached agreement.",
            smsTextTemplate: "We have sent your loan {{loan.number}} agreement to the email.",
            scope: "client, loan", locale: "es"), true)

        registry.saveItem(new CmsItem(key: "agreement.notification", itemType: CmsItemType.NOTIFICATION,
            emailSubjectTemplate: "Jūsu kredīta {{loan.number}} līgums",
            emailBodyTemplate: "Labdien, {{client.firstName}}. Nosūtam jums līgumu.",
            smsTextTemplate: "Mēs nosūtījām jums kredīta {{loan.number}} līgumu uz epastu.",
            scope: "client, loan", locale: "lv"), true)
    }
}
