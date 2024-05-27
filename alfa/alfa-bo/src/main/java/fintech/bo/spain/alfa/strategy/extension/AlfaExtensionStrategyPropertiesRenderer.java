package fintech.bo.spain.alfa.strategy.extension;

import fintech.bo.components.PropertyLayout;
import fintech.bo.components.api.ApiAccessor;
import fintech.bo.db.jooq.strategy.tables.records.CalculationStrategyRecord;
import fintech.spain.alfa.strategy.CalculationType;
import fintech.spain.alfa.strategy.StrategyType;
import fintech.spain.alfa.strategy.extension.ExtensionStrategyProperties;
import fintech.strategy.bo.StrategyPropertiesRenderer;
import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

import java.util.Comparator;

import static fintech.bo.spain.alfa.db.jooq.alfa.Tables.ALFA_EXTENSION_STRATEGY;

@Component
public class AlfaExtensionStrategyPropertiesRenderer implements StrategyPropertiesRenderer {
    @Override
    public boolean canRender(CalculationStrategyRecord strategy) {
        return StrategyType.EXTENSION.getType().equals(strategy.getStrategyType()) && CalculationType.D.name().equals(strategy.getCalculationType());
    }

    @Override
    public com.vaadin.ui.Component render(CalculationStrategyRecord strategy) {
        PropertyLayout properties = new PropertyLayout();
        properties.setMargin(false);

        ApiAccessor.gI().get(DSLContext.class)
            .selectFrom(ALFA_EXTENSION_STRATEGY)
            .where(ALFA_EXTENSION_STRATEGY.CALCULATION_STRATEGY_ID.eq(strategy.getId()))
            .fetch()
            .stream()
            .map(r -> new ExtensionStrategyProperties.ExtensionOption()
                .setRate(r.getRate())
                .setTerm(r.getTerm()))
            .sorted(Comparator.comparing(ExtensionStrategyProperties.ExtensionOption::getTerm))
            .forEach(e -> {
                properties.add(String.format("%d day(s)", e.getTerm()), String.format("%.2f%% of principal", e.getRate().doubleValue()));
            });

        return properties;
    }
}
