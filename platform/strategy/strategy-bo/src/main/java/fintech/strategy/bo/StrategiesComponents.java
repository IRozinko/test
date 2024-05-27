package fintech.strategy.bo;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import fintech.bo.components.JooqGridBuilder;
import fintech.bo.components.PropertyLayout;
import fintech.bo.db.jooq.strategy.tables.records.CalculationStrategyRecord;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static fintech.bo.db.jooq.strategy.Tables.CALCULATION_STRATEGY;
import static fintech.bo.db.jooq.task.Task.TASK;

@Component
public class StrategiesComponents {

    @Autowired
    private DSLContext db;

    public StrategyDataProvider strategyDataProvider() {
        return new StrategyDataProvider(db);
    }

    public Grid<Record> strategiesGrid(StrategyDataProvider dataProvider) {
        JooqGridBuilder<Record> builder = new JooqGridBuilder<>();
        builder.addNavigationColumn("Open", r -> "strategy/" + r.get(CALCULATION_STRATEGY.ID));
        builder.addColumn(CALCULATION_STRATEGY.ID);
        builder.addColumn(r -> r.get(CALCULATION_STRATEGY.STRATEGY_TYPE) + r.get(CALCULATION_STRATEGY.CALCULATION_TYPE) + r.get(CALCULATION_STRATEGY.VERSION)).setCaption("Name").setWidth(200);
        builder.addColumn(CALCULATION_STRATEGY.STRATEGY_TYPE);
        builder.addColumn(CALCULATION_STRATEGY.CALCULATION_TYPE);
        builder.addColumn(CALCULATION_STRATEGY.VERSION);
        builder.addColumn(CALCULATION_STRATEGY.ENABLED).setWidth(100);
        builder.addColumn(CALCULATION_STRATEGY.IS_DEFAULT).setWidth(100);
        builder.addAuditColumns(TASK.TASK_);
        builder.sortDesc(TASK.TASK_.CREATED_AT);
        return builder.build(dataProvider);
    }

    public ComboBox<String> typeComboBox() {
        ComboBox<String> comboBox = new ComboBox<>("Strategy type");
        comboBox.setPlaceholder("Strategy type");
        comboBox.setTextInputAllowed(false);
        return comboBox;
    }

    public ComboBox<String> strategyComboBox() {
        ComboBox<String> comboBox = new ComboBox<>("Calculation type");
        comboBox.setPlaceholder("Calculation type");
        comboBox.setTextInputAllowed(false);
        return comboBox;
    }

    public PropertyLayout strategyInfo(CalculationStrategyRecord strategy) {
        PropertyLayout layout = new PropertyLayout("Strategy");
        layout.add("Name", strategy.getStrategyType() + strategy.getCalculationType() + strategy.getVersion());
        layout.add("Strategy Type", strategy.getStrategyType());
        layout.add("Calculation Type", strategy.getCalculationType());
        layout.add("Version", strategy.getVersion());
        layout.add("Enabled", strategy.getEnabled());
        layout.add("Is Default", strategy.getIsDefault());
        layout.addSpacer();
        layout.add("Created At", strategy.getCreatedAt());
        layout.add("Created By", strategy.getCreatedBy());
        layout.add("Updated At", strategy.getUpdatedAt());
        layout.add("Updated By", strategy.getUpdatedBy());
        return layout;
    }
}
