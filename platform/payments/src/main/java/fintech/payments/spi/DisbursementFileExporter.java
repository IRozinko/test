package fintech.payments.spi;

import fintech.payments.model.DisbursementExportParams;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.OutputStream;

public interface DisbursementFileExporter {
    
    String exporterName();

    ExportedFileInfo exportDisbursements(DisbursementExportParams params, OutputStream outputStream);
    
    @Data
    @AllArgsConstructor
    class ExportedFileInfo {
        private String fileName;
        private String contentType;

    }

}
