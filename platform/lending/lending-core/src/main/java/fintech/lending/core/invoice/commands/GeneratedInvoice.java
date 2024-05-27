package fintech.lending.core.invoice.commands;

import fintech.lending.core.invoice.db.InvoiceItemType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GeneratedInvoice {

    @NotNull
    private LocalDate invoiceDate;

    @NotNull
    private LocalDate periodFrom;

    @NotNull
    private LocalDate periodTo;

    @NotNull
    private String number;

    @NotNull
    private LocalDate dueDate;

    @NotNull
    private Long loanId;

    @NotNull
    private Long productId;

    @NotNull
    private Long clientId;

    @NotNull
    private List<GeneratedInvoiceItem> items = newArrayList();

    @NotNull
    private boolean generateFile;

    @NotNull
    private boolean sendFile;

    private Boolean membershipLevelChecked;

    private boolean manual;

    @Data
    @Accessors(chain = true)
    public static class GeneratedInvoiceItem {
        private InvoiceItemType type;
        private String subType;
        private BigDecimal amount;
        private boolean correction = false;
    }

}
