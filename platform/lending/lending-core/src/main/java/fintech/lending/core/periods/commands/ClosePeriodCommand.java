package fintech.lending.core.periods.commands;

import fintech.TimeMachine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClosePeriodCommand {

    @NotNull
    private LocalDate periodDate;

    @NotNull
    private LocalDate closeDate = TimeMachine.today();

}
