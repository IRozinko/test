package fintech.lending.core.invoice;

import fintech.lending.core.invoice.commands.CloseInvoiceCommand;
import fintech.lending.core.invoice.commands.GeneratedInvoice;
import fintech.lending.core.invoice.commands.UpdateInvoiceCommand;
import lombok.NonNull;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Validated
public interface InvoiceService {

    Invoice get(@NonNull Long invoiceId);

    Optional<Invoice> findLastOpenInvoice(@NonNull Long loanId);

    Optional<Invoice> findFirstOpenInvoice(@NonNull Long loanId);

    List<Invoice> find(@Valid InvoiceQuery query);

    List<Invoice> findForMembershipLevel(Long clientId);

    Long createInvoice(@Valid GeneratedInvoice command);

    void updateInvoice(UpdateInvoiceCommand command);

    void invoiceFileGenerated(Long invoiceId, Long fileId, String fileName);

    void invoiceFileSent(Long invoiceId, LocalDateTime when);

    void generateFile(Long invoiceId);

    void sendFile(Long invoiceId);

    void closeInvoice(@Valid CloseInvoiceCommand command);

    void markMembershipLevelChanged(Long invoiceId);
}
