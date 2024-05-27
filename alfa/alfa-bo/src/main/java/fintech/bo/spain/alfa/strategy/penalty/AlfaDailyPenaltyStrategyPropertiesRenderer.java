package fintech.bo.spain.alfa.strategy.penalty;

import fintech.bo.components.PropertyLayout;
import fintech.bo.components.api.ApiAccessor;
import fintech.bo.db.jooq.strategy.tables.records.CalculationStrategyRecord;
import fintech.bo.spain.alfa.db.jooq.alfa.tables.records.AlfaDailyPenaltyStrategyRecord;
import fintech.spain.alfa.strategy.CalculationType;
import fintech.spain.alfa.strategy.StrategyType;
import fintech.strategy.bo.StrategyPropertiesRenderer;
import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

import static fintech.bo.spain.alfa.db.jooq.alfa.Tables.ALFA_DAILY_PENALTY_STRATEGY;

@Component
public class AlfaDailyPenaltyStrategyPropertiesRenderer implements StrategyPropertiesRenderer {
    @Override
    public boolean canRender(CalculationStrategyRecord strategy) {
        return StrategyType.PENALTY.getType().equals(strategy.getStrategyType()) && CalculationType.A.name().equals(strategy.getCalculationType());
    }

    @Override
    public com.vaadin.ui.Component render(CalculationStrategyRecord strategy) {
        AlfaDailyPenaltyStrategyRecord property = ApiAccessor.gI().get(DSLContext.class).selectFrom(ALFA_DAILY_PENALTY_STRATEGY)
            .where(ALFA_DAILY_PENALTY_STRATEGY.CALCULATION_STRATEGY_ID.eq(strategy.getId()))
            .fetchOne();

        PropertyLayout properties = new PropertyLayout();
        properties.setMargin(false);
        properties.add("Daily penalty rate (%)", property.getPenaltyRate());

        return properties;
    }
}
