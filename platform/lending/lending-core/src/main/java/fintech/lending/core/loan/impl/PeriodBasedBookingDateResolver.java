package fintech.lending.core.loan.impl;

import fintech.lending.core.periods.PeriodService;
import fintech.lending.core.periods.PeriodStatus;
import fintech.transactions.spi.BookingDateResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
class PeriodBasedBookingDateResolver implements BookingDateResolver {

    @Autowired
    private PeriodService periodService;

    @Override
    public LocalDate get(LocalDate valueDate) {
        return periodService.isOfStatus(valueDate, PeriodStatus.CLOSED) ?
            periodService.getCurrentPeriod() : valueDate;
    }

}
