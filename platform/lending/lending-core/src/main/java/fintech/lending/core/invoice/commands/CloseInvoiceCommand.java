package fintech.lending.core.invoice.commands;

import fintech.TimeMachine;
import fintech.lending.core.invoice.InvoiceStatusDetail;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CloseInvoiceCommand {

    @NonNull
    private Long invoiceId;

    @NonNull
    private LocalDate date = TimeMachine.today();

    private InvoiceStatusDetail statusDetail;

    private String reason;

}
