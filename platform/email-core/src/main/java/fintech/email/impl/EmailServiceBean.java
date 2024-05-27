package fintech.email.impl;

import fintech.Validate;
import fintech.email.Email;
import fintech.email.EmailService;
import fintech.email.db.EmailLogEntity;
import fintech.email.db.EmailLogRepository;
import fintech.filestorage.CloudFile;
import fintech.filestorage.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

@Slf4j
@Component
class EmailServiceBean implements EmailService {

    @Autowired
    private EmailLogRepository repository;

    @Autowired
    private FileStorageService fileStorageService;

    @Transactional
    @Override
    public Long enqueue(Email email) {
        checkNotNull(email, "Null email");
        log.info("Enqueuing email [{}]", email);
        for (Long fileId : email.getAttachmentFileIds()) {
            Optional<CloudFile> cloudFile = fileStorageService.get(fileId);
            Validate.isTrue(cloudFile.isPresent(), "Attachment file not found: [%s]", fileId);
        }
        EmailLogEntity log = buildLog(email);
        return repository.saveAndFlush(log).getId();
    }

    private EmailLogEntity buildLog(Email email) {
        EmailLogEntity log = new EmailLogEntity();
        log.setFrom(email.getFrom());
        log.setFromName(email.getFromName());
        log.setReplyTo(email.getReplyTo());
        log.setTo(email.getTo());
        log.setSubject(email.getSubject());
        log.setBody(email.getBody());
        log.setNextAttemptAt(email.getSendAt());
        log.setSendingStatus(EmailLogEntity.Status.PENDING);
        log.setMaxAttempts(email.getMaxSendingAttempts());
        log.setAttemptTimeoutInSeconds(email.getAttemptTimeoutInSeconds());
        String fileIds = StringUtils.join(email.getAttachmentFileIds(), ",");
        log.setAttachmentFileIds(fileIds);
        return log;
    }
}
