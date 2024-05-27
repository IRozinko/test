package fintech.email.mandrill;

import com.google.common.collect.Lists;
import com.microtripit.mandrillapp.lutung.MandrillApi;
import com.microtripit.mandrillapp.lutung.model.MandrillApiError;
import com.microtripit.mandrillapp.lutung.view.MandrillMessage;
import com.microtripit.mandrillapp.lutung.view.MandrillMessageStatus;
import fintech.email.Email;
import fintech.email.spi.EmailAttachment;
import fintech.email.spi.EmailException;
import fintech.email.spi.EmailProvider;
import fintech.email.spi.EmailResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component(MandrillEmailProvider.NAME)
public class MandrillEmailProvider implements EmailProvider {

    static final String NAME = "mandrill";

    private String apiKey;

    public MandrillEmailProvider(@Value("${email.mandrill.apiKey:REPLACE}") String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public EmailResponse send(Email email, List<EmailAttachment> attachments) {
        MandrillApi mandrill = new MandrillApi(apiKey);

        MandrillMessage mandrillMessage = new MandrillMessage();
        mandrillMessage.setTo(getRecipients(email.getTo()));
        mandrillMessage.setSubject(email.getSubject());
        mandrillMessage.setFromName(email.getFrom());
        mandrillMessage.setFromName(email.getFromName());
        mandrillMessage.setFromEmail(email.getFrom());
        mandrillMessage.setHtml(email.getBody());
        mandrillMessage.setAttachments(toMandrillAttachments(attachments));
        if (!StringUtils.isEmpty(email.getReplyTo())) {
            mandrillMessage.setHeaders(Collections.singletonMap("Reply-To", email.getReplyTo()));
        }
        try {
            MandrillMessageStatus[] statuses = mandrill
                .messages()
                .send(mandrillMessage, true);
            MandrillMessageStatus status = statuses[0];
            return EmailResponse.builder()
                .providerName(NAME)
                .id(status.getId())
                .message(getMessage(status))
                .build();
        } catch (MandrillApiError e) {
            throw new EmailException("Failed to send email via Mandrill: " + e.getMandrillErrorMessage(), e);
        } catch (IOException e) {
            throw new EmailException("Failed to send email via Mandrill: " + e.getMessage(), e);
        }
    }

    private List<MandrillMessage.MessageContent> toMandrillAttachments(List<EmailAttachment> attachments) {
        return attachments.stream().map(attachment -> {
            MandrillMessage.MessageContent content = new MandrillMessage.MessageContent();
            content.setBinary(true);
            content.setContent(Base64.getEncoder().encodeToString(attachment.getBytes()));
            content.setName(attachment.getFileName());
            content.setType(attachment.getContentType());
            return content;
        }).collect(Collectors.toList());
    }

    private String getMessage(MandrillMessageStatus status) {
        return status.getStatus() + Optional.ofNullable(status.getRejectReason())
            .map(s -> ":" + s)
            .orElse("");
    }

    private List<MandrillMessage.Recipient> getRecipients(String to) {
        MandrillMessage.Recipient recipient = new MandrillMessage.Recipient();
        recipient.setEmail(to);
        return Lists.newArrayList(recipient);
    }
}
