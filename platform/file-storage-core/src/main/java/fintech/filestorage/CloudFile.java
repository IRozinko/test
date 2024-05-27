package fintech.filestorage;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CloudFile {
    
    private Long fileId;
    private Long fileSize;
    private String originalFileName;
    private String contentType;
    private Long timesDownloaded;
    private LocalDateTime lastDownloadedAt;

    public CloudFile(Long fileId, String originalFileName) {
        this.fileId = fileId;
        this.originalFileName = originalFileName;
    }
}
