package fintech.bo.api.server;

import fintech.bo.api.model.disbursements.DisbursementExportResponse;
import fintech.bo.api.model.disbursements.ExportDisbursementsRequest;
import fintech.bo.api.model.disbursements.ExportSingleDisbursementRequest;
import fintech.bo.api.model.permissions.BackofficePermissions;
import fintech.payments.model.DisbursementExportResult;
import fintech.payments.model.ExportPendingDisbursementCommand;
import fintech.payments.spi.DisbursementBatchProcessor;
import fintech.payments.spi.DisbursementProcessorRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DisbursementApiController {

    @Autowired
    DisbursementProcessorRegistry disbursementProcessorRegistry;

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.DISBURSEMENT_EXPORT})
    @PostMapping("api/bo/disbursements/export")
    DisbursementExportResponse export(@RequestBody ExportDisbursementsRequest request) {
        DisbursementBatchProcessor batchProcessor = disbursementProcessorRegistry
            .findBatchProcessor(request.getInstitutionId(), request.getInstitutionAccountId());
        ExportPendingDisbursementCommand command = new ExportPendingDisbursementCommand(request.getInstitutionId(), request.getInstitutionAccountId());
        DisbursementExportResult disbursementExportResult = batchProcessor.exportPendingDisbursements(command);
        return toResponse(disbursementExportResult);
    }


    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.DISBURSEMENT_EXPORT})
    @PostMapping("api/bo/disbursements/export-single")
    DisbursementExportResponse exportSingle(@RequestBody ExportSingleDisbursementRequest request) {
        DisbursementBatchProcessor batchProcessor = disbursementProcessorRegistry.findBatchProcessor(request.getDisbursementId());
        DisbursementExportResult disbursementExportResult = batchProcessor.exportSingleDisbursement(request.getDisbursementId());
        return toResponse(disbursementExportResult);
    }

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.DISBURSEMENT_EXPORT})
    @PostMapping("api/bo/disbursements/{disbursementId}/void-single")
    ResponseEntity voidSingle(@PathVariable Long disbursementId) {
        DisbursementBatchProcessor batchProcessor = disbursementProcessorRegistry.findBatchProcessor(disbursementId);
        batchProcessor.voidSingleDisbursement(disbursementId);
        return ResponseEntity.noContent().build();
    }

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.DISBURSEMENT_EXPORT})
    @PostMapping("api/bo/disbursements/{disbursementId}/retry-single")
    ResponseEntity retrySingle(@PathVariable Long disbursementId) {
        DisbursementBatchProcessor batchProcessor = disbursementProcessorRegistry.findBatchProcessor(disbursementId);
        batchProcessor.retrySingleDisbursement(disbursementId);
        return ResponseEntity.noContent().build();
    }


    private DisbursementExportResponse toResponse(DisbursementExportResult disbursementExportResult) {
        DisbursementExportResponse disbursementExportResponse = new DisbursementExportResponse();
        disbursementExportResponse.setFileId(disbursementExportResult.getFileId());
        disbursementExportResponse.setFileName(disbursementExportResult.getOriginalFileName());
        return disbursementExportResponse;
    }
}
