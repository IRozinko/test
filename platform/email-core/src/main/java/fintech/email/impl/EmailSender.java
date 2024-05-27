package fintech.email.impl;


import com.google.common.base.Throwables;
import fintech.TimeMachine;
import fintech.Validate;
import fintech.email.Email;
import fintech.email.db.EmailLogEntity;
import fintech.email.db.EmailLogRepository;
import fintech.email.mock.MockEmailProvider;
import fintech.email.spi.EmailAttachment;
import fintech.email.spi.EmailException;
import fintech.email.spi.EmailProvider;
import fintech.email.spi.EmailResponse;
import fintech.filestorage.CloudFile;
import fintech.filestorage.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class EmailSender {

    @Resource(name = "${email.provider:" + MockEmailProvider.NAME + "}")
    private EmailProvider provider;

    @Autowired
    private EmailLogRepository repository;

    @Autowired
    private TransactionTemplate txTemplate;

    @Autowired
    private FileStorageService fileStorageService;

    @Value("${email.whitelistedEmails:}")
    private String whitelistedEmails;

    @Value("${email.whitelistedDomains:}")
    private String whitelistedDomains;

    @Transactional(propagation = Propagation.NEVER)
    public void send(final Long logId) {
        Email email = txTemplate.execute((status) -> repository.getRequired(logId).toEmail());
        try {
            if (shouldBeIgnored(email)) {
                logIgnored(logId);
                return;
            }
            List<EmailAttachment> attachments = loadAttachments(email.getAttachmentFileIds());
            EmailResponse response = provider.send(email, attachments);
            log.info("Email [{}] sent successfully, provider response [{}]", logId, response);
            logSuccess(logId, response);
        } catch (EmailException e) {
            log.warn("Failed to send email [{}], error [{}]", logId, e.getMessage());
            logError(logId, e.getMessage());
        } catch (Exception e) {
            log.error("Failed to send email " + logId, e);
            logError(logId, Throwables.getRootCause(e).getMessage());
        }
    }

    private boolean shouldBeIgnored(Email email) {
        if (StringUtils.isBlank(whitelistedDomains) && StringUtils.isBlank(whitelistedEmails)) {
            return false;
        }
        if (StringUtils.containsIgnoreCase(whitelistedEmails, email.getTo())) {
            return false;
        }
        String[] parts = email.getTo().split("@");
        if (parts.length >= 2) {
            String domain = parts[1];
            if (StringUtils.containsIgnoreCase(whitelistedDomains, domain)) {
                return false;
            }
        }
        return true;
    }

    private List<EmailAttachment> loadAttachments(List<Long> attachmentFileIds) {
        List<EmailAttachment> attachments = new ArrayList<>();
        for (Long fileId : attachmentFileIds) {
            Optional<CloudFile> cloudFile = fileStorageService.get(fileId);
            Validate.isTrue(cloudFile.isPresent(), "Attachment file not found: [%s]", fileId);
            log.info("Loading attachment content: [{}]", cloudFile.get());
            EmailAttachment attachment = new EmailAttachment(cloudFile.get().getOriginalFileName(), cloudFile.get().getContentType());
            fileStorageService.readContents(fileId, inputStream -> {
                try {
                    byte[] bytes = IOUtils.toByteArray(inputStream);
                    attachment.setBytes(bytes);
                } catch (IOException e) {
                    throw Throwables.propagate(e);
                }
            });
            attachments.add(attachment);
        }
        return attachments;
    }

    private void logError(Long logId, String errorMessage) {
        txTemplate.execute((status) -> {
            EmailLogEntity emailLog = repository.getRequired(logId);
            emailLog.setAttempts(emailLog.getAttempts() + 1);
            emailLog.setError(errorMessage);
            if (emailLog.getAttempts() >= emailLog.getMaxAttempts()) {
                emailLog.setSendingStatus(EmailLogEntity.Status.FAILED);
                log.info("Reached max [{}] sending attempts, marking email [{}] as failed", emailLog.getMaxAttempts(), logId);
            } else {
                int timeout = emailLog.getAttempts() * emailLog.getAttemptTimeoutInSeconds();
                emailLog.setNextAttemptAt(TimeMachine.now().plusSeconds(emailLog.getAttempts() * timeout + 1));
                log.info("Updating email [{}] next retry attempt to [{}]", logId, emailLog.getNextAttemptAt());
            }
            return emailLog;
        });
    }

    private void logSuccess(Long logId, EmailResponse response) {
        txTemplate.execute((status) -> {
            EmailLogEntity emailLog = repository.getRequired(logId);
            emailLog.setAttempts(emailLog.getAttempts() + 1);
            emailLog.setSendingStatus(EmailLogEntity.Status.SENT);
            emailLog.setProviderMessage(response.getMessage());
            emailLog.setProviderId(response.getId());
            emailLog.setProvider(response.getProviderName());
            return emailLog;
        });
    }

    private void logIgnored(Long logId) {
        txTemplate.execute((status) -> {
            EmailLogEntity emailLog = repository.getRequired(logId);
            emailLog.setAttempts(emailLog.getAttempts() + 1);
            emailLog.setSendingStatus(EmailLogEntity.Status.IGNORED);
            emailLog.setError("Not in whitelist");
            return emailLog;
        });
    }

    public void setWhitelistedEmails(String whitelistedEmails) {
        this.whitelistedEmails = whitelistedEmails;
    }

    public void setWhitelistedDomains(String whitelistedDomains) {
        this.whitelistedDomains = whitelistedDomains;
    }
}
