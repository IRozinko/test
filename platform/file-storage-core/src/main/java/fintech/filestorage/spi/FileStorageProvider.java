package fintech.filestorage.spi;

import java.io.InputStream;

public interface FileStorageProvider {
    
    FileInfo store(String fileKey, InputStream is);
    
    InputStream getContent(String fileKey);
    
}
