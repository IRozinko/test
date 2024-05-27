package fintech.spain.alfa.product.dc.impl;

import com.google.common.base.Stopwatch;
import com.google.common.net.MediaType;
import fintech.IoUtils;
import fintech.TimeMachine;
import fintech.Validate;
import fintech.cms.PdfRenderer;
import fintech.crm.attachments.AttachmentConstants;
import fintech.crm.attachments.ClientAttachmentService;
import fintech.crm.client.Client;
import fintech.crm.client.ClientService;
import fintech.dc.DcService;
import fintech.filestorage.CloudFile;
import fintech.filestorage.FileStorageService;
import fintech.filestorage.SaveFileCommand;
import fintech.instantor.InstantorService;
import fintech.instantor.model.InstantorResponseQuery;
import fintech.instantor.model.InstantorResponseStatus;
import fintech.lending.core.application.LoanApplicationQuery;
import fintech.lending.core.application.LoanApplicationService;
import fintech.lending.core.loan.Loan;
import fintech.lending.core.loan.LoanQuery;
import fintech.lending.core.loan.LoanService;
import fintech.payments.DisbursementService;
import fintech.payments.model.DisbursementStatusDetail;
import fintech.spain.alfa.product.AlfaConstants;
import fintech.spain.alfa.product.cms.CmsSetup;
import fintech.spain.alfa.product.cms.AlfaCmsModels;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class DebtExportService {

    private final FileStorageService fileStorageService;
    private final ClientAttachmentService attachmentService;
    private final LoanService loanService;
    private final LoanApplicationService loanApplicationService;
    private final ClientService clientService;
    private final DisbursementService disbursementService;
    private final InstantorService instantorService;
    private final DcService dcService;
    private final AlfaCmsModels cmsModels;
    private final PdfRenderer pdfRenderer;

    public CloudFile export(Collection<Long> loanIds) throws IOException {
        Instant startedAt = Instant.now();
        log.info("Starting debt portfolio export");
        Path tmp = Files.createTempDirectory(UUID.randomUUID().toString());

        loanIds.parallelStream().forEach(loanId -> {
            Stopwatch stopwatch = Stopwatch.createStarted();
            prepareDocuments(tmp, loanId);
            log.info("Completed prepare documents for loan {} in {} ms", loanId, stopwatch.elapsed(TimeUnit.MILLISECONDS));
        });

        File archiveFile = zipFiles(tmp.toFile());
        try (InputStream inputStream = Files.newInputStream(archiveFile.toPath())) {

            SaveFileCommand saveFileCommand = new SaveFileCommand();
            saveFileCommand.setOriginalFileName(String.format("portfolio_%s.zip", TimeMachine.currentInstant().toEpochMilli()));
            saveFileCommand.setDirectory("debt_exports");
            saveFileCommand.setInputStream(inputStream);
            saveFileCommand.setContentType(MediaType.ZIP.type());
            return fileStorageService.save(saveFileCommand);

        } finally {
            log.info("Finished debt portfolio export in {}ms", Duration.between(startedAt, Instant.now()).toMillis());
            try (Stream<Path> paths = Files.walk(tmp)) {
                paths.sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
            }
        }
    }

    @SneakyThrows
    private void prepareDocuments(Path root, Long loanId) {
        Loan loan = loanService.getLoan(loanId);
        log.info("Processing loan {}", loanId);
        Client client = clientService.get(loan.getClientId());

        Path clientRoot = root.resolve(client.getNumber());
        Files.createDirectory(clientRoot);

//        loanAgreement(clientRoot, client.getId());
//        standardInformation(clientRoot, client.getId());
//        privacyPolicy(clientRoot, client.getId());
//        invoices(clientRoot, loan.getId());
////        loanSummary(clientRoot, client.getId());
////        loanCertificate(clientRoot, client.getId());
//        disbursementDetails(clientRoot, client.getId());
//        instantorResponse(clientRoot, client.getId());
        certificateOfDebt(clientRoot, loan.getId());
    }

    private void loanAgreement(Path path, Long clientId) {
        attachmentService.findLastAttachment(ClientAttachmentService.AttachmentQuery.byClient(clientId, AttachmentConstants.ATTACHMENT_TYPE_LOAN_AGREEMENT))
            .ifPresent(attachment -> fileStorageService.get(attachment.getFileId())
                .ifPresent(cloudFile -> writeFile(path.resolve(cloudFile.getOriginalFileName()), fileStorageService.readContents(attachment.getFileId(), IoUtils::copyToByteArray))));
    }

    private void standardInformation(Path path, Long clientId) {
        attachmentService.findLastAttachment(ClientAttachmentService.AttachmentQuery.byClient(clientId, AttachmentConstants.ATTACHMENT_TYPE_STANDARD_INFORMATION))
            .ifPresent(attachment -> fileStorageService.get(attachment.getFileId())
                .ifPresent(cloudFile -> writeFile(path.resolve(cloudFile.getOriginalFileName()), fileStorageService.readContents(attachment.getFileId(), IoUtils::copyToByteArray))));
    }

    private void privacyPolicy(Path path, Long clientId) {
        attachmentService.findLastAttachment(ClientAttachmentService.AttachmentQuery.byClient(clientId, AlfaConstants.ATTACHMENT_TYPE_PRIVACY_POLICY))
            .ifPresent(attachment -> fileStorageService.get(attachment.getFileId())
                .ifPresent(cloudFile -> writeFile(path.resolve(cloudFile.getOriginalFileName()), fileStorageService.readContents(attachment.getFileId(), IoUtils::copyToByteArray))));
    }

    private void invoices(Path path, Long loanId) {
        attachmentService.findAttachments(ClientAttachmentService.AttachmentQuery.byLoan(loanId, AlfaConstants.ATTACHMENT_TYPE_INVOICE))
            .forEach(attachment -> fileStorageService.get(attachment.getFileId())
                .ifPresent(cloudFile -> writeFile(path.resolve(cloudFile.getOriginalFileName()), fileStorageService.readContents(attachment.getFileId(), IoUtils::copyToByteArray))));
    }

    private void loanSummary(Path path, Long clientId) {
        Map<String, Object> context = new HashMap<>(cmsModels.clientContext(clientId));

        loanService.findLastLoan(LoanQuery.nonVoidedLoans(clientId))
            .ifPresent(loan -> context.putAll(cmsModels.loanContext(loan.getId())));

        loanApplicationService.findLatest(LoanApplicationQuery.byClientId(clientId))
            .ifPresent(application -> context.putAll(cmsModels.applicationContext(application.getId())));

        pdfRenderer.render(CmsSetup.LOAN_SUMMARY_PDF, context, AlfaConstants.LOCALE)
            .ifPresent(pdf -> writeFile(path.resolve(pdf.getName()), pdf.getContent()));
    }

    private void loanCertificate(Path path, Long clientId) {
        Map<String, Object> context = new HashMap<>(cmsModels.clientContext(clientId));

        loanService.findLastLoan(LoanQuery.nonVoidedLoans(clientId))
            .ifPresent(loan -> context.putAll(cmsModels.loanContext(loan.getId())));

        loanApplicationService.findLatest(LoanApplicationQuery.byClientId(clientId))
            .ifPresent(application -> context.putAll(cmsModels.applicationContext(application.getId())));

        pdfRenderer.render(CmsSetup.LOAN_CERTIFICATE_PDF, context, AlfaConstants.LOCALE)
            .ifPresent(pdf -> writeFile(path.resolve(pdf.getName()), pdf.getContent()));
    }

    private void disbursementDetails(Path path, Long clientId) {
        loanService.findLastLoan(LoanQuery.nonVoidedLoans(clientId))
            .ifPresent(loan -> disbursementService.findDisbursements(DisbursementService.DisbursementQuery.byLoan(loan.getId(), DisbursementStatusDetail.SETTLED))
                .forEach(disbursement -> pdfRenderer.render(CmsSetup.DISBURSEMENT_DETAILS_PDF, cmsModels.disbursementContext(disbursement.getId()), AlfaConstants.LOCALE)
                    .ifPresent(pdf -> writeFile(path.resolve(pdf.getName()), pdf.getContent()))));
    }

    private void instantorResponse(Path path, Long clientId) {
        instantorService.findLatest(InstantorResponseQuery.byClientIdAndResponseStatus(clientId, InstantorResponseStatus.OK))
            .ifPresent(response -> pdfRenderer.render(CmsSetup.INSTANTOR_RESPONSE_PDF, cmsModels.instantorContext(clientId, response.getId()), AlfaConstants.LOCALE)
                .ifPresent(pdf -> writeFile(path.resolve(pdf.getName()), pdf.getContent())));
    }

    private void certificateOfDebt(Path path, Long loanId) {
        dcService.findByLoanId(loanId)
            .ifPresent(debt -> pdfRenderer.render(CmsSetup.CERTIFICATE_OF_DEBT_PDF, cmsModels.debtContext(debt.getId()), AlfaConstants.LOCALE)
                .ifPresent(pdf -> writeFile(path.resolve(pdf.getName()), pdf.getContent())));
    }

    private File zipFiles(File file) throws IOException {
        Validate.isTrue(file.isDirectory(), "Input must be a directory");
        File[] files = file.listFiles();
        Path archive = Files.createFile(Paths.get(file.getAbsolutePath(), "export.zip"));

        try (SeekableByteChannel bc = Files.newByteChannel(archive, StandardOpenOption.WRITE);
             ZipArchiveOutputStream o = new ZipArchiveOutputStream(bc)) {
            o.setCreateUnicodeExtraFields(ZipArchiveOutputStream.UnicodeExtraFieldPolicy.ALWAYS);
            if (files != null) {
                for (File f : files) {
                    zip(o, f, null);
                }
            }
            o.finish();
        }
        return archive.toFile();
    }

    private void zip(ArchiveOutputStream o, File file, String subDir) throws IOException {
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                zip(o, f, getName(subDir, file));
            }
        } else {
            ArchiveEntry fileEntry = o.createArchiveEntry(file, getName(subDir, file));
            o.putArchiveEntry(fileEntry);
            try (InputStream is = Files.newInputStream(file.toPath())) {
                IOUtils.copy(is, o);
            }
            o.closeArchiveEntry();
        }
    }

    private String getName(String subDir, File file) {
        return subDir != null ? subDir + "/" + file.getName() : file.getName();
    }

    @SneakyThrows
    private void writeFile(Path path, byte[] data) {
        if (data == null) {
            log.info("No content for file {}. Skipping", path.toString());
            return;
        }
        log.info("Writing prepared document {}", path.getFileName().toString());
        Path file;
        try {
            file = Files.createFile(path);
        } catch (Exception e) {
            log.info("Broken file {}. Skipping", path.toString());
            return;
        }
        try (OutputStream os = Files.newOutputStream(file)) {
            IOUtils.copy(new ByteArrayInputStream(data), os);
        }
    }

}
