package fintech.bo.api.server;

import fintech.TimeMachine;
import fintech.bo.api.model.periods.ClosePeriodRequest;
import fintech.bo.api.model.permissions.BackofficePermissions;
import fintech.lending.core.periods.PeriodService;
import fintech.lending.core.periods.commands.ClosePeriodCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@RestController
public class PeriodApiController {

    @Autowired
    private PeriodService periodService;

    // Fixme: remove using ExecutorService - bad practice, unhandled errors, silent failures, etc...
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.PERIOD_CLOSE})
    @PostMapping("/api/bo/periods/close")
    public void closePeriod(@Valid @RequestBody ClosePeriodRequest request) {
        executorService.submit(() -> {
            periodService.closePeriod(ClosePeriodCommand.builder()
                .periodDate(request.getPeriodDate())
                .closeDate(TimeMachine.today())
                .build());
        });
    }

}
