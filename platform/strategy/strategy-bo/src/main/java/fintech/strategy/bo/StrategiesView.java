package fintech.strategy.bo;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import fintech.bo.api.model.permissions.BackofficePermissions;
import fintech.bo.components.api.ApiAccessor;
import fintech.bo.components.layouts.GridViewLayout;
import fintech.bo.components.security.SecuredView;
import fintech.bo.db.jooq.strategy.Tables;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.List;

@Slf4j
@SecuredView({BackofficePermissions.ADMIN, BackofficePermissions.STRATEGIES_VIEW})
@SpringView(name = StrategiesView.NAME)
public class StrategiesView extends VerticalLayout implements View {

    public static final String NAME = "strategies";

    @Autowired
    private StrategiesComponents strategiesComponents;

    private Grid<Record> grid;
    private StrategyDataProvider dataProvider;
    private ComboBox<String> calculationType;
    private ComboBox<String> strategyType;

    @PostConstruct
    public void init() {
        dataProvider = strategiesComponents.strategyDataProvider();
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        removeAllComponents();
        setCaption("Strategies");
        GridViewLayout layout = new GridViewLayout();
        buildTop(layout);
        buildGrid(layout);
        addComponentsAndExpand(layout);

        refreshFilters();
    }

    private void buildTop(GridViewLayout layout) {
        Button newButton = new Button("New");
        newButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        newButton.addClickListener(c -> openCreateDialog());

        strategyType = strategiesComponents.typeComboBox();
        strategyType.addValueChangeListener(event -> refresh());

        calculationType = strategiesComponents.strategyComboBox();
        calculationType.addValueChangeListener(event -> refresh());

        layout.setRefreshAction((e) -> {
            refreshFilters();
            refresh();
        });

        layout.addTopComponent(newButton);
        layout.addTopComponent(strategyType);
        layout.addTopComponent(calculationType);
    }

    private void openCreateDialog() {
        NewStrategyDialog dialog = new NewStrategyDialog();
        dialog.addCloseListener(e -> {
            refreshFilters();
            refresh();
        });
        getUI().addWindow(dialog);
    }

    private void buildGrid(GridViewLayout layout) {
        grid = strategiesComponents.strategiesGrid(dataProvider);
        layout.setContent(grid);
    }

    private void refreshFilters() {
        List<String> availableStrategyTypes = ApiAccessor.gI().get(DSLContext.class).selectDistinct(Tables.CALCULATION_STRATEGY.STRATEGY_TYPE)
            .from(Tables.CALCULATION_STRATEGY)
            .fetchInto(String.class);
        strategyType.setValue(null);
        strategyType.setItems(availableStrategyTypes);

        List<String> availableCalculationTypes = ApiAccessor.gI().get(DSLContext.class).selectDistinct(Tables.CALCULATION_STRATEGY.CALCULATION_TYPE)
            .from(Tables.CALCULATION_STRATEGY)
            .fetchInto(String.class);
        calculationType.setValue(null);
        calculationType.setItems(availableCalculationTypes);
    }

    private void refresh() {
        dataProvider.setStrategyType(strategyType.getValue());
        dataProvider.setCalculationType(calculationType.getValue());
        grid.getDataProvider().refreshAll();
    }
}
