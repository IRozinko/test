package fintech.payments.spi;

import fintech.payments.impl.DisbursementProcessorRegistryBean;
import fintech.payments.model.DisbursementExportResult;
import fintech.payments.model.ExportPendingDisbursementCommand;

public interface DisbursementBatchProcessor {

    boolean isApplicable(DisbursementProcessorRegistryBean.FindBatchProcessorRequest request);

    DisbursementExportResult exportPendingDisbursements(ExportPendingDisbursementCommand command);

    DisbursementExportResult exportSingleDisbursement(long disbursementId);

    void retrySingleDisbursement(long disbursementId);

    void voidSingleDisbursement(long disbursementId);
}
