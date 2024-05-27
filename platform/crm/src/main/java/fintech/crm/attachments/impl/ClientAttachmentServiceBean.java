package fintech.crm.attachments.impl;

import com.google.common.base.Throwables;
import fintech.PredicateBuilder;
import fintech.TimeMachine;
import fintech.Validate;
import fintech.crm.attachments.AddAttachmentCommand;
import fintech.crm.attachments.Attachment;
import fintech.crm.attachments.AttachmentStatus;
import fintech.crm.attachments.ClientAttachmentService;
import fintech.crm.attachments.db.ClientAttachmentEntity;
import fintech.crm.attachments.db.ClientAttachmentRepository;
import fintech.crm.attachments.event.AttachmentSavedEvent;
import fintech.crm.attachments.spi.AttachmentDefinition;
import fintech.crm.attachments.spi.ClientAttachmentRegistry;
import fintech.crm.client.db.ClientRepository;
import fintech.filestorage.CloudFile;
import fintech.filestorage.FileStorageService;
import fintech.filestorage.SaveFileCommand;
import fintech.filestorage.spi.FileContent;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static fintech.crm.db.Entities.clientAttachment;

@Slf4j
@Component
class ClientAttachmentServiceBean implements ClientAttachmentService {

    @Autowired
    private ClientAttachmentRepository repository;

    @Autowired
    private ClientAttachmentRegistry registry;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private FileStorageService fileStorageService;

    @Override
    @Transactional
    public Long addAttachment(AddAttachmentCommand command) {
        log.info("Adding attachment {}", command);

        AttachmentDefinition definition = registry.getDefinition(command.getAttachmentType());
        Validate.notBlank(command.getName(), "Attachment name is required: [%s]", command);
        Validate.isTrue(!command.isAutoApprove() || command.getAutoApproveTerm() != null,
            "Attachment [autoApproveTerm] can't be null with enabled [autoApprove]");

        ClientAttachmentEntity entity = new ClientAttachmentEntity();
        entity.setFileId(command.getFileId());
        entity.setClient(clientRepository.getRequired(command.getClientId()));
        entity.setName(command.getName());
        entity.setType(command.getAttachmentType());
        entity.setSubType(command.getAttachmentSubType());
        entity.setGroup(definition.getGroup());
        entity.setStatus(command.getStatus());
        entity.setStatusDetail(command.getStatusDetail());
        entity.setApplicationId(command.getApplicationId());
        entity.setLoanId(command.getLoanId());
        entity.setTransactionId(command.getTransactionId());
        entity.setAutoApprove(command.isAutoApprove());
        entity.setAutoApproveTerm(command.getAutoApproveTerm());

        Long id = repository.saveAndFlush(entity).getId();

        eventPublisher.publishEvent(new AttachmentSavedEvent()
            .setAttachmentId(id)
            .setClientId(command.getClientId())
            .setType(command.getAttachmentType()));

        return id;
    }

    @Transactional
    @Override
    public List<Attachment> findAttachments(AttachmentQuery query) {
        return repository.findAll(toPredicate(query).allOf(), clientAttachment.createdAt.desc()).stream()
            .filter(attachment -> registry.hasDefinition(attachment.getType()))
            .map((attachment) -> {
                AttachmentDefinition definition = registry.getDefinition(attachment.getType());
                return attachment.toValueObject(definition);
            }).collect(Collectors.toList());
    }

    @Override
    public Optional<Attachment> findLastAttachment(AttachmentQuery query) {
        AttachmentDefinition definition = registry.getDefinition(query.getType());
        return repository.findFirst(toPredicate(query).allOf(), clientAttachment.createdAt.desc())
            .filter(attachment -> registry.hasDefinition(attachment.getType()))
            .map(clientAttachmentEntity -> clientAttachmentEntity.toValueObject(definition));
    }

