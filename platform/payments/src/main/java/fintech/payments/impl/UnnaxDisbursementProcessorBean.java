package fintech.payments.impl;

import fintech.TimeMachine;
import fintech.db.config.RequiresNew;
import fintech.payments.DisbursementService;
import fintech.payments.InstitutionService;
import fintech.payments.model.Disbursement;
import fintech.payments.model.DisbursementExportResult;
import fintech.payments.model.DisbursementStatusDetail;
import fintech.payments.model.ExportPendingDisbursementCommand;
import fintech.payments.model.Institution;
import fintech.payments.spi.DisbursementBatchProcessor;
import fintech.spain.unnax.UnnaxPayOutService;
import fintech.transactions.TransactionService;
import fintech.transactions.TransactionType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

@Slf4j
@Component
@Transactional
public class UnnaxDisbursementProcessorBean implements DisbursementBatchProcessor {

    public static final String UNNAX_EXPORTER = "UNNAX";

    private final UnnaxPayOutService unnaxPayOutService;
    private final DisbursementService disbursementService;
    private final InstitutionService institutionService;
    private final TransactionService transactionService;
    private final TransactionTemplate tx;

    public UnnaxDisbursementProcessorBean(UnnaxPayOutService unnaxPayOutService, DisbursementService disbursementService,
                                          InstitutionService institutionService, TransactionService transactionService,
                                          @RequiresNew TransactionTemplate tx) {
        this.unnaxPayOutService = unnaxPayOutService;
        this.disbursementService = disbursementService;
        this.institutionService = institutionService;
        this.transactionService = transactionService;
        this.tx = tx;
    }

    @Override
    public boolean isApplicable(DisbursementProcessorRegistryBean.FindBatchProcessorRequest request) {
        Institution institution = institutionService.getInstitution(request.getInstitutionId());
        return UNNAX_EXPORTER.equals(institution.getStatementApiExporter());
    }

    @Override
    public DisbursementExportResult exportPendingDisbursements(ExportPendingDisbursementCommand command) {
        List<Disbursement> disbursements = disbursementService.findDisbursements(DisbursementService.DisbursementQuery.pending(command.getInstitutionId(), command.getInstitutionAccountId()));
        return disbursements.stream()
            .map(Disbursement::getId)
            .map(this::exportSingleDisbursement)
            .reduce(DisbursementExportResult::add)
            .orElseGet(DisbursementExportResult::empty);
    }

    @Override
    public DisbursementExportResult exportSingleDisbursement(long disbursementId) {
        unnaxPayOutService.enqueueTransferOut(disbursementId);

        DisbursementExportResult result = DisbursementExportResult.exported();
        disbursementService.exported(disbursementId, TimeMachine.now(), result);
        return result;
    }

    @Override
    public void retrySingleDisbursement(long disbursementId) {
        Disbursement disbursement = disbursementService.getDisbursement(disbursementId);
        if (disbursement.getStatusDetail().equals(DisbursementStatusDetail.EXPORT_ERROR)) {
            transactionService.voidDisbursementTransaction(disbursementId, TransactionType.DISBURSEMENT);
            disbursementService.invalid(disbursement.getId(), disbursement.getError());
            disbursementService.pending(disbursement.getId());

        } else {
            DisbursementExportResult result = DisbursementExportResult.exported();
            tx.execute(s -> {
                disbursementService.exported(disbursementId, TimeMachine.now(), result);
                return null;
            });
            unnaxPayOutService.retryTransferOut(disbursement.getReference());
        }
    }

    @Override
    public void voidSingleDisbursement(long disbursementId) {
        disbursementService.voidDisbursement(disbursementId, "");
    }
}
