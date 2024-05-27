package fintech.bo.spain.alfa.strategy.interest;

import fintech.bo.components.PropertyLayout;
import fintech.bo.components.api.ApiAccessor;
import fintech.bo.db.jooq.strategy.tables.records.CalculationStrategyRecord;
import fintech.bo.spain.alfa.db.jooq.alfa.Tables;
import fintech.bo.spain.alfa.db.jooq.alfa.tables.records.AlfaMonthlyInterestStrategyRecord;
import fintech.spain.alfa.strategy.CalculationType;
import fintech.spain.alfa.strategy.StrategyType;
import fintech.strategy.bo.StrategyPropertiesRenderer;
import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

@Component
public class AlfaInterestStrategyPropertiesRenderer implements StrategyPropertiesRenderer {
    @Override
    public boolean canRender(CalculationStrategyRecord strategy) {
        return StrategyType.INTEREST.getType().equals(strategy.getStrategyType()) && CalculationType.X.name().equals(strategy.getCalculationType());
    }

    @Override
    public com.vaadin.ui.Component render(CalculationStrategyRecord strategy) {
        AlfaMonthlyInterestStrategyRecord property = ApiAccessor.gI().get(DSLContext.class)
            .selectFrom(Tables.ALFA_MONTHLY_INTEREST_STRATEGY)
            .where(Tables.ALFA_MONTHLY_INTEREST_STRATEGY.CALCULATION_STRATEGY_ID.eq(strategy.getId()))
            .fetchOne();

        PropertyLayout properties = new PropertyLayout();
        properties.setMargin(false);
        properties.add("Monthly interest rate (%)", property.getInterestRate());
        properties.add("Use Decision Engine", property.getUsingDecisionEngine());
        properties.add("Decision Engine scenario key", property.getScenario());

        return properties;
    }
}
