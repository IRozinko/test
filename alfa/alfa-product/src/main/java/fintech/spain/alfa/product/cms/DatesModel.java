package fintech.spain.alfa.product.cms;

import fintech.TimeMachine;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DatesModel {

    public LocalDate getToday() {
        return TimeMachine.today();
    }

    public LocalDate getTomorrow() {
        return TimeMachine.today().plusDays(1);
    }

    public LocalDateTime getNow() {
        return TimeMachine.now();
    }
}
