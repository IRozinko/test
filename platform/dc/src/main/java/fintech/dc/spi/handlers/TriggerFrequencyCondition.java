package fintech.dc.spi.handlers;

import com.querydsl.jpa.JPQLQueryFactory;
import fintech.TimeMachine;
import fintech.dc.db.Entities;
import fintech.dc.spi.ConditionContext;
import fintech.dc.spi.ConditionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
public class TriggerFrequencyCondition implements ConditionHandler {


    @Autowired
    JPQLQueryFactory queryFactory;

    @Override
    public boolean apply(ConditionContext context) {
        Integer doNotRepeatForDays = context.getRequiredParam("doNotRepeatForDays", Integer.class);

        LocalDateTime lastExecuted = queryFactory.select(Entities.action.createdAt.max()).from(Entities.action)
            .where(
                Entities.action.debt.id.eq(context.getDebt().getId())
                    .and(Entities.action.actionName.eq(context.getTrigger().getName()))
            ).fetchOne();
        if (lastExecuted == null) {
            return true;
        }
        long daysSinceExecuted = ChronoUnit.DAYS.between(lastExecuted, TimeMachine.now());
        return daysSinceExecuted >= doNotRepeatForDays;
    }
}
