package fintech.payments.impl;

import fintech.TimeMachine;
import fintech.crm.attachments.AddAttachmentCommand;
import fintech.crm.attachments.ClientAttachmentService;
import fintech.filestorage.CloudFile;
import fintech.filestorage.FileStorageService;
import fintech.filestorage.SaveFileCommand;
import fintech.payments.DisbursementService;
import fintech.payments.PaymentService;
import fintech.payments.PaymentService.PaymentQuery;
import fintech.payments.model.PaymentType;
import fintech.spain.unnax.event.BankStatementReceivedEvent;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import static fintech.crm.attachments.AttachmentConstants.ATTACHMENT_TYPE_BANK_STATEMENT;
import static fintech.payments.DisbursementService.DisbursementQuery.byReference;

@Component
@Slf4j
public class PaymentBankStatementsService {

    @Autowired
    private DisbursementService disbursementService;
    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private ClientAttachmentService clientAttachmentService;
    @Autowired
    private PaymentService paymentService;

    @SneakyThrows
    @EventListener
    public void handleBankStatementReceivedEvent(BankStatementReceivedEvent event) {
        paymentService.getOptional(new PaymentQuery(PaymentType.OUTGOING, event.getStatementId().toString()))
            .ifPresent(p -> disbursementService.getOptional(byReference(p.getDetails()))
                .ifPresent(disbursement -> {
                    long clientId = disbursement.getClientId();
                    CloudFile cloudFile = saveBankStatement(event.getStatementId(), event.getRawPdf());
                    addClientAttachment(clientId, disbursement.getLoanId(), cloudFile);
                }));
    }

    public CloudFile exportBankStatements(List<Long> loanIds) {
        List<Long> attachmentIds = new ArrayList<>();
        for (Long loanId : loanIds) {
            clientAttachmentService.findAttachments(ClientAttachmentService.AttachmentQuery.byLoan(loanId, ATTACHMENT_TYPE_BANK_STATEMENT)).stream()
                .findFirst()
                .ifPresent(attachment -> attachmentIds.add(attachment.getFileId()));
        }
        return clientAttachmentService.exportToZipArchive(attachmentIds, String.format("bank_statements_%s.zip", TimeMachine.now()));
    }

    private CloudFile saveBankStatement(Long disbursementId, byte[] content) {
        SaveFileCommand command = new SaveFileCommand();
        command.setOriginalFileName(String.format("disbursement-%s.pdf", disbursementId));
        command.setDirectory("bank_statements");
        command.setInputStream(new ByteArrayInputStream(content));
        command.setContentType(SaveFileCommand.CONTENT_TYPE_PDF);
        return fileStorageService.save(command);
    }


    private Long addClientAttachment(Long clientId, Long loanId, CloudFile cloudFile) {
        AddAttachmentCommand commad = new AddAttachmentCommand();
        commad.setClientId(clientId);
        commad.setLoanId(loanId);
        commad.setAttachmentType("");
        commad.setAttachmentType(ATTACHMENT_TYPE_BANK_STATEMENT);
        commad.setName(cloudFile.getOriginalFileName());
        commad.setFileId(cloudFile.getFileId());
        return clientAttachmentService.addAttachment(commad);
    }


}
