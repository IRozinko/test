package fintech.payments.model;

import fintech.filestorage.CloudFile;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

@Data
@Accessors(chain = true)
@NoArgsConstructor
public class DisbursementExportResult {

    private int exportedDisbursementCount;
    private int failedDisbursementCount;
    private Long fileId;
    private String originalFileName;

    public DisbursementExportResult(int exportedDisbursementCount, CloudFile file) {
        this.exportedDisbursementCount = exportedDisbursementCount;
        this.fileId = file.getFileId();
        this.originalFileName = file.getOriginalFileName();
    }

    public DisbursementExportResult add(DisbursementExportResult result) {
        this.exportedDisbursementCount += result.exportedDisbursementCount;
        this.failedDisbursementCount += result.failedDisbursementCount;
        return this;
    }

    public boolean isExported() {
        return exportedDisbursementCount > 0 && failedDisbursementCount == 0;
    }

    public boolean isFile() {
        return fileId != null && StringUtils.isNotBlank(originalFileName);
    }

    public static DisbursementExportResult exported() {
        return new DisbursementExportResult().setExportedDisbursementCount(1);
    }

    public static DisbursementExportResult failed() {
        return new DisbursementExportResult().setFailedDisbursementCount(1);
    }

    public static DisbursementExportResult empty() {
        return new DisbursementExportResult();
    }

}
