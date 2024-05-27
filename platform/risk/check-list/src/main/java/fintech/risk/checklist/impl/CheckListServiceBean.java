package fintech.risk.checklist.impl;

import com.google.common.base.Throwables;
import fintech.Validate;
import fintech.crm.client.Client;
import fintech.crm.client.ClientService;
import fintech.crm.logins.EmailLogin;
import fintech.crm.logins.EmailLoginService;
import fintech.filestorage.CloudFile;
import fintech.filestorage.FileStorageService;
import fintech.filestorage.SaveFileCommand;
import fintech.risk.checklist.CheckListEntry;
import fintech.risk.checklist.CheckListService;
import fintech.risk.checklist.commands.AddCheckListEntryCommand;
import fintech.risk.checklist.commands.AddCheckListTypeCommand;
import fintech.risk.checklist.commands.DeleteCheckListEntryCommand;
import fintech.risk.checklist.commands.ExportCheckListEntryCommand;
import fintech.risk.checklist.commands.ImportChecklistEntryCommand;
import fintech.risk.checklist.commands.UpdateCheckListEntryCommand;
import fintech.risk.checklist.db.CheckListEntryEntity;
import fintech.risk.checklist.db.CheckListEntryRepository;
import fintech.risk.checklist.db.CheckListTypeEntity;
import fintech.risk.checklist.db.CheckListTypeRepository;
import fintech.risk.checklist.db.Entities;
import fintech.risk.checklist.model.CheckListAction;
import fintech.risk.checklist.model.CheckListQuery;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static fintech.risk.checklist.CheckListConstants.CHECKLIST_TYPE_DNI;
import static fintech.risk.checklist.CheckListConstants.CHECKLIST_TYPE_EMAIL;
import static fintech.risk.checklist.CheckListConstants.CHECKLIST_TYPE_PHONE;
import static fintech.risk.checklist.db.Entities.entry;

@Slf4j
@Transactional
@Component
public class CheckListServiceBean implements CheckListService {

    private final CheckListTypeRepository typeRepository;
    private final CheckListEntryRepository entryRepository;
    private final FileStorageService fileStorageService;
    private final ClientService clientService;
    private final EmailLoginService emailLoginService;

    @Autowired
    public CheckListServiceBean(CheckListTypeRepository typeRepository, CheckListEntryRepository entryRepository,
                                FileStorageService fileStorageService, ClientService clientService,
                                EmailLoginService emailLoginService) {
        this.typeRepository = typeRepository;
        this.entryRepository = entryRepository;
        this.fileStorageService = fileStorageService;
        this.clientService = clientService;
        this.emailLoginService = emailLoginService;
    }

    @Override
    public Long addType(AddCheckListTypeCommand command) {
        String type = normalize(command.getType());
        Optional<CheckListTypeEntity> existing = typeRepository.findFirst(Entities.type.type.eq(type), Entities.type.id.desc());
        if (existing.isPresent()) {
            Validate.isTrue(existing.get().getAction() == command.getAction(), "Check list type already exists with different action: [%s] [%s]", command, existing);
            log.info("Check list type already exists: [{}]", type);
            return existing.get().getId();
        }
        CheckListTypeEntity entity = new CheckListTypeEntity();
        entity.setType(type);
        entity.setAction(command.getAction());
        return typeRepository.save(entity).getId();
    }

    @Override
    public Long addEntry(AddCheckListEntryCommand command) {
        CheckListTypeEntity type = findType(command.getType());
        Optional<CheckListEntryEntity> existing = entryRepository.findFirst(entry.type.eq(type)
            .and(entry.value1.eq(normalize(command.getValue1()))), entry.id.desc());
        if (existing.isPresent()) {
            log.info("Check list entry already exists: [{}]", command);
            return existing.get().getId();
        }
        updateBlockCommunication(type, command.getValue1(), true);
        CheckListEntryEntity entity = new CheckListEntryEntity();
        entity.setType(type);
        entity.setValue1(normalize(command.getValue1()));
        entity.setComment(command.getComment());
        return entryRepository.save(entity).getId();
    }

    private void updateBlockCommunication(CheckListTypeEntity type, String value, boolean isBlockCommunication) {
        if (type.getType().equalsIgnoreCase(CHECKLIST_TYPE_EMAIL)) {
            Optional<EmailLogin> client = emailLoginService.findByEmail(value);
            client.ifPresent(emailLogin -> clientService.updateBlockCommunication(
                emailLogin.getClientId(),
                isBlockCommunication,
                "Blacklisted by email"));
        } else if (type.getType().equalsIgnoreCase(CHECKLIST_TYPE_PHONE)) {
            Optional<Client> client = clientService.findByPhone(value);
            client.ifPresent(c -> clientService.updateBlockCommunication(
                c.getId(),
                isBlockCommunication,
                "Blacklisted by phone"));

        } else if (type.getType().equalsIgnoreCase(CHECKLIST_TYPE_DNI)) {
            Optional<Client> client = clientService.findByDocumentNumber(value);
            client.ifPresent(c -> clientService.updateBlockCommunication(
                c.getId(),
                isBlockCommunication,
                "Blacklisted by DNI"));
        }
    }