    @Override
    @Transactional
    public void updateStatus(Long attachmentId, String status, String statusDetail) {
        log.info("Updating attachment {} status to {}, {}", attachmentId, status, statusDetail);
        ClientAttachmentEntity entity = repository.getRequired(attachmentId);
        AttachmentDefinition definition = registry.getDefinition(entity.getType());
        Validate.isTrue(definition.getStatuses().contains(status), "Unknown attachment status [%s], attachment: %s", status, entity);
        entity.setStatus(status);
        entity.setStatusDetail(statusDetail);
        repository.saveAndFlush(entity);
    }

    @Override
    @Transactional
    public void setLoanId(Long attachmentId, Long loanId) {
        log.info("Updating attachment {} loan id to {}", attachmentId, loanId);
        Validate.notNull(loanId, "Null loan id");
        ClientAttachmentEntity entity = repository.getRequired(attachmentId);
        entity.setLoanId(loanId);
    }

    @Transactional
    @Override
    public Attachment get(Long attachmentId) {
        ClientAttachmentEntity entity = repository.getRequired(attachmentId);
        AttachmentDefinition definition = registry.getDefinition(entity.getType());
        return entity.toValueObject(definition);
    }

    @Override
    @Transactional
    public void autoApproveAttachments() {
        LocalDateTime now = TimeMachine.now();
        repository.findAll(clientAttachment.autoApprove.isTrue()
            .and(clientAttachment.status.eq(AttachmentStatus.WAITING_APPROVAL)))
            .stream()
            .filter(attachment -> now.isAfter(attachment.getCreatedAt().plusDays(attachment.getAutoApproveTerm())))
            .forEach(attachment ->
                updateStatus(attachment.getId(), AttachmentStatus.APPROVED, "AutoApproved"));
    }

    @SneakyThrows
    public CloudFile exportToZipArchive(List<Long> attachmentIds, String fileName) {
        List<FileContent> attachments = loadAttachments(attachmentIds);
        @Cleanup InputStream inputStream = zip(attachments);
        SaveFileCommand saveFileCommand = new SaveFileCommand();
        saveFileCommand.setOriginalFileName(fileName);
        saveFileCommand.setDirectory("temp-export");
        saveFileCommand.setInputStream(inputStream);
        saveFileCommand.setContentType("application/zip");
        return fileStorageService.save(saveFileCommand);
    }

    private List<FileContent> loadAttachments(List<Long> attachmentFileIds) {
        List<FileContent> fileContents = new LinkedList<>();
        for (Long fileId : attachmentFileIds) {
            Optional<CloudFile> cloudFile = fileStorageService.get(fileId);
            Validate.isTrue(cloudFile.isPresent(), "Attachment file not found: [%s]", fileId);
            log.info("Loading attachment content: [{}]", cloudFile.get());
            FileContent fileContent = new FileContent();
            fileContent.setName(cloudFile.get().getOriginalFileName());
            fileStorageService.readContents(fileId, inputStream -> {
                try {
                    byte[] bytes = IOUtils.toByteArray(inputStream);
                    fileContent.setContent(bytes);
                } catch (IOException e) {
                    throw Throwables.propagate(e);
                }
            });
            fileContents.add(fileContent);
        }
        return fileContents;
    }

    private InputStream zip(List<FileContent> attachments) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ZipOutputStream zipOutput = new ZipOutputStream(output);
        for (FileContent attachment : attachments) {
            zipOutput.putNextEntry(new ZipEntry(attachment.getName()));
            byte[] bytes = attachment.getContent();
            IOUtils.copy(new ByteArrayInputStream(bytes), zipOutput);
        }
        zipOutput.closeEntry();
        zipOutput.close();
        return new ByteArrayInputStream(output.toByteArray());
    }

    private PredicateBuilder toPredicate(AttachmentQuery query) {
        return new PredicateBuilder()
            .addIfPresent(query.getClientId(), clientAttachment.client.id::eq)
            .addIfPresent(query.getApplicationId(), clientAttachment.applicationId::eq)
            .addIfPresent(query.getStatus(), clientAttachment.status::in)
            .addIfPresent(query.getType(), clientAttachment.type::in)
            .addIfPresent(query.getLoanId(), clientAttachment.loanId::eq)
            .addIfPresent(query.getFileId(), clientAttachment.fileId::eq);
    }

}
