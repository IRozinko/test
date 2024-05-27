package fintech.spain.dc.command;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@Data
@Accessors(chain = true)
public class ReschedulingPreviewCommand {

    private Long loanId;
    private int numberOfPayments;
    private LocalDate when;
}
