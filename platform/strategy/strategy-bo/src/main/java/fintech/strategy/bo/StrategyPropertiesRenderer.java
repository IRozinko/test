package fintech.strategy.bo;

import com.vaadin.ui.Component;
import fintech.bo.db.jooq.strategy.tables.records.CalculationStrategyRecord;

public interface StrategyPropertiesRenderer {
    boolean canRender(CalculationStrategyRecord strategy);

    Component render(CalculationStrategyRecord strategy);
}
