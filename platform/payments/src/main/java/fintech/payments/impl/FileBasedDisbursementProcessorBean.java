package fintech.payments.impl;


import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import fintech.TimeMachine;
import fintech.Validate;
import fintech.filestorage.CloudFile;
import fintech.filestorage.FileStorageService;
import fintech.filestorage.SaveFileCommand;
import fintech.payments.DisbursementService;
import fintech.payments.InstitutionService;
import fintech.payments.model.*;
import fintech.payments.spi.DisbursementBatchProcessor;
import fintech.payments.spi.DisbursementFileExporter;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;


@Slf4j
@Component
@RequiredArgsConstructor
public class FileBasedDisbursementProcessorBean implements DisbursementBatchProcessor {

    private final InstitutionService institutionService;
    private final FileStorageService fileStorageService;
    private final DisbursementService disbursementService;

    @Autowired(required = false)
    private Set<DisbursementFileExporter> exporters;

    @Override
    public boolean isApplicable(DisbursementProcessorRegistryBean.FindBatchProcessorRequest request) {
        Institution institution = institutionService.getInstitution(request.getInstitutionId());
        return StringUtils.isNotBlank(institution.getStatementExportFormat());
    }

    @Override
    public DisbursementExportResult exportSingleDisbursement(long disbursementId) {
        Disbursement disbursement = disbursementService.getDisbursement(disbursementId);
        DisbursementExportResult result = exportDisbursements(disbursement.getInstitutionId(), disbursement.getInstitutionAccountId(),
            ImmutableList.of(disbursement));
        disbursementService.exported(disbursementId, TimeMachine.now(), result);
        return result;
    }

    @Override
    public void retrySingleDisbursement(long disbursementId) {
        throw new UnsupportedOperationException("Method retrySingleDisbursement unsupported for file based disbursement");
    }

    @Override
    public void voidSingleDisbursement(long disbursementId) {
        disbursementService.voidDisbursement(disbursementId, "");
    }

    @Override
    public DisbursementExportResult exportPendingDisbursements(ExportPendingDisbursementCommand command) {
        log.info("Exporting disbursements: [{}]", command);
        List<Disbursement> disbursements = disbursementService.findDisbursements(DisbursementService.DisbursementQuery.pending(command.getInstitutionId(),
            command.getInstitutionAccountId()));

        log.info("Found {} disbursements for export", disbursements.size());
        if (disbursements.isEmpty())
            return DisbursementExportResult.empty();

        DisbursementExportResult exportResult = exportDisbursements(command.getInstitutionId(),
            command.getInstitutionAccountId(), disbursements);

        LocalDateTime now = TimeMachine.now();
        disbursements.forEach(d -> disbursementService.exported(d.getId(), now, exportResult));
        return exportResult;
    }

    public DisbursementExportResult exportDisbursements(Long institutionId, Long institutionAccountId, List<Disbursement> disbursements) {
        Validate.isTrue(!disbursements.isEmpty());
        Institution institution = institutionService.getInstitution(institutionId);
        InstitutionAccount institutionAccount = institutionService.getAccount(institutionAccountId);
        DisbursementFileExporter exporter = findExporter(institution);
        CloudFile file = exportToCloudFile(institution, institutionAccount, exporter, disbursements);
        return new DisbursementExportResult(disbursements.size(), file);
    }

    private CloudFile exportToCloudFile(Institution institution, InstitutionAccount institutionAccount, DisbursementFileExporter exporter, List<Disbursement> pendingDisbursements) {
        File tempFile = null;
        try {
            tempFile = File.createTempFile("disbursement", "export");
            @Cleanup FileOutputStream fos = new FileOutputStream(tempFile);
            DisbursementExportParams params = new DisbursementExportParams();
            params.setInstitution(institution);
            params.setInstitutionAccount(institutionAccount);
            params.setDisbursements(pendingDisbursements);
            DisbursementFileExporter.ExportedFileInfo exportedFile = exporter.exportDisbursements(params, fos);
            @Cleanup FileInputStream fis = new FileInputStream(tempFile);

            SaveFileCommand saveFileCommand = new SaveFileCommand();
            saveFileCommand.setDirectory("disbursement-export");
            saveFileCommand.setOriginalFileName(exportedFile.getFileName());
            saveFileCommand.setContentType(exportedFile.getContentType());
            saveFileCommand.setInputStream(fis);
            return fileStorageService.save(saveFileCommand);
        } catch (IOException e) {
            throw Throwables.propagate(e);
        } finally {
            FileUtils.deleteQuietly(tempFile);
        }
    }

    private DisbursementFileExporter findExporter(Institution institution) {
        return exporters.stream().filter(it -> it.exporterName().equals(institution.getStatementExportFormat()))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Disbursement exporter not found"));
    }


}
