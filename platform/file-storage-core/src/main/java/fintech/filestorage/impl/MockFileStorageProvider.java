package fintech.filestorage.impl;

import com.google.common.base.Throwables;
import fintech.Validate;
import fintech.filestorage.spi.FileInfo;
import fintech.filestorage.spi.FileStorageProvider;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Slf4j
@Component(MockFileStorageProvider.NAME)
public class MockFileStorageProvider implements FileStorageProvider {

    public static final String NAME = "mock-file-storage-provider";

    private Map<String, byte[]> store = new ConcurrentHashMap<>();

    @Override
    public FileInfo store(String fileKey, InputStream is) {
        log.warn("Mock file storage in use");
        try {
            byte[] bytes = IOUtils.toByteArray(is);
            FileInfo fileInfo = new FileInfo();
            fileInfo.setSize((long) bytes.length);
            store.put(fileKey, bytes);
            return fileInfo;
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

    @Override
    public InputStream getContent(String fileKey) {
        byte[] bytes = store.get(fileKey);
        Validate.notNull(bytes, "No content found by file key: [%s]", fileKey);
        return new ByteArrayInputStream(bytes);
    }
}
