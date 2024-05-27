package fintech.filestorage.impl;

import com.google.common.base.Throwables;
import fintech.TimeMachine;
import fintech.filestorage.CloudFile;
import fintech.filestorage.FileStorageService;
import fintech.filestorage.SaveFileCommand;
import fintech.filestorage.db.CloudFileEntity;
import fintech.filestorage.db.CloudFileRepository;
import fintech.filestorage.db.Entities;
import fintech.filestorage.spi.FileInfo;
import fintech.filestorage.spi.FileStorageProvider;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.hibernate.annotations.OptimisticLockType;
import org.hibernate.annotations.OptimisticLocking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.google.common.base.Preconditions.checkNotNull;

@Slf4j
@Transactional
@Component
@OptimisticLocking(type = OptimisticLockType.NONE)
class FileStorageServiceBean implements FileStorageService {

    @Autowired
    private CloudFileRepository cloudFileRepository;

    @Resource(name = "${fileStorage.provider:" + MockFileStorageProvider.NAME + "}")
    private FileStorageProvider fileStorageProvider;

    @Transactional
    @Override
    public CloudFile save(SaveFileCommand command) {
        log.info("Saving file: [{}]", command);
        checkNotNull(command.getDirectory(), "Directory should be provided");
        checkNotNull(command.getOriginalFileName(), "Original file name should be provided");
        checkNotNull(command.getInputStream(), "Input stream should be provided");
        checkNotNull(command.getContentType(), "Content type should be provided");

        String fileUuid = UUID.randomUUID().toString();
        CloudFileEntity entity = new CloudFileEntity();
        entity.setOriginalFileName(command.getOriginalFileName());
        entity.setContentType(command.getContentType());
        entity.setDirectory(command.getDirectory());
        entity.setFileUuid(fileUuid);

        FileInfo fileInfo = fileStorageProvider.store(fileUuid, command.getInputStream());
        entity.setFileSize(fileInfo.getSize());
        entity = cloudFileRepository.saveAndFlush(entity);
        log.info("File saved: [{}]", entity);
        return entity.toValueObject();
    }

    @Override
    public Optional<CloudFile> get(Long fileId) {
        return cloudFileRepository.getOptional(Entities.cloudFile.id.eq(fileId)).map(CloudFileEntity::toValueObject);
    }


    @Override
    public void readContents(Long fileId, Consumer<InputStream> consumer) {
        CloudFileEntity entity = cloudFileRepository.getRequired(fileId);
        try (InputStream is = fileStorageProvider.getContent(entity.getFileUuid())) {
            consumer.accept(is);
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
        logDownloaded(entity);
    }

    @Override
    public <T> T readContents(Long fileId, Function<InputStream, T> function) {
        CloudFileEntity entity = cloudFileRepository.getRequired(fileId);
        T result = readContentsWithFunction(function, entity);
        logDownloaded(entity);
        return result;
    }

    @Override
    public String readContentAsString(Long fileId, Charset charset) {
        return readContents(fileId, inputStream -> {
            try {
                return IOUtils.toString(inputStream, charset);
            } catch (IOException e) {
                throw Throwables.propagate(e);
            }
        });
    }

    private <T> T readContentsWithFunction(Function<InputStream, T> function, CloudFileEntity entity) {
        try (InputStream is = fileStorageProvider.getContent(entity.getFileUuid())) {
            return function.apply(is);
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

    private void logDownloaded(CloudFileEntity entity) {
        entity.setLastDownloadedAt(TimeMachine.now());
        entity.setTimesDownloaded(entity.getTimesDownloaded() + 1);
    }

}
