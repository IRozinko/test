package fintech.lending.core.periods.impl;

import fintech.lending.core.db.Entities;
import fintech.lending.core.periods.PeriodService;
import fintech.lending.core.periods.PeriodStatus;
import fintech.lending.core.periods.PeriodStatusDetail;
import fintech.lending.core.periods.db.PeriodEntity;
import fintech.lending.core.periods.db.PeriodRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class InterruptedPeriodRecovery {

    @Autowired
    private PeriodRepository periodRepository;

    @Autowired
    private PeriodService periodService;

    public void recover() {
        List<PeriodEntity> periods = periodRepository.findAll(Entities.period.status.eq(PeriodStatus.OPEN).and(Entities.period.statusDetail.eq(PeriodStatusDetail.CLOSING)));

        if (periods.isEmpty()) {
            return;
        }

        log.info("Found {} interrupted periods to recover", periods.size());

        periods.forEach(period -> periodService.failPeriod(period.getPeriodDate(), "Interrupted period recovery"));
    }
}
