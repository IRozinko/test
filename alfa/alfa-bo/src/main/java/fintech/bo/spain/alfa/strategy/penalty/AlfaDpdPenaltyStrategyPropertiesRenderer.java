package fintech.bo.spain.alfa.strategy.penalty;

import fintech.bo.components.PropertyLayout;
import fintech.bo.components.api.ApiAccessor;
import fintech.bo.db.jooq.strategy.tables.records.CalculationStrategyRecord;
import fintech.bo.db.jooq.alfa.tables.records.AlfaDpdPenaltyStrategyPenaltyRecord;
import fintech.spain.alfa.strategy.CalculationType;
import fintech.spain.alfa.strategy.StrategyType;
import fintech.strategy.bo.StrategyPropertiesRenderer;
import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

import java.util.List;

import static fintech.bo.db.jooq.alfa.Tables.ALFA_DPD_PENALTY_STRATEGY;
import static fintech.bo.db.jooq.alfa.Tables.ALFA_DPD_PENALTY_STRATEGY_PENALTY;

@Component
public class AlfaDpdPenaltyStrategyPropertiesRenderer implements StrategyPropertiesRenderer {

    @Override
    public boolean canRender(CalculationStrategyRecord strategy) {
        return StrategyType.PENALTY.getType().equals(strategy.getStrategyType()) && CalculationType.AV.name().equals(strategy.getCalculationType());
    }

    @Override
    public com.vaadin.ui.Component render(CalculationStrategyRecord strategy) {
        List<AlfaDpdPenaltyStrategyPenaltyRecord> properties = ApiAccessor.gI().get(DSLContext.class).select(ALFA_DPD_PENALTY_STRATEGY_PENALTY.fields())
            .from(ALFA_DPD_PENALTY_STRATEGY_PENALTY)
            .join(ALFA_DPD_PENALTY_STRATEGY).on(ALFA_DPD_PENALTY_STRATEGY_PENALTY.DPD_PENALTY_STRATEGY_ID.eq(ALFA_DPD_PENALTY_STRATEGY.ID))
            .where(ALFA_DPD_PENALTY_STRATEGY.CALCULATION_STRATEGY_ID.eq(strategy.getId()))
            .orderBy(ALFA_DPD_PENALTY_STRATEGY_PENALTY.DAYS_FROM)
            .fetchInto(AlfaDpdPenaltyStrategyPenaltyRecord.class);

        PropertyLayout layout = new PropertyLayout();
        layout.setMargin(false);

        for (AlfaDpdPenaltyStrategyPenaltyRecord property : properties) {
            layout.addPercentage(String.format("From %d days overdue", property.getDaysFrom()), property.getPenaltyRate());
        }

        return layout;
    }
}
