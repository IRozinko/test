package fintech.filestorage.impl;

import fintech.filestorage.spi.FileInfo;
import fintech.filestorage.spi.FileStorageProvider;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@Slf4j
@Component(LocalFileStorageProvider.NAME)
public class LocalFileStorageProvider implements FileStorageProvider {

    public static final String NAME = "local-file-storage-provider";

    private final Path storageRoot = Paths.get("storage");

    @SneakyThrows
    @Override
    public FileInfo store(String fileKey, InputStream is) {
        Path file = Files.createFile(storageRoot.resolve(fileKey));
        try (OutputStream os = Files.newOutputStream(file)) {
            IOUtils.copy(is, os);
        }
        FileInfo fileInfo = new FileInfo();
        fileInfo.setSize(file.toFile().length());
        return fileInfo;
    }

    @SneakyThrows
    @Override
    public InputStream getContent(String fileKey) {
        Path file = storageRoot.resolve(fileKey);
        if (Files.exists(file)) {
            InputStream is = Files.newInputStream(file);
            return new BufferedInputStream(is);
        } else {
            throw new IllegalArgumentException("File not found: " + fileKey);
        }
    }

    @PostConstruct
    public void initStorage() throws IOException {
        if (Files.notExists(storageRoot)) {
            Files.createDirectory(storageRoot);
        }
    }
}
