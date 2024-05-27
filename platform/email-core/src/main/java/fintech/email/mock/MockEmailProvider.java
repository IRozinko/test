package fintech.email.mock;


import fintech.email.Email;
import fintech.email.spi.EmailAttachment;
import fintech.email.spi.EmailProvider;
import fintech.email.spi.EmailResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component(MockEmailProvider.NAME)
public class MockEmailProvider implements EmailProvider {

    public static final String NAME = "mock-email-provider";

    private RuntimeException nextException;

    private Email lastEmail;

    @Override
    public EmailResponse send(Email email, List<EmailAttachment> attachments) {
        this.lastEmail = email;
        log.info("Mock email sending: {}, attachments: {}", email, attachments.size());
        if (nextException != null) {
            RuntimeException exception = nextException;
            nextException = null;
            throw exception;
        }
        return EmailResponse.builder().id(UUID.randomUUID().toString()).providerName(NAME).build();
    }

    public void failNextEmail(RuntimeException exception) {
        this.nextException = exception;
    }

    public Email getLastEmail() {
        return lastEmail;
    }
}
