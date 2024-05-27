package fintech.spain.dc.command;

import fintech.spain.dc.model.ReschedulingPreview;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@Data
@Accessors(chain = true)
public class RescheduleCommand {

    private Long loanId;
    private LocalDate when;
    private ReschedulingPreview preview;
}
