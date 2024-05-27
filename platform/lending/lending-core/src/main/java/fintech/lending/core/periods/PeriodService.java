package fintech.lending.core.periods;

import fintech.lending.core.periods.commands.ClosePeriodCommand;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.time.LocalDate;

@Validated
public interface PeriodService {

    void closePeriod(@Valid ClosePeriodCommand command);

    void failPeriod(LocalDate periodDate, String resultLog);

    boolean isClosedOrClosing(LocalDate periodDate);

    boolean isOfStatus(LocalDate periodDate, PeriodStatus status);

    boolean isOfStatusDetail(LocalDate periodDate, PeriodStatusDetail statusDetail);

    LocalDate getCurrentPeriod();

    void generateNextOpenPeriods(LocalDate from, int count);

    void setup(LocalDate when);
}
