package fintech.email.mandrill

import com.google.common.collect.ImmutableList
import fintech.ClasspathUtils
import fintech.email.Email
import fintech.email.spi.EmailAttachment

import java.time.LocalDateTime

class ManualRun {

    public static void main(String[] args) {
        def provider = new MandrillEmailProvider("ihDVQVbKBGCrmFP_eKohRg")
        Email email = new Email(
            from: "info@prestoprestamos.es",
            fromName: "Presto",
            to: "ilgvarsj@gmail.com",
            subject: "Test " + LocalDateTime.now(),
            body: "Hello!"
        )
        def attachment = new EmailAttachment("test.pdf", "application/pdf")
        attachment.setBytes(ClasspathUtils.resourceToBytes("test.pdf"))
        def result = provider.send(email, ImmutableList.of(attachment))
        println result
    }
}
