package fintech.email.spi;


import fintech.email.Email;

import java.util.List;

public interface EmailProvider {

    EmailResponse send(Email email, List<EmailAttachment> attachments);

}
