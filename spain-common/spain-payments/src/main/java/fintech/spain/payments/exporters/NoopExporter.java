package fintech.spain.payments.exporters;

import fintech.payments.model.DisbursementExportParams;
import fintech.payments.spi.DisbursementFileExporter;
import org.springframework.stereotype.Component;

import java.io.OutputStream;

@Component
public class NoopExporter implements DisbursementFileExporter {

    public static final String EXPORTER_NAME = "dummy-csv";

    @Override
    public String exporterName() {
        return EXPORTER_NAME;
    }

    @Override
    public ExportedFileInfo exportDisbursements(DisbursementExportParams params, OutputStream outputStream) {
        return new DisbursementFileExporter.ExportedFileInfo("dummy.csv", "csv");
    }

}
