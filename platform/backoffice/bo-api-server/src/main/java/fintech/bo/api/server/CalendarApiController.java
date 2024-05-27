package fintech.bo.api.server;

import fintech.bo.api.model.calendar.BusinessDaysRequest;
import fintech.bo.api.model.calendar.BusinessDaysResponse;
import fintech.calendar.spi.BusinessCalendarService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@Slf4j
@RestController
public class CalendarApiController {

    private final BusinessCalendarService calendar;

    @Autowired
    public CalendarApiController(BusinessCalendarService calendar) {
        this.calendar = calendar;
    }

    @PostMapping("/api/bo/calendar/business-time/resolve")
    public BusinessDaysResponse calculate(@RequestBody BusinessDaysRequest r) {
        LocalDateTime businessTime = calendar.resolveBusinessTime(r.getOrigin(), r.getAmountToAdd(), r.getUnit());
        return new BusinessDaysResponse(businessTime);
    }
}
