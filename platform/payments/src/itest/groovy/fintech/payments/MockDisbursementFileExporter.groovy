package fintech.payments

import fintech.TimeMachine
import fintech.payments.model.DisbursementExportParams
import fintech.payments.spi.DisbursementFileExporter
import org.springframework.stereotype.Component

import java.time.format.DateTimeFormatter

@Component
class MockDisbursementFileExporter implements DisbursementFileExporter {

    public static final NAME = "mock-disbursement-processor"

    @Override
    String exporterName() {
        return NAME
    }

    @Override
    DisbursementFileExporter.ExportedFileInfo exportDisbursements(DisbursementExportParams params, OutputStream outputStream) {
        outputStream.write("Testing".getBytes())
        def dateStr = TimeMachine.now().format(DateTimeFormatter.BASIC_ISO_DATE)
        return new DisbursementFileExporter.ExportedFileInfo("mock-$dateStr/.csv", "text/csv")
    }

}
