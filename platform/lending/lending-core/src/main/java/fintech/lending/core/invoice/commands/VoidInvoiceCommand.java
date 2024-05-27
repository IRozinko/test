package fintech.lending.core.invoice.commands;

import fintech.TimeMachine;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class VoidInvoiceCommand {

    @NonNull
    private Long invoiceId;

    @NonNull
    private LocalDate voidDate = TimeMachine.today();

}
