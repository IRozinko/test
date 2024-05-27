package fintech.bo.spain.alfa.strategy.penalty;

import com.vaadin.data.Binder;
import com.vaadin.data.ValueProvider;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.components.grid.Editor;
import fintech.BigDecimalUtils;
import fintech.bo.components.BackofficeTheme;
import fintech.bo.components.Converters;
import fintech.bo.components.api.ApiAccessor;
import fintech.bo.spain.alfa.strategy.CmsItemNotificationLabel;
import fintech.spain.alfa.strategy.CalculationType;
import fintech.spain.alfa.strategy.StrategyType;
import fintech.spain.alfa.strategy.penalty.DpdPenaltyStrategyProperties;
import fintech.strategy.bo.StrategyForm;
import fintech.strategy.bo.StrategyFormRenderer;
import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

import static fintech.bo.db.jooq.alfa.Tables.ALFA_DPD_PENALTY_STRATEGY;
import static fintech.bo.db.jooq.alfa.Tables.ALFA_DPD_PENALTY_STRATEGY_PENALTY;

@Component
public class AlfaDpdPenaltyStrategyFormRenderer implements StrategyFormRenderer {

    @Override
    public boolean canRender(String strategyType, String calculationType) {
        return StrategyType.PENALTY.getType().equals(strategyType) && CalculationType.AV.name().equals(calculationType);
    }

    @Override
    public StrategyForm renderNew() {
        return new Form();
    }

    @Override
    public StrategyForm renderForStrategy(Long calculationStrategyId) {
        List<DpdPenaltyStrategyProperties.PenaltyStrategy> strategies = ApiAccessor.gI().get(DSLContext.class)
            .select(ALFA_DPD_PENALTY_STRATEGY_PENALTY.fields())
            .from(ALFA_DPD_PENALTY_STRATEGY_PENALTY)
            .join(ALFA_DPD_PENALTY_STRATEGY).on(ALFA_DPD_PENALTY_STRATEGY.ID.eq(ALFA_DPD_PENALTY_STRATEGY_PENALTY.DPD_PENALTY_STRATEGY_ID))
            .where(ALFA_DPD_PENALTY_STRATEGY.CALCULATION_STRATEGY_ID.eq(calculationStrategyId))
            .orderBy(ALFA_DPD_PENALTY_STRATEGY_PENALTY.DAYS_FROM)
            .fetch(record -> {
                DpdPenaltyStrategyProperties.PenaltyStrategy penaltyStrategy = new DpdPenaltyStrategyProperties.PenaltyStrategy();
                penaltyStrategy.setFrom(record.get(ALFA_DPD_PENALTY_STRATEGY_PENALTY.DAYS_FROM));
                penaltyStrategy.setRate(record.get(ALFA_DPD_PENALTY_STRATEGY_PENALTY.PENALTY_RATE));
                return penaltyStrategy;
            });
        return new Form(new DpdPenaltyStrategyProperties().setStrategies(strategies));
    }

    private static class Form extends StrategyForm {

        private final DpdPenaltyStrategyProperties properties;

        Form(DpdPenaltyStrategyProperties properties) {
            this.properties = properties;

            VerticalLayout root = new VerticalLayout();
            root.setMargin(false);

            new CmsItemNotificationLabel(StrategyType.PENALTY.getType(), CalculationType.AV.name())
                .get()
                .ifPresent(root::addComponent);

            Grid<DpdPenaltyStrategyProperties.PenaltyStrategy> grid = new Grid<>();

            ListDataProvider<DpdPenaltyStrategyProperties.PenaltyStrategy> dataProvider = new ListDataProvider<>(properties.getStrategies());
            grid.setDataProvider(dataProvider);

            grid.addComponentColumn((ValueProvider<DpdPenaltyStrategyProperties.PenaltyStrategy, com.vaadin.ui.Component>) penaltyStrategy -> {
                Button deleteButton = new Button("Remove");
                deleteButton.addClickListener(event -> {
                    properties.getStrategies().remove(penaltyStrategy);
                    dataProvider.refreshAll();
                });
                return deleteButton;
            }).setWidth(80).setStyleGenerator(item -> BackofficeTheme.ACTION_ROW).setSortable(false);

            TextField fromField = new TextField("From");
            TextField rateField = new TextField("Rate");

            Editor<DpdPenaltyStrategyProperties.PenaltyStrategy> editor = grid.getEditor();
            Binder<DpdPenaltyStrategyProperties.PenaltyStrategy> binder = editor.getBinder();

            Binder.Binding<DpdPenaltyStrategyProperties.PenaltyStrategy, Integer> fromFieldBinding = binder.forField(fromField)
                .withNullRepresentation("")
                .withConverter(new StringToIntegerConverter("Invalid number"))
                .withValidator(v -> v >= 0, "Invalid value")
                .bind(DpdPenaltyStrategyProperties.PenaltyStrategy::getFrom, DpdPenaltyStrategyProperties.PenaltyStrategy::setFrom);

            grid.addColumn(DpdPenaltyStrategyProperties.PenaltyStrategy::getFrom)
                .setEditorBinding(fromFieldBinding)
                .setCaption("From");

            Binder.Binding<DpdPenaltyStrategyProperties.PenaltyStrategy, BigDecimal> rateFieldBinding = binder.forField(rateField)
                .withNullRepresentation("")
                .withConverter(Converters.stringToBigDecimalInputConverter())
                .withValidator(BigDecimalUtils::isPositive, "Invalid value")
                .bind(DpdPenaltyStrategyProperties.PenaltyStrategy::getRate, DpdPenaltyStrategyProperties.PenaltyStrategy::setRate);

            grid.addColumn(DpdPenaltyStrategyProperties.PenaltyStrategy::getRate)
                .setEditorBinding(rateFieldBinding)
                .setCaption("Rate");

            grid.setSelectionMode(Grid.SelectionMode.NONE);
            grid.setWidth(305, Unit.PIXELS);
            grid.setHeightByRows(4);

            editor.setEnabled(true);
            editor.addSaveListener(event -> dataProvider.refreshAll());

            root.addComponent(grid);
            root.addComponent(new Button("Add", event -> {
                DpdPenaltyStrategyProperties.PenaltyStrategy newItem = new DpdPenaltyStrategyProperties.PenaltyStrategy();
                properties.getStrategies().add(newItem);
                dataProvider.refreshAll();
                grid.recalculateColumnWidths();
            }));
            root.setHeight(100, Unit.PERCENTAGE);

            setCompositionRoot(root);
        }

        Form() {
            this(new DpdPenaltyStrategyProperties());
        }

        @Override
        public Object getStrategyProperties() {
            return properties;
        }
    }
}
