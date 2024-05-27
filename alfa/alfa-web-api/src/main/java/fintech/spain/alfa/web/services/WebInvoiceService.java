package fintech.spain.alfa.web.services;

import fintech.FileHashId;
import fintech.crm.attachments.Attachment;
import fintech.crm.attachments.ClientAttachmentService;
import fintech.lending.core.loan.LoanService;
import fintech.spain.alfa.web.models.InvoiceInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static fintech.spain.alfa.product.AlfaConstants.ATTACHMENT_TYPE_INVOICE;

@Component
public class WebInvoiceService {

    @Autowired
    private ClientAttachmentService clientAttachmentService;

    @Autowired
    private LoanService loanService;

    public List<InvoiceInfo> listInvoices(Long clientId) {
        List<Attachment> attachments = clientAttachmentService.findAttachments(ClientAttachmentService.AttachmentQuery.byClient(clientId, ATTACHMENT_TYPE_INVOICE));
        return attachments.stream()
            .map(attachment -> {
                String number = loanService.getLoan(attachment.getLoanId()).getNumber() + "-" + attachment.getFileId();
                return new InvoiceInfo()
                    .setId(attachment.getId())
                    .setNumber(number)
                    .setInvoiceDate(attachment.getCreatedAt())
                    .setFileId(FileHashId.encodeFileId(attachment.getClientId(), attachment.getFileId()));
            })
            .collect(Collectors.toList());
    }

}