    private CheckListTypeEntity findType(String typeName) {
        typeName = normalize(typeName);
        CheckListTypeEntity type = typeRepository.findOne(Entities.type.type.eq(typeName));
        Validate.notNull(type, "Check list type not registered: [%s]", typeName);
        return type;
    }

    @Override
    public Long updateEntry(UpdateCheckListEntryCommand command) {
        CheckListTypeEntity type = findType(command.getType());
        CheckListEntryEntity entity = entryRepository.findOne(entry.id.eq(command.getId()));

        if (!entity.getValue1().equalsIgnoreCase(command.getValue1())) {
            updateBlockCommunication(type, entity.getValue1(), false);
        }
        Validate.notNull(entity, "No entry with type [%d] exists", command.getId());
        updateBlockCommunication(type, command.getValue1(), true);
        entity.setType(type);
        entity.setValue1(normalize(command.getValue1()));
        entity.setComment(command.getComment());
        return entryRepository.save(entity).getId();
    }

    @Override
    public boolean isAllowed(CheckListQuery query) {
        CheckListTypeEntity type = findType(query.getType());
        boolean exists = entryRepository.exists(entry.type.eq(type).and(entry.value1.eq(normalize(query.getValue1()))));
        if (type.getAction() == CheckListAction.WHITELIST) {
            long totalEntries = entryRepository.count(entry.type.eq(type));
            return exists || totalEntries == 0;
        } else {
            return !exists;
        }
    }

    @Override
    public List<CheckListEntry> find(CheckListQuery query) {
        CheckListTypeEntity type = findType(query.getType());
        return entryRepository.findAll(entry.type.eq(type).and(entry.value1.eq(normalize(query.getValue1())))).stream()
            .map(CheckListEntryEntity::toValueObject)
            .collect(Collectors.toList());
    }

    @Override
    public void delete(DeleteCheckListEntryCommand deleteCheckListEntryCommand) {
        CheckListEntryEntity entryEntity = entryRepository.getOne(deleteCheckListEntryCommand.getId());
        log.info("Deleting checklist entry [{}]", deleteCheckListEntryCommand.getId());
        entryRepository.delete(deleteCheckListEntryCommand.getId());
        log.info("Disabling attributes and disableCommunication");
        updateBlockCommunication(entryEntity.getType(), entryEntity.getValue1(), false);
    }

    @Override
    public void importChecklistEntries(ImportChecklistEntryCommand command) {
        Long fileId = command.getFileId();

        if (command.isOverride()) {
            CheckListTypeEntity type = findType(command.getType());
            List<CheckListEntryEntity> toRemove = entryRepository.findAll(entry.type.eq(type));
            entryRepository.delete(toRemove);
        }

        fileStorageService.readContents(fileId, inputStream -> {
            importFromInputStream(inputStream, command.getType());
        });
    }

    private void importFromInputStream(InputStream is, String type) {
        try {
            CsvBeanReader mapReader = new CsvBeanReader(new InputStreamReader(is), CsvPreference.STANDARD_PREFERENCE);
            mapReader.getHeader(true);

            AddCheckListEntryCommand value;
            while ((value = mapReader.read(AddCheckListEntryCommand.class, "type", "value1", "comment")) != null) {
                addEntry(AddCheckListEntryCommand.builder().type(type).value1(value.getValue1()).comment(value.getComment()).build());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CloudFile exportChecklistEntries(ExportCheckListEntryCommand command) {
        log.info("Exporting checklist entries: [{}]", command);

        CheckListTypeEntity type = findType(command.getType());
        List<AddCheckListEntryCommand> entries = entryRepository.findAll(entry.type.eq(type))
            .stream()
            .map(e ->
                AddCheckListEntryCommand.builder()
                    .type(e.getType().getType())
                    .value1(e.getValue1())
                    .comment(e.getComment())
                    .build())
            .collect(Collectors.toList());

        return exportToCloudFile(entries, new String[]{"type", "value1", "comment"}, command.getType());
    }

    private CloudFile exportToCloudFile(List<AddCheckListEntryCommand> values, String[] header, String type) {
        File tempFile = null;
        CsvBeanWriter csvBeanWriter = null;
        try {
            tempFile = File.createTempFile("checklist_entry", "export");

            csvBeanWriter = new CsvBeanWriter(new FileWriter(tempFile), CsvPreference.STANDARD_PREFERENCE);

            csvBeanWriter.writeHeader(header);
            for (AddCheckListEntryCommand v : values) {
                csvBeanWriter.write(v, header);
            }
            csvBeanWriter.close();

            @Cleanup FileInputStream fis = new FileInputStream(tempFile);
            SaveFileCommand saveFileCommand = new SaveFileCommand();
            saveFileCommand.setDirectory("checklist-export");
            saveFileCommand.setOriginalFileName(String.format("%s.csv", type));
            saveFileCommand.setContentType("application/csv");
            saveFileCommand.setInputStream(fis);
            return fileStorageService.save(saveFileCommand);
        } catch (IOException e) {
            throw Throwables.propagate(e);
        } finally {
            FileUtils.deleteQuietly(tempFile);
        }
    }

    private static String normalize(String value) {
        return StringUtils.upperCase(StringUtils.trim(value));
    }
}
