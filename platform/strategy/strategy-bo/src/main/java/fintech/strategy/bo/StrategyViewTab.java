package fintech.strategy.bo;

import com.vaadin.ui.Component;
import fintech.bo.db.jooq.strategy.tables.records.CalculationStrategyRecord;

public interface StrategyViewTab {
    String getCaption();

    Component component(CalculationStrategyRecord strategy);
}
