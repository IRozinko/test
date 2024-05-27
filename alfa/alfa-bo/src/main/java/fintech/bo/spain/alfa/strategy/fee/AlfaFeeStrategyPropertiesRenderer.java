package fintech.bo.spain.alfa.strategy.fee;

import fintech.bo.components.PropertyLayout;
import fintech.bo.components.api.ApiAccessor;
import fintech.bo.db.jooq.strategy.tables.records.CalculationStrategyRecord;
import fintech.spain.alfa.strategy.CalculationType;
import fintech.spain.alfa.strategy.StrategyType;
import fintech.spain.alfa.strategy.fee.FeeStrategyProperties;
import fintech.strategy.bo.StrategyPropertiesRenderer;
import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

import java.util.Comparator;

import static fintech.bo.db.jooq.alfa.Tables.ALFA_FEE_STRATEGY;

@Component
public class AlfaFeeStrategyPropertiesRenderer implements StrategyPropertiesRenderer {
    @Override
    public boolean canRender(CalculationStrategyRecord strategy) {
        return StrategyType.FEE.getType().equals(strategy.getStrategyType()) && CalculationType.FT.name().equals(strategy.getCalculationType());
    }

    @Override
    public com.vaadin.ui.Component render(CalculationStrategyRecord strategy) {
        PropertyLayout properties = new PropertyLayout();
        properties.setMargin(false);

        ApiAccessor.gI().get(DSLContext.class)
            .selectFrom(ALFA_FEE_STRATEGY)
            .where(ALFA_FEE_STRATEGY.CALCULATION_STRATEGY_ID.eq(strategy.getId()))
            .fetch()
            .stream()
            .map(r -> new FeeStrategyProperties.FeeOption()
                .setCompany(r.getCompany())
                .setOneTimeFeeRate(r.getFeeRate()))
            .sorted(Comparator.comparing(FeeStrategyProperties.FeeOption::getCompany))
            .forEach(e -> {
                properties.add(String.format("%s company", e.getCompany()), String.format("%.2f%% fee rate", e.getOneTimeFeeRate().doubleValue()));
            });

        return properties;

    }
}
