package fintech.spain.alfa.product.lending.certificate;

import fintech.Validate;
import fintech.cms.Pdf;
import fintech.cms.PdfRenderer;
import fintech.crm.attachments.AddAttachmentCommand;
import fintech.crm.attachments.ClientAttachmentService;
import fintech.filestorage.CloudFile;
import fintech.filestorage.FileStorageService;
import fintech.lending.core.loan.Loan;
import fintech.lending.core.loan.LoanService;
import fintech.spain.alfa.product.filestorage.FileStorageCommandFactory;
import fintech.spain.alfa.product.AlfaConstants;
import fintech.spain.alfa.product.cms.AlfaCmsModels;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static fintech.spain.alfa.product.AlfaConstants.ATTACHMENT_TYPE_OTHER;

@Service
@RequiredArgsConstructor
public class LoanCertificateServiceImpl implements LoanCertificateService {

    private final PdfRenderer pdfRenderer;
    private final FileStorageService fileStorageService;
    private final ClientAttachmentService clientAttachmentService;
    private final AlfaCmsModels cmsModels;
    private final LoanService loanService;

    @Override
    @Transactional
    public CloudFile generateCertificate(Long loanId, LoanCertificateType type) {
        Loan loan = loanService.getLoan(loanId);
        Validate.isTrue(type.getCondition().test(loan), "Can't generate certificate of type "
            + type + " for loanId" + loanId);

        Map<String, Object> context = cmsModels.loanContext(loanId);

        Pdf pdf = pdfRenderer.renderRequired(type.getTemplate(), context, AlfaConstants.LOCALE);
        CloudFile file = fileStorageService.save(FileStorageCommandFactory.fromPdf(AlfaConstants.FILE_DIRECTORY_CERTIFICATES, pdf));

        AddAttachmentCommand addAttachmentCommand = AddAttachmentCommand.builder()
            .loanId(loanId)
            .clientId(loan.getClientId())
            .applicationId(loan.getApplicationId())
            .fileId(file.getFileId())
            .name(file.getOriginalFileName())
            .attachmentType(ATTACHMENT_TYPE_OTHER)
            .build();

        clientAttachmentService.addAttachment(addAttachmentCommand);

        return file;
    }

}
