package fintech.spain.alfa.product.transaction;

import fintech.cms.Pdf;
import fintech.cms.PdfRenderer;
import fintech.crm.attachments.AddAttachmentCommand;
import fintech.crm.attachments.ClientAttachmentService;
import fintech.filestorage.CloudFile;
import fintech.filestorage.FileStorageService;
import fintech.filestorage.SaveFileCommand;
import fintech.lending.core.loan.Loan;
import fintech.lending.core.loan.LoanService;
import fintech.spain.alfa.product.extension.ExtensionService;
import fintech.spain.alfa.product.AlfaConstants;
import fintech.spain.alfa.product.cms.ClientRepaymentModel;
import fintech.spain.alfa.product.cms.AlfaCmsModels;
import fintech.spain.alfa.product.cms.CmsSetup;
import fintech.transactions.Transaction;
import fintech.transactions.TransactionEntry;
import fintech.transactions.TransactionEntryType;
import fintech.transactions.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.util.Optional;

import static fintech.BigDecimalUtils.amount;
import static fintech.BigDecimalUtils.isZero;

@Slf4j
@Component
@Transactional
public class InvoiceAttachmentGenerator {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private LoanService loanService;

    @Autowired
    private AlfaCmsModels cmsModels;

    @Autowired
    private PdfRenderer pdfRenderer;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private ClientAttachmentService clientAttachmentService;

    public Optional<Long> generate(Long transactionId) {
        Transaction transaction = transactionService.getTransaction(transactionId);
        if (transaction.isVoided() || isZero(transaction.getPrincipalPaid().add(transaction.getInterestPaid()).add(transaction.getPenaltyPaid()).add(transaction.getFeePaid()))) {
            return Optional.empty();
        }

        Loan loan = loanService.getLoan(transaction.getLoanId());
        return pdfRenderer.render(CmsSetup.INVOICE_PDF, cmsModels.clientRepaymentContext(loan.getId(), clientRepaymentModel(transaction)), AlfaConstants.LOCALE).map(pdf -> {
                CloudFile invoice = saveInvoice(transaction, loan, pdf);
                addClientAttachment(transaction, invoice);
                return invoice.getFileId();
            }
        );
    }

    private ClientRepaymentModel clientRepaymentModel(Transaction transaction) {
        BigDecimal extensionFeePaid = calculateFeeAmount(transaction, ExtensionService.EXTENSION_FEE_TYPE);
        BigDecimal prepaymentFeePaid = calculateFeeAmount(transaction, AlfaConstants.PREPAYMENT_FEE_SUB_TYPE);
        BigDecimal reschedulingFeePaid = transaction.getFeePaid().subtract(extensionFeePaid).subtract(prepaymentFeePaid);
        BigDecimal totalPaid = transaction.getPrincipalPaid().add(transaction.getInterestPaid())
            .add(transaction.getPenaltyPaid()).add(extensionFeePaid).add(prepaymentFeePaid).add(reschedulingFeePaid);
        // can't invoice principal, only the amount we profit from
        BigDecimal totalInvoiced = transaction.getPenaltyPaid().add(extensionFeePaid).add(prepaymentFeePaid).add(reschedulingFeePaid);

        boolean transactionOfMigratedLoan = isTransactionOfMigratedLoan(transaction);
        if (!transactionOfMigratedLoan) {
            // only for non-migrated loans we can invoice interest
            // migrated loans already had initial invoice sent with the interest amount and we can't double-invoice it again
            totalInvoiced = totalInvoiced.add(transaction.getInterestPaid());
        }
        ClientRepaymentModel model = new ClientRepaymentModel()
            .setPrincipalPaid(transaction.getPrincipalPaid())
            .setInterestPaid(transaction.getInterestPaid())
            .setPenaltyPaid(transaction.getPenaltyPaid())
            .setExtensionFeePaid(extensionFeePaid)
            .setPrepaymentFeePaid(prepaymentFeePaid)
            .setReschedulingFeePaid(reschedulingFeePaid)
            .setTotalInvoiced(totalInvoiced)
            .setTotalPaid(totalPaid)
            .setRepaymentDate(transaction.getValueDate())
            .setTransactionId(transaction.getId());
        log.info("Client repayment of transaction [{}] and loan [{}], migrated [{}], model: [{}]", transaction.getId(), transaction.getLoanId(), transactionOfMigratedLoan, model);
        return model;
    }

    private boolean isTransactionOfMigratedLoan(Transaction transaction) {
        if (transaction.getLoanId() != null) {
            Loan loan = loanService.getLoan(transaction.getLoanId());
            return StringUtils.containsIgnoreCase(loan.getCreatedBy(), "system:migration");
        }
        return false;
    }

    private BigDecimal calculateFeeAmount(Transaction transaction, String subType) {
        return transaction.getEntries().stream()
            .filter(entry -> entry.getType() == TransactionEntryType.FEE && StringUtils.equals(entry.getSubType(), subType))
            .map(TransactionEntry::getAmountPaid)
            .reduce(amount(0), BigDecimal::add);
    }

    private CloudFile saveInvoice(Transaction transaction, Loan loan, Pdf pdf) {
        SaveFileCommand command = new SaveFileCommand();
        command.setOriginalFileName(String.format("invoice-%s-%s.pdf", loan.getNumber(), transaction.getId()));
        command.setDirectory(AlfaConstants.FILE_DIRECTORY_INVOICES);
        command.setInputStream(new ByteArrayInputStream(pdf.getContent()));
        command.setContentType(SaveFileCommand.CONTENT_TYPE_PDF);

        return fileStorageService.save(command);
    }

    private Long addClientAttachment(Transaction transaction, CloudFile cloudFile) {
        AddAttachmentCommand commad = new AddAttachmentCommand();
        commad.setClientId(transaction.getClientId());
        commad.setLoanId(transaction.getLoanId());
        commad.setTransactionId(transaction.getId());
        commad.setAttachmentType(AlfaConstants.ATTACHMENT_TYPE_INVOICE);
        commad.setName(cloudFile.getOriginalFileName());
        commad.setFileId(cloudFile.getFileId());

        return clientAttachmentService.addAttachment(commad);
    }
}
