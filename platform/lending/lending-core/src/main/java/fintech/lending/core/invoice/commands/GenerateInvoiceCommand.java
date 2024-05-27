package fintech.lending.core.invoice.commands;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GenerateInvoiceCommand {

    @NotNull
    private Long loanId;

    @NotNull
    private LocalDate dateTo;

    @NotNull
    private LocalDate invoiceDate;

    @NotNull
    private LocalDate dueDate;

    @NotNull
    private boolean generateFile;

    @NotNull
    private boolean sendFile;

    private Boolean membershipLevelChecked;

    private boolean manual;
}
